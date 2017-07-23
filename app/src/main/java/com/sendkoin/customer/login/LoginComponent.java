package com.sendkoin.customer.login;

import com.sendkoin.customer.data.dagger.Component.NetComponent;
import com.sendkoin.customer.data.dagger.CustomScope;


import dagger.Component;

/**
 * Created by warefhaque on 5/30/17.
 */

@CustomScope
@Component(dependencies = NetComponent.class, modules = LoginModule.class)
public interface LoginComponent {
  void inject (LoginActivity loginActivity);
}
