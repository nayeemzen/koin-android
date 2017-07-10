package com.sendkoin.customer.Payment.QRPayment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sendkoin.customer.R;

import butterknife.ButterKnife;

/**
 * Created by warefhaque on 7/9/17.
 */

public class InventoryQRPaymentFragment extends Fragment{

  QRCodeScannerActivity qrCodeScannerActivity;
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_inventory_qr, container, false);
    ButterKnife.bind(this, view);
    qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
    qrCodeScannerActivity.mPresenter.getInventory(qrCodeScannerActivity.qrCode.qr_token);
    return view;
  }
}
