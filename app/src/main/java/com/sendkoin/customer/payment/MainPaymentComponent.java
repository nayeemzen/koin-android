package com.sendkoin.customer.payment;

import com.sendkoin.customer.data.dagger.Component.NetComponent;
import com.sendkoin.customer.data.dagger.CustomScope;

import dagger.Component;


@CustomScope
@Component(dependencies = NetComponent.class, modules = MainPaymentModule.class)
public interface MainPaymentComponent {
    void inject (MainPaymentFragment mainPaymentFragment);
}
