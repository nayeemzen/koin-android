package com.sendkoin.customer.payment.makePayment;


import android.util.Log;

import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.GetInventoryResponse;
import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.data.payments.InventoryService;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.PaymentService;
import com.sendkoin.customer.payment.makePayment.inventory.InventoryRecyclerViewAdapter;
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

public class QRScannerPresenter implements QRScannerContract.Presenter {

  private static final String TAG = "QRScannerPresenter";
  private QRScannerContract.View view;
  private LocalPaymentDataStore localPaymentDataStore;
  private PaymentService paymentService;
  private InventoryService inventoryService;
  private LocalOrderDataStore localOrderDataStore;
  public static final String MERCHANT_NAME = "merchant_name";
  public static final String SALE_AMOUNT = "sale_amount";
  private CompositeSubscription compositeSubscription = new CompositeSubscription();

  // TODO: 6/4/17 need to fill in with proper error messages and codes
  public static final String INTERNAL_SERVER_ERROR = "HTTP 500 Internal Server Error";
  public static final String UNAUTHORIZED = "401";

  @Inject
  public QRScannerPresenter(QRScannerContract.View view,
                            LocalPaymentDataStore localPaymentDataStore,
                            PaymentService paymentService,
                            InventoryService inventoryService,
                            LocalOrderDataStore localOrderDataStore) {

    this.view = view;
    this.localPaymentDataStore = localPaymentDataStore;
    this.paymentService = paymentService;
    this.inventoryService = inventoryService;
    this.localOrderDataStore = localOrderDataStore;
  }

  /**
   * 1. Create the acceptTransactionObject and make call to api
   * to save payment
   * <p>
   * 2. Save the transaction object to realm
   *
   * @param qrCode - provided from the QR
   */
  @Override
  public void acceptTransaction(QrCode qrCode, List<SaleItem> saleItems) {
    // 1. create the transaction object
    switch (qrCode.qr_type) {
      case DYNAMIC:
        processDynamicTransaction(qrCode);
        break;
      case STATIC:
      case INVENTORY_STATIC:
        if (saleItems != null) {
          processStaticTransaction(qrCode, saleItems);
        } else {
          Log.e(TAG, "Static: Qr sale amount = -1");
        }
        break;
    }

    // 2. save the transaction in the DB
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
  public void putOrder(String qrToken, InventoryRecyclerViewAdapter.InventoryQRPaymentListItem inventoryQRPaymentListItem ) {
    Subscription subscription = localOrderDataStore
        .createOrUpdateOrder(qrToken)
        .flatMap(currentOrderEntity -> localOrderDataStore.createOrUpdateItem(
            inventoryQRPaymentListItem,
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
   * ALERT : PASSING IN NULL FOR TRANSACTION WHEN "BACK" IS PRESSED FROM QRScannerActivity
   * Removes all the entries from the CurrentOrderTable and the InventoryOrderItemTable
   *
   * @param transaction = null if called from the QRScannerActivity when back pressed
   *                    = transaction object when placing the complete order and transitioning to
   *                    the DetailedReceiptActivity or detailed reciept of the payment.
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

  private void processStaticTransaction(QrCode qrCode, List<SaleItem> saleItems) {
    Subscription subscription = paymentService
        .initiateCurrentTransaction(new InitiateStaticTransactionRequest.Builder()
            .qr_token(qrCode.qr_token)
            .sale_items(saleItems)
            .build())
        .subscribeOn(Schedulers.io())
        .flatMap(initiateStaticTransactionResponse ->
            localPaymentDataStore.createTransaction(initiateStaticTransactionResponse
                .order
                .transaction))
        .flatMap(transaction -> localOrderDataStore.removeAllOrders(transaction))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Transaction>() {
          @Override
          public void onCompleted() {
            Log.d(TAG, "on Completed!");
          }

          @Override
          public void onError(Throwable e) {
            view.showTransactionError(getErrorMessage(e));
            Log.e(TAG, e.getMessage());
          }

          @Override
          public void onNext(Transaction transaction) {
            view.showTransactionReciept(transaction);
          }
        });

    compositeSubscription.add(subscription);
  }

  private void processDynamicTransaction(QrCode qrCode) {
    Subscription subscription = paymentService
        .acceptCurrentTransaction(new AcceptTransactionRequest.Builder()
            .idempotence_token(UUID.randomUUID().toString())
            .qr_token(qrCode.qr_token)
            .build())
        .subscribeOn(Schedulers.io())
        .flatMap(acceptTransactionResponse ->
            localPaymentDataStore.createTransaction(acceptTransactionResponse.transaction))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Transaction>() {
          @Override
          public void onCompleted() {
            Log.d(TAG, "on Completed!");
          }

          @Override
          public void onError(Throwable e) {
            view.showTransactionError(getErrorMessage(e));
            Log.e(TAG, e.getMessage());
          }

          @Override
          public void onNext(Transaction transaction) {
            view.showTransactionReciept(transaction);
          }
        });

    compositeSubscription.add(subscription);
  }


  @Override
  public void subscribe() {
    // happens when the scan is captured so dont need here
  }

  @Override
  public void unsubscribe() {
    if (compositeSubscription != null) {
      compositeSubscription.clear();
    }

  }

  @Override
  public void closeRealm() {
  }

  public String getErrorMessage(Throwable error) {
    if (error.getMessage().contains(INTERNAL_SERVER_ERROR)) {
      return "Declined. Internal Error.";
    } else {
      return "Declined. Please try again.";
    }
  }

}
