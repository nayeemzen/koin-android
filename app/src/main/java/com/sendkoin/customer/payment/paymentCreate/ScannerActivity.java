package com.sendkoin.customer.payment.paymentCreate;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sendkoin.customer.R;

import butterknife.ButterKnife;

/**
 * Holds the QrScanner
 * todo : QrScannerFragment just needs to be ported to QrScannerActivity - delete this class
 */

public class ScannerActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_qrscanner);
    ButterKnife.bind(this);

    QrScannerFragment qrScannerFragement = new QrScannerFragment();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.add(R.id.frame_layout, qrScannerFragement);
    transaction.commit();
  }
}
