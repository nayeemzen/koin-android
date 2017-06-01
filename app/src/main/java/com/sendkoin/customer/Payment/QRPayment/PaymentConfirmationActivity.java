package com.sendkoin.customer.Payment.QRPayment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sendkoin.customer.R;

/**
 * Created by warefhaque on 5/29/17.
 */

public class PaymentConfirmationActivity extends Activity{

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_payment_confirmation);
  }
}
