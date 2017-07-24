package com.sendkoin.customer.LoadPayments;

import android.text.format.DateFormat;

import com.annimon.stream.Stream;
import com.sendkoin.api.Merchant;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.api.TransactionState;
import com.sendkoin.customer.DaggerKoinTestComponent;
import com.sendkoin.customer.FakePaymentService;
import com.sendkoin.customer.KoinTestModule;
import com.sendkoin.customer.MiscGenerator;
import com.sendkoin.customer.QRPayment.QRPaymentTestModule;
import com.sendkoin.customer.RxSchedulersOverrideRule;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.payment.MainPaymentContract;
import com.sendkoin.customer.payment.makePayment.QRScannerContract;
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
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import static com.annimon.stream.Collectors.groupingBy;
import static com.annimon.stream.Collectors.toList;
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
  @Inject QRScannerContract.Presenter qrScannerPresenter;
  @Inject LocalPaymentDataStore localPaymentDataStore;
  @Inject MiscGenerator miscGenerator;

  @Before
  public void setup(){
    DaggerLoadPaymentTestComponent.builder()
        .koinTestComponent(DaggerKoinTestComponent.builder()
            .koinTestModule(new KoinTestModule())
            .build())
        .loadPaymentTestModule(new LoadPaymentTestModule(mock(MainPaymentContract.View.class)))
        .qRPaymentTestModule(new QRPaymentTestModule(mock(QRScannerContract.View.class)))
        .build()
        .inject(this);
    MockitoAnnotations.initMocks(this);
    miscGenerator.createTransactionHash();
  }

  @Test
  public void loadPayments() throws Exception {

    //1. start with a fresh db
    localPaymentDataStore.deleteAllPayments();
    assert (localPaymentDataStore.getAllPayments().size() == 0);

    //2. subscribe to the stream of items coming from the local DB
    mainPaymentPresenter.subscribe();
    List<PaymentEntity> payments = new ArrayList<>();
    // upon subscribing ou get a call back from the DB but DB is empty
    verify(view).showPaymentItems(payments);

    //3. "process" a fake transaction on the local DB
    QrCode firstQrCode = miscGenerator.generateQRCode(QrType.DYNAMIC, "1");
    List<SaleItem> firstSaleItems = miscGenerator.generateSaleItems();
    qrScannerPresenter.acceptTransaction(firstQrCode, firstSaleItems);

    // process another transaction on the local DB
    QrCode secondQRCode = miscGenerator.generateQRCode(QrType.DYNAMIC, "2");
    List<SaleItem> secondSaleItems = miscGenerator.generateSaleItems();
    qrScannerPresenter.acceptTransaction(secondQRCode,secondSaleItems);

    // EVERY TIME the DB changes you get a call back - RX MAGIC!!
    // YOU must get 3 callbacks for 3 processed transactions otherwise something went wrong!
    verify(view, times(3)).showPaymentItems(argumentCaptor.capture());
    List<PaymentEntity> paymentEntities = argumentCaptor.getValue();

    // 4. VERIFY the Transactions in DB are loaded in descending order like they
    // are supposed to. Transaction with token  "2" has a higher timestamp!!
    PaymentEntity paymentEntity = paymentEntities.get(0);
    assert (paymentEntity
        .getMerchantName()
        .equals(FakePaymentService
            .paymentEntityLinkedHashMap.get(secondQRCode.qr_token)
            .merchant
            .store_name));

    PaymentEntity paymentEntity2 = paymentEntities.get(1);
    assert (paymentEntity2
        .getMerchantName()
        .equals(FakePaymentService
            .paymentEntityLinkedHashMap.get(firstQrCode.qr_token)
            .merchant
            .store_name));
  }
}
