package com.sendkoin.customer.payment;

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
import com.sendkoin.customer.data.payments.Local.LocalPaymentDataStore;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.MainActivity;
import com.sendkoin.customer.payment.makePayment.QRCodeScannerActivity;
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

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;
import static com.annimon.stream.Collectors.groupingBy;
import static com.annimon.stream.Collectors.toList;

/**
 * Created by warefhaque on 5/20/17.
 */

public class MainPaymentFragment extends android.support.v4.app.Fragment
    implements MainPaymentContract.View {

  @Inject MainPaymentContract.Presenter mPresenter;
  @Inject Gson mGson;
  @Inject LocalPaymentDataStore localPaymentDataStore;

  @BindView(R.id.fab_create_payment) FloatingActionButton mFabCreatePayment;
  @BindView(R.id.recycler_view_main_payment) RecyclerView mRecyclerView;
  @BindView(R.id.coordinator_layout_main) CoordinatorLayout mCoordinatorLayoutMain;
  @BindView(R.id.text_no_payments) TextView mTextNotPayments;

  private int mLastVisibleItem, mTotalItemCount; // for the hiding and displaying of the fab
  private boolean mIsLoading;
  private Unbinder mUnbinder;
  private MainPaymentAdapter mMainPaymentAdapter;
  private int mVisibleThreshold = 1; // for the scrolling detector

  private enum UIState {
    PAYMENTS,
    NO_PAYMENTS
  }

  public void setUiState(UIState uiState) {
    switch (uiState) {
      case PAYMENTS:
        mTextNotPayments.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        break;
      case NO_PAYMENTS:
        mTextNotPayments.setText("You currently have no transactions.");
        mTextNotPayments.setVisibility(View.VISIBLE);
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
    mUnbinder.unbind();
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
    mUnbinder = ButterKnife.bind(this, view);

    return view;
  }

  @OnClick(R.id.fab_create_payment)
  public void clickedCreatePayment(View view) {
    Intent intent = new Intent(getActivity(), QRCodeScannerActivity.class);
    startActivity(intent);

  }


  public void setupRecyclerView() {
    mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), VERTICAL, false));
    mRecyclerView.setHasFixedSize(true);
    mMainPaymentAdapter = new MainPaymentAdapter((MainActivity) getActivity());
    mRecyclerView.setAdapter(mMainPaymentAdapter);
  }


  @Override
  public void showPaymentItems(List<PaymentEntity> paymentEntities) {
    UIState uiState = (paymentEntities.size() == 0) ? UIState.NO_PAYMENTS : UIState.PAYMENTS;
    setUiState(uiState);
    LinkedHashMap<String, List<PaymentEntity>> payments = groupTransactionsByCreatedAt(paymentEntities);
    mMainPaymentAdapter.setGroupedList(payments);
    mMainPaymentAdapter.notifyDataSetChanged();
    mIsLoading = false;
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
    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        mTotalItemCount = linearLayoutManager.getItemCount();
        mLastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        if (dy > 0 && mFabCreatePayment.getVisibility() == View.VISIBLE) {
          // hide the FAB when scrolling down
          mFabCreatePayment.hide();
        } else if (dy < 0 && mFabCreatePayment.getVisibility() != View.VISIBLE) {
          mFabCreatePayment.show();
        }

        if (dy > 0) {
          if (!mIsLoading && mTotalItemCount <= (mLastVisibleItem + mVisibleThreshold)) {
            if (mPresenter != null) {
              mPresenter.loadTransactionsFromServer(false);
            }

            mIsLoading = true;
          }
        }
      }
    });
  }


}