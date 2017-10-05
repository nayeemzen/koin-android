package com.sendkoin.customer.QRPayment;

import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.DaggerKoinTestComponent;
import com.sendkoin.customer.FakePaymentService;
import com.sendkoin.customer.KoinTestModule;
import com.sendkoin.customer.MiscGenerator;
import com.sendkoin.customer.RxSchedulersOverrideRule;
import com.sendkoin.customer.payment.paymentCreate.QRScannerContract;
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
public class QRPaymentTest {
  @Rule public RxSchedulersOverrideRule rxSchedulersOverrideRule = new RxSchedulersOverrideRule();

  @Inject QRScannerContract.View view;
  @Inject QRScannerContract.Presenter presenter;
  @Inject MiscGenerator miscGenerator;

  @Before
  public void setup() {
    DaggerQRPaymentTestComponent.builder()
        .koinTestComponent(DaggerKoinTestComponent.builder()
            .koinTestModule(new KoinTestModule())
            .build())
        .qRPaymentTestModule(new QRPaymentTestModule(mock(QRScannerContract.View.class)))
        .build()
        .inject(this);
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void staticPaymentTest() {
    String qrToken = "test_static_transaction";
    QrCode qrCode = miscGenerator.generateQRCode(QrType.STATIC, qrToken);
    List<SaleItem> saleItems = miscGenerator.generateSaleItems();

    presenter.acceptTransaction(qrCode, saleItems);
    Transaction result = FakePaymentService.paymentEntityLinkedHashMap.get(qrToken);

    verify(view).showTransactionReciept(result);
  }

  /**
   * Testing the Inventory Static Fragment
   *  - Call put Order a number of times using the presenter
   *  - Call getOrderItems and see the number of items match the items put
   *  - After calling the AcceptTransaction see if the order was deleted as this is crucial
   */
}
