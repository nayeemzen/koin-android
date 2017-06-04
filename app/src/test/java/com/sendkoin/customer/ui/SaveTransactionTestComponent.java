package com.sendkoin.customer.ui;

import com.sendkoin.customer.BaseTestModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by warefhaque on 6/2/17.
 */

@Singleton
@Component(modules = {
    BaseTestModule.class,
    SaveTransactionTestModule.class,
})
public interface SaveTransactionTestComponent {
  void inject(SaveTransactionTest saveTransactionTest);
}

