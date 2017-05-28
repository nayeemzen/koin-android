package com.sendkoin.koincustomer.Payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sendkoin.koincustomer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.koincustomer.Data.Payments.Models.Payment;
import com.sendkoin.koincustomer.KoinApplication;
import com.sendkoin.koincustomer.Payment.QRPayment.QRCodeScannerActivity;
import com.sendkoin.koincustomer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by warefhaque on 5/20/17.
 */

public class MainPaymentFragment extends android.support.v4.app.Fragment implements MainPaymentContract.View
{

    @Inject
    MainPaymentContract.Presenter mPresenter;
    @BindView(R.id.create_payment)
    FloatingActionButton createPayment;
    @BindView(R.id.payment_history)
    RecyclerView recyclerViewPaymentHistory;

    MainPaymentHistoryAdapter mMainPaymentHistoryAdapter;
  @Inject LocalPaymentDataStore localPaymentDataStore;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpDagger();
        setupRecyclerView();
    }

    private void setUpDagger() {
        DaggerMainPaymentComponent.builder().netComponent(((KoinApplication) getActivity()
                .getApplicationContext())
                .getNetComponent())
        .mainPaymentModule(new MainPaymentModule(this))
        .build()
        .inject(this);
    }

  @Override
  public void onDestroy() {
    super.onDestroy();

  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.loadItems();
  }

  @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_payment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.create_payment)
    public void setCreatePayment(View view){
        Intent intent = new Intent(getActivity(), QRCodeScannerActivity.class);
        startActivity(intent);

    }

    public void setupRecyclerView(){
      recyclerViewPaymentHistory.addItemDecoration(new DividerItemDecoration(getActivity()));
      recyclerViewPaymentHistory.setLayoutManager(new LinearLayoutManager(getActivity(), VERTICAL, false));
      recyclerViewPaymentHistory.setHasFixedSize(true);
      mMainPaymentHistoryAdapter = new MainPaymentHistoryAdapter();
      recyclerViewPaymentHistory.setAdapter(mMainPaymentHistoryAdapter);
    }

  @Override
  public void showPaymentItems(HashMap<String, List<Payment>> payments) {
    mMainPaymentHistoryAdapter.setGroupedList(payments);
    mMainPaymentHistoryAdapter.notifyDataSetChanged();
  }





}
