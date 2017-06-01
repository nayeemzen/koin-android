package com.sendkoin.customer.Payment.QRPayment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sendkoin.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by warefhaque on 5/29/17.
 */

public class PaymentConfirmationActivity extends Activity{

  @BindView(R.id.pay_button)
  FancyButton payButton;
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_payment_confirmation);
    ButterKnife.bind(this);

    setupPayButton();
  }
  /**
   * For the Calligraphy fonts
   * @param newBase
   */
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  private void setupPayButton() {
    payButton.setText("Pay");
    payButton.setBackgroundColor(Color.parseColor("#008489"));
    payButton.setFocusBackgroundColor(Color.parseColor("#37B3B8"));
    payButton.setTextSize(20);
    payButton.setRadius(17);
    payButton.setPadding(10,20,10,20);
    payButton.setCustomTextFont("Nunito-Bold.ttf");
  }
}
