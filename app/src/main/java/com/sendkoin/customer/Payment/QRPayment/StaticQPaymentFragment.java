package com.sendkoin.customer.Payment.QRPayment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sendkoin.api.SaleItem;
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

public class StaticQPaymentFragment extends android.app.Fragment {

  @BindView(R.id.pay_button)
  FancyButton payButton;
  @BindView(R.id.enter_sales_message)
  TextView saleAmoutMessage;
  @BindView(R.id.merchant_logo)
  AvatarView merchantLogo;
  @BindView(R.id.enter_sales)
  EditText enterSaleAmount;

  QRCodeScannerActivity qrCodeScannerActivity;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_static_qr_payment, container, false);
    ButterKnife.bind(this, view);
    setupPayButton();

    qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
    qrCodeScannerActivity.setUpLogo(merchantLogo);
    saleAmoutMessage.setText("Please enter the amount in Taka you wish to pay " + qrCodeScannerActivity.name);
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

  @OnClick(R.id.enter_sales)
  void processStaticPayment() {
    if (qrCodeScannerActivity.qrCode != null) {
      if (enterSaleAmount.getText().toString().isEmpty()) {
        Toast.makeText(getActivity(), "Please enter a valid sale amount", Toast.LENGTH_SHORT).show();
        return;
      }
      int saleAmount = Integer.parseInt(enterSaleAmount.getText().toString());

      qrCodeScannerActivity.mPresenter.acceptTransaction(qrCodeScannerActivity.qrCode, Collections.singletonList(new SaleItem.Builder()
          .name("One Time Payment")
          .quantity(1)
          .sale_type(SaleItem.SaleType.QUICK_SALE)
          .price(saleAmount)
          .build()));
    }
    qrCodeScannerActivity.qrCode = null;
  }
}
