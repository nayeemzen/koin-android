package com.sendkoin.customer.payment.paymentCreate.dynamicQr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sendkoin.api.QrCode;
import com.sendkoin.customer.R;
import com.sendkoin.customer.payment.paymentCreate.QrScannerActivity;

import java.io.IOException;
import java.util.Collections;

import agency.tango.android.avatarview.views.AvatarView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by warefhaque on 7/9/17.
 */

public class DynamicQrPaymentFragment extends android.app.Fragment {
  @BindView(R.id.merchant_logo)
  AvatarView merchantLogo;
  @BindView(R.id.sale_amount)
  TextView saleAmount;
  @BindView(R.id.pay_button)
  FancyButton payButton;

  private QrCode qrCode;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_dynamic_qr_payment, container, false);
    ButterKnife.bind(this, view);
    setupPayButton();
    try {
      setUpArguments();
    } catch (IOException e) {
      e.printStackTrace();
    }
    ((QrScannerActivity) getActivity()).setUpLogo(merchantLogo, qrCode.merchant_name);
    saleAmount.setText("$" + qrCode.amount.toString());
    return view;
  }

  private void setUpArguments() throws IOException {
    Bundle arguments = getArguments();
    byte[] qrCodeBytes = arguments.getByteArray("qr_code");
    this.qrCode = QrCode.ADAPTER.decode(qrCodeBytes);
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
    ((QrScannerActivity) getActivity())
        .showPinConfirmationActivity(qrCode, Collections.emptyList());
  }
}
