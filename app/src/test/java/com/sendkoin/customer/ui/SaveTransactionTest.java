package com.sendkoin.customer.ui;

import com.sendkoin.customer.BaseTestModule;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.Data.Payments.PaymentService;
import com.sendkoin.customer.Payment.QRPayment.QRCodeScannerActivity;
import com.sendkoin.customer.Payment.QRPayment.QRScannerContract;
import com.sendkoin.customer.Payment.QRPayment.QRScannerPresenter;
import com.sendkoin.customer.RxSchedulersOverrideRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.mockito.Mockito.verify;

/**
 * Created by warefhaque on 6/2/17.
 */


public class SaveTransactionTest {
  @Inject
  QRScannerContract.View mView;
  @Inject
  QRScannerPresenter mPresenter;
  @Inject
  FakeTransactionService fakeTransactionService;
  // want to test before the transaction and after whether the transaction object was actually saved
  Realm realm;

  @Before
  public void setup(){
    DaggerSaveTransactionTestComponent.builder()
        .baseTestModule(new BaseTestModule())
        .saveTransactionTestModule(new SaveTransactionTestModule())
        .build()
        .inject(this);

    RealmConfiguration testConfig =
        new RealmConfiguration.Builder().
            inMemory().
            name("test-realm").build();
   realm = Realm.getInstance(testConfig);
  }

  @Test
  public void testSaveTransaction(){

    String transactionToken = fakeTransactionService.initiateTransaction();
    RealmResults<RealmTransaction> realmResults = realm.where(RealmTransaction.class)
        .equalTo("transactionToken", transactionToken)
        .findAll();
    assert (realmResults == null);

    mPresenter.createTransaction(transactionToken);

    RealmResults<RealmTransaction> realmResults2 = realm.where(RealmTransaction.class)
        .equalTo("transactionToken", transactionToken)
        .findAll();
    assert (realmResults2 != null);

    verify(mView).showTransactionComplete();
  }

}
