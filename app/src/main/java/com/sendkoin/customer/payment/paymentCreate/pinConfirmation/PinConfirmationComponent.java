package com.sendkoin.customer.payment.paymentCreate.pinConfirmation;

import com.sendkoin.customer.data.dagger.Component.NetComponent;
import com.sendkoin.customer.data.dagger.CustomScope;

import dagger.Component;

/**
 * Created by warefhaque on 7/29/17.
 */

@CustomScope
@Component(dependencies = NetComponent.class, modules = PinConfirmationModule.class)
public interface PinConfirmationComponent {
  void inject(PinConfirmationActivity pinConfirmationActivity);
}
