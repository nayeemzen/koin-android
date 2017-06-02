package com.sendkoin.customer.Payment.QRPayment;


import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.Data.Payments.PaymentService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import rx.Subscription;

/**
 * Created by warefhaque on 5/23/17.
 */

public class QRScannerPresenter implements QRScannerContract.Presenter {

  private QRScannerContract.View view;
  private LocalPaymentDataStore localPaymentDataStore;
  private PaymentService paymentService;
  private RealSessionManager realSessionManager;
  public static final String MERCHANT_NAME = "merchant_name";
  public static final String SALE_AMOUNT = "sale_amount";
  private Subscription subscription;

  @Inject
  public QRScannerPresenter(QRScannerContract.View view,
                            LocalPaymentDataStore localPaymentDataStore,
                            PaymentService paymentService,
                            RealSessionManager realSessionManager) {

    this.view = view;
    this.localPaymentDataStore = localPaymentDataStore;
    this.paymentService = paymentService;
    this.realSessionManager = realSessionManager;
  }

  /**
   * 1. Create the acceptTransactionObject and make call to api
   * to save payment
   *
   * 2. Save the transaction object to realm
   *
   * @param transactionToken - provided from the QR
   */
  @Override
  public void createTransaction(String transactionToken) {

    //1. create the transaction object
    long timeStamp = System.currentTimeMillis() / 1000L;

    AcceptTransactionRequest acceptTransactionRequest = new AcceptTransactionRequest(
        timeStamp,
        "waref",
        transactionToken);
    //2. save the transaction in the DB
    subscription = paymentService
        .acceptCurrentTransaction(realSessionManager.getSessionToken(), acceptTransactionRequest)
    //3. save the transaction in realm
        .subscribe(transaction -> localPaymentDataStore.createPayment(RealmTransaction.
            transactionToRealmTransaction(transaction)));
  }


  @Override
  public void subscribe() {
    // happens when the scan is captured so dont need here
  }

  @Override
  public void unsubscribe() {
    if (subscription != null) {
      subscription.unsubscribe();
    }

  }

  @Override
  public void closeRealm() {
    if (localPaymentDataStore != null) {
      localPaymentDataStore.close();
    }
  }

  public static String getFormattedDate(Date date) {

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM dd");
    return simpleDateFormat.format(date);
  }

  /**
   * Create object to populate the payment confirmation screen
   *
   * @param paymentJson result from the scan
   * @throws JSONException
   */
  @Override
  public void getTransactionConfirmationDetails(JSONObject paymentJson) throws JSONException {
    String merchant_name = paymentJson.has(MERCHANT_NAME) ? paymentJson.getString(MERCHANT_NAME) : "";
    String sale_amount = paymentJson.has(SALE_AMOUNT) ? paymentJson.getString(SALE_AMOUNT) : "";
    Date currentDate = new Date();
    String date = getFormattedDate(currentDate);
    RealmTransaction realmTransaction = new RealmTransaction()
        .setAmount(Integer.valueOf(sale_amount))
        .setMerchantName(merchant_name)
        .setDate(date)
        .setTransactionToken("waref")
        .setMerchantType("Restaurant");

    // will show the dialog
    view.showTransactionConfirmationScreen(realmTransaction);
  }
}
