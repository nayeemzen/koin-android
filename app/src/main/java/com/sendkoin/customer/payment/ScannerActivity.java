package com.sendkoin.customer.payment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import com.sendkoin.customer.R;
import com.sendkoin.customer.payment.makePayment.QRScannerFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Holds the QrScanner
 * todo : QRScannerFragment just needs to be ported to QRCodeScannerActivity - delete this class
 */

public class ScannerActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_qrscanner);
    ButterKnife.bind(this);

    QRScannerFragment qrScannerFragement = new QRScannerFragment();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.add(R.id.frame_layout, qrScannerFragement);
    transaction.commit();
  }
}
