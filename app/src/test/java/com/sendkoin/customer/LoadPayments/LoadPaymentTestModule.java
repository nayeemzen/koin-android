//package com.sendkoin.customer.LoadPayments;
//
//import com.sendkoin.customer.Data.Dagger.CustomScope;
//import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
//import com.sendkoin.customer.Data.Payments.PaymentRepository;
//import com.sendkoin.customer.Data.Payments.PaymentService;
//import com.sendkoin.customer.FakePaymentService;
//import com.sendkoin.customer.Payment.MainPaymentContract;
//import com.sendkoin.customer.Payment.MainPaymentPresenter;
//
//import dagger.Module;
//import dagger.Provides;
//
///**
// * Created by warefhaque on 6/15/17.
// */
//
//@Module
//public class LoadPaymentTestModule {
//
//  private MainPaymentContract.View view;
//
//  public LoadPaymentTestModule(MainPaymentContract.View view) {
//    this.view = view;
//  }
//
//  @Provides
//  @CustomScope
//  public MainPaymentContract.View providesView(){
//    return view;
//  }
//
//  @Provides
//  @CustomScope
//  public MainPaymentContract.Presenter providesPresenter(
//      LocalPaymentDataStore localPaymentDataStore,
//      PaymentRepository paymentRepository,
//      PaymentService paymentService){
//    return new MainPaymentPresenter(view, localPaymentDataStore, paymentRepository, paymentService);
//  }
//
//}
