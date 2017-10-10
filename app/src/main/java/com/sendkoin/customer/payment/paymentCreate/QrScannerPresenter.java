package com.sendkoin.customer.payment.paymentCreate;


import android.os.Bundle;
import android.util.Log;

import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.GetInventoryResponse;
import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.data.payments.InventoryService;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemLocal;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by warefhaque on 5/23/17.
 */

public class QrScannerPresenter implements QrScannerContract.Presenter {

  private static final String TAG = "QrScannerPresenter";
  private QrScannerContract.View view;
  private InventoryService inventoryService;
  private LocalOrderDataStore localOrderDataStore;
  public static final String MERCHANT_NAME = "merchant_name";
  public static final String SALE_AMOUNT = "sale_amount";
  private CompositeSubscription compositeSubscription = new CompositeSubscription();

  // TODO: need to fill in with proper error messages and codes
  public static final String INTERNAL_SERVER_ERROR = "HTTP 500 Internal Server Error";
  public static final String UNAUTHORIZED = "401";

  @Inject
  public QrScannerPresenter(QrScannerContract.View view,
                            InventoryService inventoryService,
                            LocalOrderDataStore localOrderDataStore) {

    this.view = view;
    this.inventoryService = inventoryService;
    this.localOrderDataStore = localOrderDataStore;
  }


  @Override
  public void getInventory(String qrToken) {
    Subscription subscription = inventoryService
        .getAllInventoryItems(qrToken)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<GetInventoryResponse>() {
          @Override
          public void onCompleted() {}

          @Override
          public void onError(Throwable e) {
            Log.e(TAG, e.getLocalizedMessage());
          }

          @Override
          public void onNext(GetInventoryResponse getInventoryResponse) {
            view.showInventoryItems(getInventoryResponse.categories);
          }
        });

    compositeSubscription.add(subscription);
  }

  @Override
  public void getOrderItems() {
    Subscription subscription = localOrderDataStore
        .getCurrentOrder()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<List<InventoryOrderItemEntity>>() {
          @Override
          public void onCompleted() {}

          @Override
          public void onError(Throwable e) {
            e.printStackTrace();
          }

          @Override
          public void onNext(List<InventoryOrderItemEntity> inventoryOrderEntities) {
            view.handleOrderItems(inventoryOrderEntities);
          }
        });

    compositeSubscription.add(subscription);
  }

  @Override
  public void putOrder(String qrToken, InventoryItemLocal inventoryItemLocal) {
    Subscription subscription = localOrderDataStore
        .createOrUpdateOrder(qrToken)
        .flatMap(currentOrderEntity -> localOrderDataStore.createOrUpdateItem(inventoryItemLocal,
            currentOrderEntity.getOrderId()))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<PutResult>() {
          @Override
          public void onCompleted() {}

          @Override
          public void onError(Throwable e) {
            Log.e(TAG, "Error adding inventory order: " + e.getLocalizedMessage());
            e.printStackTrace();
          }

          @Override
          public void onNext(PutResult inventoryOrder) {
            Log.d(TAG, inventoryOrder.toString());
          }
        });

    compositeSubscription.add(subscription);
  }

  /**
   * Removes all the entries from the CurrentOrderTable and the InventoryOrderItemTable
   * @param transaction = null if called from the QRScannerActivity when back pressed
   *                    = transaction object when placing the complete order and transitioning to
   *                    the TransactionDetailsActivity or detailed reciept of the payment.
   */

  @Override
  public void removeAllOrders(Transaction transaction) {
    Subscription subscription = localOrderDataStore
        .removeAllOrders(transaction)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Transaction>() {
          @Override
          public void onCompleted() {}

          @Override
          public void onError(Throwable e) {
            e.printStackTrace();
          }

          @Override
          public void onNext(Transaction tran) {
            if (transaction == null)
              view.showOrderDeleted();
          }
        });
    compositeSubscription.add(subscription);
  }

  /**
   * Create the proto for static and inventory static transactions
   * @param qrCode - contains the required token to process payment
   * @param saleItemList -
   *                     inventory static : items the customer bought
   *                     static : empty list
   */
  @Override
  public void createInitiateTransactionRequest(QrCode qrCode, List<SaleItem> saleItemList) {
    InitiateStaticTransactionRequest initiateStaticTransactionRequest =
        new InitiateStaticTransactionRequest.Builder()
            .sale_items(saleItemList)
            .qr_token(qrCode.qr_token)
            .build();
    view.processStaticTransaction(initiateStaticTransactionRequest, qrCode);
  }

  /**
   * Create the proto for dynamic transactions
   * @param qrCode - contains the required token to process payment
   */
  @Override
  public void createAcceptTransactionRequest(QrCode qrCode) {
    AcceptTransactionRequest acceptTransactionRequest =
        new AcceptTransactionRequest.Builder()
            .idempotence_token(UUID.randomUUID().toString())
            .qr_token(qrCode.qr_token)
            .build();
    view.processDynamicTransaction(acceptTransactionRequest, qrCode);
  }

  @Override
  public void subscribe() {}

  @Override
  public void unsubscribe() {
    if (compositeSubscription != null) {
      compositeSubscription.clear();
    }

  }
}
