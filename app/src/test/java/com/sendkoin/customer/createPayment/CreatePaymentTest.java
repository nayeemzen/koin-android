package com.sendkoin.customer.createPayment;

import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.api.SaleItem;
import com.sendkoin.customer.DaggerKoinTestComponent;
import com.sendkoin.customer.KoinTestModule;
import com.sendkoin.customer.MiscGenerator;
import com.sendkoin.customer.RxSchedulersOverrideRule;
import com.sendkoin.customer.data.FakePaymentService;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.payment.paymentCreate.QrScannerContract;
import com.sendkoin.customer.payment.paymentCreate.pinConfirmation.PinConfirmationContract;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import java.util.List;

import javax.inject.Inject;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@RunWith(RobolectricTestRunner.class)
public class CreatePaymentTest {
  @Rule public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

  @Inject QrScannerContract.View view;
  @Inject PinConfirmationContract.View pinConfirmationView;
  @Inject PinConfirmationContract.Presenter pinConfirmationPresenter;
  @Inject QrScannerContract.Presenter presenter;
  @Inject MiscGenerator miscGenerator;
  @Inject LocalPaymentDataStore localPaymentDataStore;

  @Before
  public void setup() {
    DaggerCreatePaymentTestComponent.builder()
        .koinTestComponent(DaggerKoinTestComponent.builder()
            .koinTestModule(new KoinTestModule())
            .build())
        .createPaymentTestModule(new CreatePaymentTestModule(
            mock(QrScannerContract.View.class),
            mock(PinConfirmationContract.View.class)))
        .build()
        .inject(this);
    MockitoAnnotations.initMocks(this);
    localPaymentDataStore.clearDB();
  }

  /**
   * Tests a successful case of scanning a static qr code
   */
  @Test
  public void staticPaymentTest() {
    String qrToken = "test_static_transaction";
    QrCode qrCode = miscGenerator.generateQRCode(QrType.STATIC, qrToken);
    List<SaleItem> saleItems = miscGenerator.generateStaticPaymentSaleItems();
    InitiateStaticTransactionRequest initiateStaticTransactionRequest =
        new InitiateStaticTransactionRequest.Builder()
            .sale_items(saleItems)
            .qr_token(qrCode.qr_token)
            .build();
    //1. initiate the transaction after entering amount
    presenter.createInitiateTransactionRequest(qrCode,saleItems);
    //2. verify correct proto object created and passed to PinConfirmationActivity
    verify(view).processStaticTransaction(initiateStaticTransactionRequest);
    //3. correct pin entered - process the payment
    pinConfirmationPresenter.processStaticTransactionRequest(initiateStaticTransactionRequest);
    //4. payment complete
    verify(pinConfirmationView).showTransactionReciept(
        FakePaymentService.paymentEntityLinkedHashMap.get(qrToken));
  }

  /**
   * Testing the Inventory Static Fragment
   *  - Call put Order a number of times using the presenter
   *  - Call getOrderItems and see the number of items match the items put
   *  - After calling the AcceptTransaction see if the order was deleted as this is crucial
   */
}
