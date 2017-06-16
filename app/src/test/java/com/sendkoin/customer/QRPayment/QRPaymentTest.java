package com.sendkoin.customer.QRPayment;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.sendkoin.customer.DaggerKoinTestComponent;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.KoinTestComponent;
import com.sendkoin.customer.KoinTestModule;
import com.sendkoin.customer.Payment.QRPayment.QRScannerContract;
import com.sendkoin.customer.Payment.QRPayment.QRScannerPresenter;
import com.sendkoin.customer.RxSchedulersOverrideRule;
import com.sendkoin.sql.entities.PaymentEntity;
import com.sendkoin.sql.tables.PaymentTable;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by warefhaque on 6/14/17.
 */

@RunWith(RobolectricTestRunner.class)
public class QRPaymentTest {
  @Rule
  public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

  @Inject
  QRScannerContract.View view;
  @Inject
  QRScannerContract.Presenter presenter;
  @Inject
  LocalPaymentDataStore localPaymentDataStore;

  @Before
  public void setup() {
    DaggerQRPaymentTestComponent.builder()
        .koinTestComponent(DaggerKoinTestComponent.builder()
            .koinTestModule(new KoinTestModule())
            .build())
        .qRPaymentTestModule(new QRPaymentTestModule(mock(QRScannerContract.View.class)))
        .build()
        .inject(this);
  }

  @Test
  public void createPayment() throws Exception {
    presenter.createTransaction("waref");
    verify(view).showTransactionComplete();
  }

}
