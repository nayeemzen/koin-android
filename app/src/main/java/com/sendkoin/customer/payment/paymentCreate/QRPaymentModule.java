package com.sendkoin.customer.payment.paymentCreate;

import com.sendkoin.customer.data.dagger.CustomScope;
import com.sendkoin.customer.data.payments.InventoryService;
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.data.payments.PaymentService;

import dagger.Module;
import dagger.Provides;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import retrofit2.Retrofit;

/**
 * Dependencies for QRCodeScannerActivity
 * @see QRCodeScannerActivity
 */

@Module
public class QRPaymentModule {
  private QRScannerContract.View view;

  public QRPaymentModule(QRScannerContract.View view) {
    this.view = view;
  }

  @Provides
  @CustomScope
  public PaymentService providesPaymentService(Retrofit retrofit){
    return  retrofit.create(PaymentService.class);
  }

  @Provides
  @CustomScope
  public InventoryService providesInventoryService(Retrofit retrofit){
    return retrofit.create(InventoryService.class);
  }

  @Provides
  @CustomScope
  public QRScannerContract.Presenter providesPresenter(InventoryService inventoryService,
                                                       LocalOrderDataStore localOrderDataStore){
    return new QRScannerPresenter(view, inventoryService, localOrderDataStore);
  }

  @Provides
  @CustomScope
  public QRScannerContract.View providesView(){
    return view;
  }

  @Provides
  @CustomScope
  public ZBarScannerView providesZBarScannerView(){
    return new ZBarScannerView(view.getContext());
  }

}
