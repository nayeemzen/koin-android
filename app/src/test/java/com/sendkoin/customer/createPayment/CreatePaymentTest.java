package com.sendkoin.customer.createPayment;

import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.InventoryItem;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.api.SaleItem;
import com.sendkoin.customer.DaggerKoinTestComponent;
import com.sendkoin.customer.KoinTestModule;
import com.sendkoin.customer.MiscGenerator;
import com.sendkoin.customer.RxSchedulersOverrideRule;
import com.sendkoin.customer.data.FakePaymentService;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemLocal;
import com.sendkoin.customer.payment.paymentCreate.QrScannerContract;
import com.sendkoin.customer.payment.paymentCreate.pinConfirmation.PinConfirmationContract;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
  @Inject Random random;
  @Inject LocalOrderDataStore localOrderDataStore;
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
    verify(view).processStaticTransaction(initiateStaticTransactionRequest,qrCode);
    //3. correct pin entered - process the payment
    pinConfirmationPresenter.processStaticTransactionRequest(initiateStaticTransactionRequest);
    //4. payment complete
    verify(pinConfirmationView).showTransactionReciept(
        FakePaymentService.paymentEntityLinkedHashMap.get(qrToken));
  }

  /**
   * Testing the Inventory Static Fragment
   */
  @Test
  public void inventoryStaticPaymentTest() {
    String qrToken = "test_inventory_static_transaction";
    QrCode qrCode = miscGenerator.generateQRCode(QrType.STATIC, qrToken);
    //1. add item to cart
    InventoryItemLocal firstOrderItem = new InventoryItemLocal("Chicken",250,1,random.nextLong());
    presenter.putOrder(qrToken, firstOrderItem);
    //2. add another item to cart
    InventoryItemLocal secondOrderItem = new InventoryItemLocal("Beef",230,2,random.nextLong());
    presenter.putOrder(qrToken, secondOrderItem);

    List<SaleItem> saleItems = localOrderDataStore.toSaleItems(localOrderDataStore.getCurrentOrderTest());
    InitiateStaticTransactionRequest initiateStaticTransactionRequest =
        new InitiateStaticTransactionRequest.Builder()
            .sale_items(saleItems)
            .qr_token(qrCode.qr_token)
            .build();

    //3. initiate the transaction after entering amount
    presenter.createInitiateTransactionRequest(qrCode,saleItems);
    //4. verify correct proto object created and passed to PinConfirmationActivity
    verify(view).processStaticTransaction(initiateStaticTransactionRequest,qrCode);
    //5. correct pin entered - process the payment
    pinConfirmationPresenter.processStaticTransactionRequest(initiateStaticTransactionRequest);
    //6. payment complete
    verify(pinConfirmationView).showTransactionReciept(
        FakePaymentService.paymentEntityLinkedHashMap.get(qrToken));
  }
}
