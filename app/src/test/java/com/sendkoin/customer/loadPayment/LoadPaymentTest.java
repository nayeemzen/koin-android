package com.sendkoin.customer.loadPayment;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.collect.Ordering;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.DaggerKoinTestComponent;
import com.sendkoin.customer.data.FakePaymentService;
import com.sendkoin.customer.KoinTestModule;
import com.sendkoin.customer.MiscGenerator;
import com.sendkoin.customer.createPayment.QRPaymentTestModule;
import com.sendkoin.customer.RxSchedulersOverrideRule;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.payment.paymentCreate.QrScannerContract;
import com.sendkoin.customer.payment.paymentList.MainPaymentContract;
import com.sendkoin.sql.entities.PaymentEntity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static com.annimon.stream.Collectors.groupingBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by warefhaque on 6/15/17.
 */
@RunWith(RobolectricTestRunner.class)
public class LoadPaymentTest {
  @Rule
  public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

  @Captor ArgumentCaptor<List<PaymentEntity>> argumentCaptor;
  @Inject MainPaymentContract.View view;
  @Inject MainPaymentContract.Presenter mainPaymentPresenter;
  @Inject QrScannerContract.Presenter qrScannerPresenter;
  @Inject LocalPaymentDataStore localPaymentDataStore;
  @Inject MiscGenerator miscGenerator;

  @Before
  public void setup(){
    DaggerLoadPaymentTestComponent.builder()
        .koinTestComponent(DaggerKoinTestComponent.builder()
            .koinTestModule(new KoinTestModule())
            .build())
        .loadPaymentTestModule(new LoadPaymentTestModule(mock(MainPaymentContract.View.class)))
        .qRPaymentTestModule(new QRPaymentTestModule(mock(QrScannerContract.View.class)))
        .build()
        .inject(this);
    MockitoAnnotations.initMocks(this);
    miscGenerator.createTransactionHash();
    localPaymentDataStore.clearDB();
  }

  /**
   * When application not found -> ./gradlew clean test
   * Tests if MainPresenter working properly
   *  - calls "remote" server
   *  - saves transactions in local db
   *  - loads transactions from local db
   *  - checks if new transactions appear first
   */
  @Test
  public void loadPayments() {
    mainPaymentPresenter.subscribe();
    verify(view, times(2)).showPaymentItems(argumentCaptor.capture());
    List<PaymentEntity> paymentEntities = argumentCaptor.getValue();
    List<Long> transactionTimes = Stream.of(paymentEntities)
        .map(PaymentEntity::getCreatedAt)
        .collect(Collectors.toList());

    assert Ordering.natural().reverse().isOrdered(transactionTimes);
  }
}
