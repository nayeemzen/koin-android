package com.sendkoin.customer.Payment;

import android.util.Log;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.sendkoin.api.ListTransactionsResponse;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Authentication.RealSessionManager;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.Payment;
import com.sendkoin.customer.Data.Payments.PaymentRepository;
import com.sendkoin.customer.Data.Payments.PaymentService;
import com.sendkoin.customer.Payment.QRPayment.QRScannerPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.realm.RealmAsyncTask;
import io.realm.RealmResults;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by warefhaque on 5/20/17.
 */

public class MainPaymentPresenter implements MainPaymentContract.Presenter{
    private static final String TAG = "MainPaymentPresenter";
    private MainPaymentContract.View view;
    private LocalPaymentDataStore localPaymentDataStore;
    private PaymentRepository paymentRepository;
    private PaymentService paymentService;
    private RealSessionManager realSessionManager;
    private Subscription subscription;

    // need local and payment repo for calls
    @Inject
    public MainPaymentPresenter(MainPaymentContract.View view,
                                LocalPaymentDataStore localPaymentDataStore,
                                PaymentRepository paymentRepository,
                                PaymentService paymentService,
                                RealSessionManager realSessionManager) {

        this.view = view;
        this.localPaymentDataStore = localPaymentDataStore;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.realSessionManager = realSessionManager;
    }

    @Override
    public void loadItems() {
        // first call the remote db -> update the view -> update realm
        remoteDBCall();

    }
    private void remoteDBCall() {
        subscription = paymentRepository
             .getAllPayments(paymentService, "Bearer "+realSessionManager.getSessionToken())
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(new Subscriber<ListTransactionsResponse>() {
                 @Override
                 public void onCompleted() {
//                     Log.d(TAG, "Completed DB Call!");
                 }

                 @Override
                 public void onError(Throwable e) {
                     Log.e(TAG, "ERROR - " + e.getMessage());
                 }

                 @Override
                 public void onNext(ListTransactionsResponse listTransactionsResponse) {

                     List<Transaction> transactions = listTransactionsResponse.transactions;
                     view.showPaymentItems(groupPaymentsIntoHashMap(transactionsToPayments(transactions)));
                     localPaymentDataStore.saveAllTransactions(transactionsToPayments(transactions))
                         .subscribe(realmAsyncTask -> Log.d(TAG, "Saved from db to realm!"));
                 }

             });
    }

    /**
     * Debugging purposes
     */

    @Override
    public void deleteAll() {
        localPaymentDataStore.deleteAllTranscations();
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

    /**
     * Convert the transaction objects passed in to realm objects to save in realms
     * @param transactions - from db
     * @return payments
     */
    public List<Payment> transactionsToPayments(List<Transaction> transactions){

        List<Payment> payments = new ArrayList<>();
        for (Transaction transaction : transactions){

            Payment payment = new Payment()
                // TODO: 5/31/17 (WAREF) ask zen to save Type on the server
                .setMerchantType("Restaurant")
                .setMerchantName(transaction.merchant_name)
                .setAmount(transaction.amount)
                .setCreatedAt(transaction.created_at)
                .setTransactionToken(transaction.token)
                .setMerchantId(transaction.merchant_id)
                .setState(transaction.state)
                // TODO: 5/31/17 (WAREF) ask zen to save date on the server
                .setDate(QRScannerPresenter.getFormattedDate(new Date()));

            payments.add(payment);

        }

        return payments;

    }



    private void initialLoadItems() {
        subscription = localPaymentDataStore.getAllTransactions()
            .subscribe(new Subscriber<RealmResults<Payment>>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "Completed the load!");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, e.getMessage());
                }

                @Override
                public void onNext(RealmResults<Payment> payments) {
                    List<Payment> items = Stream.of(payments).collect(Collectors.toList());
                    remoteDBCall();
                    view.showPaymentItems(groupPaymentsIntoHashMap(items));

                }
            });
    }

}
