package com.sendkoin.koincustomer.Payment;

import javax.inject.Inject;

/**
 * Created by warefhaque on 5/20/17.
 */

public class MainPaymentPresenter implements MainPaymentContract.Presenter{
    private MainPaymentContract.View view;

    @Inject
    public MainPaymentPresenter(MainPaymentContract.View view) {

        this.view = view;
    }
}
