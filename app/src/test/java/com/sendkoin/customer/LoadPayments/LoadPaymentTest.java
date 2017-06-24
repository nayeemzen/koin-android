package com.sendkoin.customer.LoadPayments;

import android.text.format.DateFormat;

import com.annimon.stream.Stream;
import com.sendkoin.api.Merchant;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.DaggerKoinTestComponent;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.FakePaymentService;
import com.sendkoin.customer.KoinTestModule;
import com.sendkoin.customer.Payment.MainPaymentContract;
import com.sendkoin.customer.Payment.QRPayment.QRScannerContract;
import com.sendkoin.customer.QRPayment.QRPaymentTestModule;
import com.sendkoin.customer.RxSchedulersOverrideRule;
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
    createTransactionHash();
  }

  @Test
  public void loadPayments() throws Exception {

    //1. start with a fresh db
    localPaymentDataStore.deleteAllPayments();
    assert (localPaymentDataStore.getAllPayments().size() == 0);

    mainPaymentPresenter.subscribe();
    List<PaymentEntity> payments = new ArrayList<>();
    verify(view).showPaymentItems(payments);

    String transactionToken = "1";
    qrScannerPresenter.acceptTransaction(transactionToken);
    String transactionToken2 = "2";
    qrScannerPresenter.acceptTransaction(transactionToken2);

    verify(view, times(3)).showPaymentItems(argumentCaptor.capture());
    List<PaymentEntity> paymentEntities = argumentCaptor.getValue();

    // this proves the descending order of getting the payments as "2" has a higher timestamp!
    PaymentEntity paymentEntity = paymentEntities.get(0);
    assert (paymentEntity
        .getMerchantName()
        .equals(FakePaymentService
            .paymentEntityLinkedHashMap
            .get(transactionToken2)
            .merchant.store_name));

    PaymentEntity paymentEntity2 = paymentEntities.get(1);
    assert (paymentEntity2
        .getMerchantName()
        .equals(FakePaymentService
            .paymentEntityLinkedHashMap
            .get(transactionToken)
            .merchant.store_name));
  }

  @Test
  public void detailsTest() throws Exception {
  }

  /************************************************************************************************
   * Helper functions start here
   ************************************************************************************************/

  public LinkedHashMap<String, List<PaymentEntity>> groupTransactionsByCreatedAt(
      List<PaymentEntity> transactionEntities) {
    return Stream.of(transactionEntities)
        .collect(groupingBy(transactionEntity -> getCreatedAt(transactionEntity.getCreatedAt()),
            LinkedHashMap::new, toList()));
  }

  public String getCreatedAt(long createdAt) {
    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    cal.setTimeInMillis(createdAt);
    String dateString = DateFormat.format("EEEE, MMMM d", cal).toString();
    int day = cal.get(Calendar.DAY_OF_MONTH);
    return dateString + getDateSuffix(day);
  }

  private String getDateSuffix(int day) {
    switch (day) {
      case 1:
        return "st";
      case 2:
        return "nd";
      case 3:
        return "rd";
      case 31:
        return "st";
      default:
        return "th";
    }
  }

  public PaymentEntity fromWire(Transaction transaction) {
    return new PaymentEntity(
        transaction.token,
        transaction.amount.longValue(),
        transaction.created_at,
        transaction.state.name(),
        transaction.merchant.store_name,
        transaction.merchant.store_type);
  }

  public void createTransactionHash(){
    long timeStamp = System.currentTimeMillis() /1000L;
    String transactionToken = "1";
    Transaction transaction = new Transaction.Builder()
        .token(transactionToken)
        .created_at(timeStamp)
        .amount(300)
        .merchant(new Merchant.Builder()
            .store_name("Gloria Jeans")
            .store_type("Coffee Shop")
            .build())
        .state(Transaction.State.COMPLETE)
        .build();
    FakePaymentService.paymentEntityLinkedHashMap.put(transactionToken, transaction);

    timeStamp = System.currentTimeMillis() /1000L;
    String transactionToken2 = "2";
    Transaction transaction2 = new Transaction.Builder()
        .token(transactionToken2)
        .created_at(timeStamp)
        .amount(400)
        .merchant(new Merchant.Builder()
            .store_name("Pizza Hut")
            .store_type("Restaurant")
            .build())
        .state(Transaction.State.COMPLETE)
        .build();
    FakePaymentService.paymentEntityLinkedHashMap.put(transactionToken2,transaction2);
  }


}
