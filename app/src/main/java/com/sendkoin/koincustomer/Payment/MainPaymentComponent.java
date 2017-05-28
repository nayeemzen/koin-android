package com.sendkoin.koincustomer.Payment;

import com.sendkoin.koincustomer.Data.Dagger.Component.NetComponent;
import com.sendkoin.koincustomer.Data.Dagger.CustomScope;

import dagger.Component;

/**
 * Created by warefhaque on 5/20/17.
 */

@CustomScope
@Component(dependencies = NetComponent.class, modules = MainPaymentModule.class)
public interface MainPaymentComponent {
    void inject (MainPaymentFragment mainPaymentFragment);
}
