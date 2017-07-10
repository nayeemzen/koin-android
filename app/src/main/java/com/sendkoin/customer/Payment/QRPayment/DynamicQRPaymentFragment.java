package com.sendkoin.customer.Payment.QRPayment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sendkoin.customer.R;

import java.util.Collections;

import agency.tango.android.avatarview.views.AvatarView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by warefhaque on 7/9/17.
 */

public class DynamicQRPaymentFragment extends android.app.Fragment {
  @BindView(R.id.merchant_logo)
  AvatarView merchantLogo;
  @BindView(R.id.sale_amount)
  TextView saleAmount;
  @BindView(R.id.pay_button)
  FancyButton payButton;
  private QRCodeScannerActivity qrCodeScannerActivity;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_dynamic_qr_payment, container, false);
    ButterKnife.bind(this, view);
    setupPayButton();

    qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
    qrCodeScannerActivity.setUpLogo(merchantLogo);
    saleAmount.setText("$"+qrCodeScannerActivity.dynamicQRAmount);
    return view;
  }

  private void setupPayButton() {
    payButton.setText("Pay");
    payButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
    payButton.setFocusBackgroundColor(Color.parseColor("#008489"));
    payButton.setBorderWidth(5);
    payButton.setBorderColor(Color.parseColor("#37B3B8"));
    payButton.setTextSize(20);
    payButton.setTextColor(Color.parseColor("#37B3B8"));
    payButton.setRadius(20);
    payButton.setPadding(10, 20, 10, 20);
    payButton.setCustomTextFont("Nunito-Regular.ttf");
  }

  @OnClick(R.id.pay_button)
  void payClicked(){
    qrCodeScannerActivity.mPresenter.acceptTransaction(qrCodeScannerActivity.qrCode, Collections.emptyList());
  }
}
