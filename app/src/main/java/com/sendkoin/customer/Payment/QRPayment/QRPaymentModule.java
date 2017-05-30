package com.sendkoin.customer.Payment.QRPayment;

import com.sendkoin.customer.Data.Dagger.CustomScope;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;

import dagger.Module;
import dagger.Provides;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Created by warefhaque on 5/23/17.
 */

@Module
public class QRPaymentModule {
  private QRScannerContract.View view;

  public QRPaymentModule(QRScannerContract.View view) {
    this.view = view;
  }

  @Provides
  @CustomScope
  public QRScannerContract.Presenter providesPresenter(LocalPaymentDataStore localPaymentDataStore){
    return new QRScannerPresenter(view, localPaymentDataStore);
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
