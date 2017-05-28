package com.sendkoin.koincustomer.Payment;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.sendkoin.koincustomer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.koincustomer.Data.Payments.Models.Payment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by warefhaque on 5/20/17.
 */

public class MainPaymentPresenter implements MainPaymentContract.Presenter{
    private MainPaymentContract.View view;
    private LocalPaymentDataStore localPaymentDataStore;
    private Subscription subscription;

    @Inject
    public MainPaymentPresenter(MainPaymentContract.View view, LocalPaymentDataStore localPaymentDataStore) {

        this.view = view;
        this.localPaymentDataStore = localPaymentDataStore;
    }

    @Override
    public void loadItems() {
        subscription = localPaymentDataStore.getAllTransactions()
            .subscribe(new Subscriber<RealmResults<Payment>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(RealmResults<Payment> payments) {
                List<Payment> items = Stream.of(payments).collect(Collectors.toList());
                view.showPaymentItems(groupPaymentsIntoHashMap(items));
            }
        });

    }

    public HashMap<String, List<Payment>> groupPaymentsIntoHashMap(List<Payment> payments){
        HashMap<String, List<Payment>> groupedResult = new HashMap<>();

        for (Payment payment : payments){
            if (groupedResult.containsKey(payment.getDate())){
                groupedResult.get(payment.getDate()).add(payment);
            }
            else{
                List<Payment> paymentList = new ArrayList<>();
                paymentList.add(payment);
                groupedResult.put(payment.getDate(),paymentList);
            }
        }
        return groupedResult;
    }
}
