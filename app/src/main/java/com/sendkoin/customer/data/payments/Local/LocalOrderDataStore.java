package com.sendkoin.customer.data.payments.Local;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemLocal;
import com.sendkoin.sql.entities.CurrentOrderEntity;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;
import com.sendkoin.sql.tables.CurrentOrderTable;
import com.sendkoin.sql.tables.InventoryOrderItemTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Read/Write the customers CURRENT order only
 */

public class LocalOrderDataStore {

  private StorIOSQLite storIOSQLite;

  @Inject
  public LocalOrderDataStore(StorIOSQLite storIOSQLite) {

    this.storIOSQLite = storIOSQLite;
  }

  private Observable<PutResult> saveOrderItem(InventoryOrderItemEntity inventoryOrderItemEntity) {
    return storIOSQLite.put()
        .object(inventoryOrderItemEntity)
        .prepare()
        .asRxObservable();
  }

  private Observable<PutResult> saveOrder(CurrentOrderEntity currentOrderEntity) {
    return storIOSQLite.put()
        .object(currentOrderEntity)
        .prepare()
        .asRxObservable();
  }

  /**
   * Saves/Updates order item selected by user.
   * Timestamp calculated here. Might refactor.
   * @param inventoryItemLocal - the item selected by customer
   * @param orderId - order this item is associated with
   * @return PutResult just to know success/failure
   */
  public Observable<PutResult> createOrUpdateItem(
     InventoryItemLocal inventoryItemLocal,
      String orderId) {

      return storIOSQLite.get().listOfObjects(InventoryOrderItemEntity.class).withQuery(Query.builder()
        .table(InventoryOrderItemTable.TABLE)
        .where(InventoryOrderItemTable.COLUMN_ORDER_ITEM_ID + " = ?")
        .whereArgs(inventoryItemLocal.inventoryItemId)
        .build())
        .prepare()
        .asRxObservable()
        .subscribeOn(Schedulers.io())
        .take(1)
        .flatMap(inventoryOrderItemEntities -> {
          Long createdAt = inventoryOrderItemEntities.size() > 0
              ? inventoryOrderItemEntities.get(0).getCreatedAt()
              : System.currentTimeMillis();
          return saveOrderItem(new InventoryOrderItemEntity(
              inventoryItemLocal.inventoryItemId,
              createdAt,
              System.currentTimeMillis(),
              orderId,
              (long) inventoryItemLocal.itemPrice,
              (long) inventoryItemLocal.quantity,
              inventoryItemLocal.itemName,
              inventoryItemLocal.itemDescription,
              inventoryItemLocal.itemImageUrl,
              inventoryItemLocal.additionalNotes));

        });

  }

  /**
   * Saves/Updates order
   * TimeStamp + OrderId created here. Might Refactor
   * @param qrToken - saving as may need to query group by qr_token in future
   * @return CurrentOrderEntity - Needed by QrScannerPresenter to flatmap and save the items
   * in this order
   *
   * @see com.sendkoin.customer.payment.paymentCreate.QrScannerPresenter
   */
  public Observable<CurrentOrderEntity> createOrUpdateOrder(String qrToken) {

    return storIOSQLite.get().listOfObjects(CurrentOrderEntity.class).withQuery(Query.builder()
        .table(CurrentOrderTable.TABLE)
        .where(CurrentOrderTable.COLUMN_QR_TOKEN + " = ?")
        .whereArgs(qrToken)
        .build())
        .prepare()
        .asRxObservable()
        .take(1)
        .subscribeOn(Schedulers.io())
        .flatMap(orderItemEntities -> {
          Long createdAt = orderItemEntities.size() > 0
              ? orderItemEntities.get(0).getCreatedAt()
              : System.currentTimeMillis();
          String orderId = orderItemEntities.size() > 0
              ? orderItemEntities.get(0).getOrderId()
              : UUID.randomUUID().toString();
          CurrentOrderEntity currentOrderEntity = new CurrentOrderEntity(
              orderId,
              qrToken,
              createdAt,
              System.currentTimeMillis()

          );
          return saveOrder(currentOrderEntity).map(putResult -> currentOrderEntity);
        });
  }


  /**
   * Fetches the users current order with the list of items involved
   * @return List<InventoryOrderItemEntity>
   */
  public Observable<List<InventoryOrderItemEntity>> getCurrentOrder() {
    return storIOSQLite.get()
        .listOfObjects(InventoryOrderItemEntity.class)
        .withQuery(Query.builder()
            .table(InventoryOrderItemTable.TABLE)
            .build())
        .prepare()
        .asRxObservable();
  }

  public List<SaleItem> toSaleItems(List<InventoryOrderItemEntity> inventoryOrderItemEntities) {
    List<SaleItem> saleItems = new ArrayList<>();
    for (InventoryOrderItemEntity inventoryOrderItemEntity : inventoryOrderItemEntities) {
      SaleItem saleItem = new SaleItem.Builder()
          .name(inventoryOrderItemEntity.getItemName())
          .price(inventoryOrderItemEntity.getItemPrice().intValue())
          .quantity(inventoryOrderItemEntity.getItemQuantity().intValue())
          .customer_notes(inventoryOrderItemEntity.getItemAdditionalNotes())
          .sale_type(SaleItem.SaleType.QUICK_SALE)
          .build();
      saleItems.add(saleItem);
    }

    return saleItems;
  }

  /**
   * For testing whether correct items were stored or not
   * @return
   */
  public List<InventoryOrderItemEntity> getCurrentOrderTest() {
    return storIOSQLite.get()
        .listOfObjects(InventoryOrderItemEntity.class)
        .withQuery(Query.builder()
            .table(InventoryOrderItemTable.TABLE)
            .build())
        .prepare()
        .executeAsBlocking();
  }
  /**
   * As the customer goes back to scanner OR finished paying order deleted and new order
   * will be started.
   * Operations below used in flatmap in QrScannerPresenter to delete order and items
   * @param transaction - Proto Object representing transaction in product
   * @return
   */
  public Observable<Transaction> removeAllOrders(Transaction transaction) {
    return storIOSQLite.delete()
        .byQuery(DeleteQuery.builder()
            .table(CurrentOrderTable.TABLE)
            .build())
        .prepare()
        .asRxObservable()
        .flatMap(deleteResult -> removeAllOrderItems(transaction));
  }

  private Observable<Transaction> removeAllOrderItems(Transaction transaction) {
    return storIOSQLite.delete()
        .byQuery(DeleteQuery.builder()
            .table(InventoryOrderItemTable.TABLE)
            .build())
        .prepare()
        .asRxObservable()
        .map(deleteResult -> transaction);
  }
}
