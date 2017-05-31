package com.sendkoin.customer.Payment.QRPayment;


import android.widget.Toast;

import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.Payment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import rx.Subscription;

/**
 * Created by warefhaque on 5/23/17.
 */

public class QRScannerPresenter  implements QRScannerContract.Presenter{

  private QRScannerContract.View view;
  private LocalPaymentDataStore localPaymentDataStore;
  public static final String MERCHANT_ID = "merchant_id";
  public static final String MERCHANT_NAME = "merchant_name";
  public static final String SALE_AMOUNT = "sale_amount";
  private Subscription subscription;

  @Inject
  public QRScannerPresenter(QRScannerContract.View view,
                            LocalPaymentDataStore localPaymentDataStore) {

    this.view = view;
    this.localPaymentDataStore = localPaymentDataStore;
  }

  @Override
  public void createPayment(JSONObject paymentJson) throws JSONException {

    String merchant_id = paymentJson.has(MERCHANT_ID) ? paymentJson.getString(MERCHANT_ID) : "";
    String merchant_name = paymentJson.has(MERCHANT_NAME) ? paymentJson.getString(MERCHANT_NAME) : "";
    String sale_amount = paymentJson.has(SALE_AMOUNT) ? paymentJson.getString(SALE_AMOUNT) : "";
    Date currentDate = new Date();
    String date = getFormattedDate(currentDate);
    // todo: Change the transaction id later (its merchant id now
    Payment payment = new Payment()
        .setTransactionId(merchant_id)
        .setTotalPrice(Integer.valueOf(sale_amount))
        .setMerchantName(merchant_name)
        .setDate(date)
        .setMerchantType("Restaurant");

    subscription = localPaymentDataStore.createPayment(payment).subscribe(realmAsyncTask -> Toast.makeText(view.getContext(), "Payment Saved!", Toast.LENGTH_SHORT).show());

  }

  @Override
  public void unsubscribe() {
    if (subscription!=null){
      subscription.unsubscribe();
    }

  }

  public String getFormattedDate(Date date){

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM dd");
    return simpleDateFormat.format(date);
  }

  @Override
  public void close() {
    localPaymentDataStore.close();
  }

  // http request
}
