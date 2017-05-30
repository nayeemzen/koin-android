package com.sendkoin.customer.Login;

import com.sendkoin.customer.Data.Dagger.Component.NetComponent;
import com.sendkoin.customer.Data.Dagger.CustomScope;


import dagger.Component;

/**
 * Created by warefhaque on 5/30/17.
 */

@CustomScope
@Component(dependencies = NetComponent.class, modules = LoginModule.class)
public interface LoginComponent {
  void inject (LoginActivity loginActivity);
}
