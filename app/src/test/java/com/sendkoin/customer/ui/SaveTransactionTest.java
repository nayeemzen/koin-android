package com.sendkoin.customer.ui;

import com.sendkoin.customer.BaseTestModule;
import com.sendkoin.customer.Payment.QRPayment.QRCodeScannerActivity;
import com.sendkoin.customer.Payment.QRPayment.QRScannerContract;
import com.sendkoin.customer.Payment.QRPayment.QRScannerPresenter;
import com.sendkoin.customer.RxSchedulersOverrideRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import static org.mockito.Mockito.verify;

/**
 * Created by warefhaque on 6/2/17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest="src/main/AndroidManifest.xml")
public class SaveTransactionTest {
  @Rule
  public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

  @Inject
  QRScannerPresenter mPresenter;

  @Inject
  QRScannerContract.View mView;

  @Before
  public void setup(){
    DaggerSaveTransactionTestComponent.builder()
        .baseTestModule(new BaseTestModule())
        .saveTransactionTestModule(new SaveTransactionTestModule())
        .build()
        .inject(this);
  }

  @Test
  public void testSaveTransaction(){
    mPresenter.createTransaction("a26465a7f0c00b5f829c58445bb30c898cb002c8");
    verify(mView).showTransactionComplete();
  }

}
