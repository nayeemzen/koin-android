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
 * Dependencies for QrScannerActivity
 * @see QrScannerActivity
 */

@Module
public class QrScannerModule {
  private QrScannerContract.View view;

  public QrScannerModule(QrScannerContract.View view) {
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
  public QrScannerContract.Presenter providesPresenter(InventoryService inventoryService,
                                                       LocalOrderDataStore localOrderDataStore){
    return new QrScannerPresenter(view, inventoryService, localOrderDataStore);
  }

  @Provides
  @CustomScope
  public QrScannerContract.View providesView(){
    return view;
  }

  @Provides
  @CustomScope
  public ZBarScannerView providesZBarScannerView(){
    return new ZBarScannerView(view.getContext());
  }

}
