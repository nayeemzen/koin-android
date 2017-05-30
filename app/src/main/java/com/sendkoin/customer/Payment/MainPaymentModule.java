package com.sendkoin.customer.Payment;

import com.sendkoin.customer.Data.Dagger.CustomScope;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;

import dagger.Module;
import dagger.Provides;

/**
 * Created by warefhaque on 5/20/17.
 */

@Module
public class MainPaymentModule {


    private MainPaymentContract.View view;

    public MainPaymentModule(MainPaymentContract.View view) {
        this.view = view;
    }

    @Provides
    @CustomScope
    MainPaymentContract.View providesMainPaymentView(){
        return view;
    }

    @Provides
    @CustomScope
    MainPaymentContract.Presenter providesMainPaymentPresenter(MainPaymentContract.View view, LocalPaymentDataStore localPaymentDataStore){
        return new MainPaymentPresenter(view, localPaymentDataStore);
    }
}

