package com.sendkoin.customer.payment.paymentCreate.staticQr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sendkoin.api.QrCode;
import com.sendkoin.api.SaleItem;
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

public class StaticQrPaymentFragment extends android.app.Fragment {

  @BindView(R.id.pay_button) FancyButton payButton;
  @BindView(R.id.enter_sales_message) TextView saleAmoutMessage;
  @BindView(R.id.merchant_logo) AvatarView merchantLogo;
  @BindView(R.id.enter_sales) EditText enterSaleAmount;
  private QrCode qrCode;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_static_qr_payment, container, false);
    ButterKnife.bind(this, view);
    setupPayButton();
    try {
      setUpArguments();
      ((QrScannerActivity) getActivity()).setUpLogo(merchantLogo, qrCode.merchant_name);
      saleAmoutMessage.setText(getString(R.string.static_qr_message) + qrCode.merchant_name);
    } catch (IOException e) {
      e.printStackTrace();
      Toast.makeText(getActivity(), "Unable to set up payment. Please try again.", Toast.LENGTH_SHORT).show();
    }
    return view;
  }

  private void setUpArguments() throws IOException {
    Bundle arguments = getArguments();
    byte[] qrCodeBytes = arguments.getByteArray("qr_code");
    // not putting it as a field as OnClick needs it at any time
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
  void processStaticPayment() {
    if (enterSaleAmount.getText().toString().isEmpty()) {
      Toast.makeText(getActivity(), R.string.static_qr_valid_input_message, Toast.LENGTH_SHORT).show();
      return;
    }

    // process payment
    int saleAmount = Integer.parseInt(enterSaleAmount.getText().toString());
    ((QrScannerActivity) getActivity()).showPinConfirmationActivity(
        qrCode,
        Collections.singletonList(
            new SaleItem.Builder()
                .name("One Time Payment")
                .quantity(1)
                .sale_type(SaleItem.SaleType.QUICK_SALE)
                .price(saleAmount).build()));
  }

  @Override
  public void onPause() {
    super.onPause();
    ((QrScannerActivity) getActivity()).mPresenter.unsubscribe();
  }
}
