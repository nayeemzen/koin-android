package com.sendkoin.customer.data.payments.Local;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.payment.makePayment.inventory.InventoryRecyclerViewAdapter;
import com.sendkoin.sql.entities.CurrentOrderEntity;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;
import com.sendkoin.sql.tables.CurrentOrderTable;
import com.sendkoin.sql.tables.InventoryOrderItemTable;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by warefhaque on 7/12/17.
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

  public Observable<PutResult> createOrUpdateItem(
      InventoryRecyclerViewAdapter.InventoryQRPaymentListItem inventoryQRPaymentListItem,
      String orderId) {

      return storIOSQLite.get().listOfObjects(InventoryOrderItemEntity.class).withQuery(Query.builder()
        .table(InventoryOrderItemTable.TABLE)
        .where(InventoryOrderItemTable.COLUMN_ORDER_ITEM_ID + " = ?")
        .whereArgs(inventoryQRPaymentListItem.inventoryItemId)
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
              inventoryQRPaymentListItem.inventoryItemId,
              createdAt,
              System.currentTimeMillis(),
              orderId,
              (long) inventoryQRPaymentListItem.itemPrice,
              (long) inventoryQRPaymentListItem.quantity,
              inventoryQRPaymentListItem.itemName,
              inventoryQRPaymentListItem.itemDescription,
              inventoryQRPaymentListItem.itemImageUrl,
              inventoryQRPaymentListItem.additionalNotes));

        });

  }

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


  public Observable<List<InventoryOrderItemEntity>> getCurrentOrder() {
    return storIOSQLite.get()
        .listOfObjects(InventoryOrderItemEntity.class)
        .withQuery(Query.builder()
            .table(InventoryOrderItemTable.TABLE)
            .build())
        .prepare()
        .asRxObservable();
  }

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
