package com.sendkoin.customer.Payment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.google.gson.Gson;
import com.sendkoin.customer.Data.Payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.MainActivity;
import com.sendkoin.customer.Payment.QRPayment.QRCodeScannerActivity;
import com.sendkoin.customer.R;
import com.sendkoin.sql.entities.PaymentEntity;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.annimon.stream.Collectors.groupingBy;
import static com.annimon.stream.Collectors.toList;

/**
 * Created by warefhaque on 5/20/17.
 */

public class MainPaymentFragment extends android.support.v4.app.Fragment
    implements MainPaymentContract.View {

  private static final String TAG = "MainPaymentFragment";
  public static final String FIRST_TIME = "first_time";
  @Inject MainPaymentContract.Presenter mPresenter;
  @Inject Gson gson;

  @BindView(R.id.create_payment) FloatingActionButton createPayment;
  @BindView(R.id.payment_history) RecyclerView recyclerViewPaymentHistory;
  @BindView(R.id.main_payment_content) CoordinatorLayout mainPaymentContent;
  @BindView(R.id.no_transactions_text) TextView noTransactionsText;

  private SweetAlertDialog pDialog;
  private int lastVisibleItem, totalItemCount;
  private boolean isLoading;

  public Unbinder unbinder;

  MainPaymentHistoryAdapter mMainPaymentHistoryAdapter;
  @Inject
  LocalPaymentDataStore localPaymentDataStore;
  private int visibleThreshold = 1;

  enum UIState {
    PAYMENTS,
    NO_PAYMENTS
  }

  public void setUiState(UIState uiState) {
    switch (uiState) {
      case PAYMENTS:
        noTransactionsText.setVisibility(View.GONE);
        recyclerViewPaymentHistory.setVisibility(View.VISIBLE);
        break;
      case NO_PAYMENTS:
//        recyclerViewPaymentHistory.setVisibility(View.INVISIBLE);
        noTransactionsText.setText("You currently have no transactions.");
        noTransactionsText.setVisibility(View.VISIBLE);
        break;

    }
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setUpDagger();
    setupRecyclerView();
    listenForListScroll();
//    localPaymentDataStore.deleteAllPayments();
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
  public void onDestroyView() {
    super.onDestroyView();
    unbinder.unbind();
    mPresenter.closeRealm();
  }

  @Override
  public void onPause() {
    super.onPause();
    mPresenter.unsubscribe();
  }

  @Override
  public void onResume() {
    super.onResume();
    mPresenter.subscribe();

  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main_payment, container, false);
    unbinder = ButterKnife.bind(this, view);

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
    mMainPaymentHistoryAdapter = new MainPaymentHistoryAdapter((MainActivity) getActivity());
    recyclerViewPaymentHistory.setAdapter(mMainPaymentHistoryAdapter);
  }


  @Override
  public void showPaymentItems(List<PaymentEntity> paymentEntities) {
    UIState uiState = (paymentEntities.size() == 0) ? UIState.NO_PAYMENTS : UIState.PAYMENTS;
    setUiState(uiState);
    LinkedHashMap<String, List<PaymentEntity>> payments = groupTransactionsByCreatedAt(paymentEntities);
    mMainPaymentHistoryAdapter.setGroupedList(payments);
    mMainPaymentHistoryAdapter.notifyDataSetChanged();
    isLoading = false;
  }

  public LinkedHashMap<String, List<PaymentEntity>> groupTransactionsByCreatedAt(
      List<PaymentEntity> transactionEntities) {
    return Stream.of(transactionEntities)
        .collect(groupingBy(transactionEntity -> getCreatedAt(transactionEntity.getCreatedAt()),
            LinkedHashMap::new, toList()));
  }

  public String getCreatedAt(long createdAt) {
    Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    cal.setTimeInMillis(createdAt);
    String dateString = DateFormat.format("EEEE, MMMM d", cal).toString();
    int day = cal.get(Calendar.DAY_OF_MONTH);
    return dateString + getDateSuffix(day);
  }

  private String getDateSuffix(int day) {
    switch (day) {
      case 1:
        return "st";
      case 2:
        return "nd";
      case 3:
        return "rd";
      case 31:
        return "st";
      default:
        return "th";
    }
  }


  @Override
  public Context getApplicationContext() {
    return getActivity().getApplicationContext();
  }


  private void listenForListScroll() {
    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerViewPaymentHistory.getLayoutManager();
    recyclerViewPaymentHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        totalItemCount = linearLayoutManager.getItemCount();
        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        if (dy > 0 && createPayment.getVisibility() == View.VISIBLE) {
          // hide the FAB when scrolling down
          createPayment.hide();
        } else if (dy < 0 && createPayment.getVisibility() != View.VISIBLE) {
          createPayment.show();
        }

        if (dy > 0) {
          if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
            if (mPresenter != null) {
                mPresenter.loadTransactionsFromServer(false);
            }

            isLoading = true;
          }
        }
      }
    });
  }


}
