package com.sendkoin.koincustomer.Payment.QRPayment;

import android.content.Context;

import com.sendkoin.koincustomer.Data.Dagger.CustomScope;
import com.sendkoin.koincustomer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.koincustomer.Payment.MainPaymentContract;

import dagger.Module;
import dagger.Provides;
import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

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
