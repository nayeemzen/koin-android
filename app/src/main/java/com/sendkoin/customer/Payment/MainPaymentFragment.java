package com.sendkoin.customer.Payment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.sendkoin.api.SaleItem;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.Payment.QRPayment.QRCodeScannerActivity;
import com.sendkoin.customer.R;
import com.sendkoin.customer.Utility.ByteToken;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by warefhaque on 5/20/17.
 */

public class MainPaymentFragment extends android.support.v4.app.Fragment implements MainPaymentContract.View {

  private static final String TAG = "MainPaymentFragment";
  @Inject
  MainPaymentContract.Presenter mPresenter;
  @BindView(R.id.create_payment)
  FloatingActionButton createPayment;
  @BindView(R.id.payment_history)
  RecyclerView recyclerViewPaymentHistory;
  @Inject
  Gson gson;

  public Unbinder unbinder;

  MainPaymentHistoryAdapter mMainPaymentHistoryAdapter;
  @Inject
  LocalPaymentDataStore localPaymentDataStore;

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setUpDagger();
    setupRecyclerView();
    listenForListScroll();
    dummyDataforMerChantPost();
    Log.d(TAG, "ON ACTIVITY CREATED!");

//        mPresenter.deleteAll();
  }

  private void dummyDataforMerChantPost() {
    Log.i(TAG, "Idempotence Token: " + ByteToken.generate());
    Log.i(TAG, "Created at: " + Long.toString(new Date().getTime()));
    long id = 1233456782;
    SaleItem saleItem = new SaleItem(id, "Vanilla Latte", 345, SaleItem.SaleType.INVENTORY);
    Log.i(TAG, gson.toJson(saleItem));

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
    unbinder.unbind();

  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.loadItemsFromDatabase();
    Log.d(TAG, "ON RESUME!");

  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main_payment, container, false);
    unbinder = ButterKnife.bind(this, view);
    Log.d(TAG, "ON CREATE VIEW!");

    return view;
  }

  @OnClick(R.id.create_payment)
  public void setCreatePayment(View view) {
    Intent intent = new Intent(getActivity(), QRCodeScannerActivity.class);
    startActivity(intent);

  }

  public void setupRecyclerView() {
    recyclerViewPaymentHistory.addItemDecoration(new DividerItemDecoration(getActivity()));
    recyclerViewPaymentHistory.setLayoutManager(new LinearLayoutManager(getActivity(), VERTICAL, false));
    recyclerViewPaymentHistory.setHasFixedSize(true);
    mMainPaymentHistoryAdapter = new MainPaymentHistoryAdapter();
    recyclerViewPaymentHistory.setAdapter(mMainPaymentHistoryAdapter);
  }

  @Override
  public void showPaymentItems(HashMap<String, List<RealmTransaction>> payments) {

    mMainPaymentHistoryAdapter.setGroupedList(payments);
    mMainPaymentHistoryAdapter.notifyDataSetChanged();
  }


  private void listenForListScroll() {
    recyclerViewPaymentHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy > 0 && createPayment.getVisibility() == View.VISIBLE) {
          createPayment.hide();
        } else if (dy < 0 && createPayment.getVisibility() != View.VISIBLE) {
          createPayment.show();
        }
      }
    });
  }


}
