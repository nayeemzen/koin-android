package com.sendkoin.customer.Payment.TransactionDetails;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.sendkoin.api.Transaction;
import com.sendkoin.api.TransactionDetail;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by warefhaque on 6/5/17.
 */

public class TransactionDetailsActivity
    extends AppCompatActivity
    implements TransactionDetailsContract.View{

  @Inject
  TransactionDetailsContract.Presenter mPresenter;
  @BindView(R.id.recyclerView_sale_item)
  RecyclerView transactionRecyclerView;
  @BindView(R.id.tv_date)
  TextView transactionDate;
  @BindView(R.id.tv_merchant_name)
  TextView merchantName;
  @BindView(R.id.tv_sales_total)
  TextView salesTotal;
  private TransactionDetailsAdapter transactionDetailsAdapter;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_details_transaction);
    ButterKnife.bind(this);
    setUpDagger();
    setUpRecyclerView();
    String transactionToken = getIntent().getStringExtra("transaction_token");
    mPresenter.fetchTransactionDetails(transactionToken);

  }

  private void setUpRecyclerView() {
    transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
    transactionRecyclerView.setHasFixedSize(true);
    transactionDetailsAdapter = new TransactionDetailsAdapter();
    transactionRecyclerView.setAdapter(transactionDetailsAdapter);
  }

  /**
   * For the Calligraphy fonts
   *
   * @param newBase
   */
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }


  private void setUpDagger() {
    DaggerTransactionDetailsComponent.builder().netComponent(((KoinApplication) getApplication()
        .getApplicationContext())
        .getNetComponent())
        .transactionDetailsModule(new TransactionDetailsModule(this))
        .build()
        .inject(this);
  }

  @Override
  public void showDetailedTransaction(TransactionDetail transactionDetail) {
    Transaction transaction = transactionDetail.transaction;
    setupHeader(transaction);
    transactionDetailsAdapter.saleItems = transactionDetail.sale_items;
    transactionDetailsAdapter.notifyDataSetChanged();
  }

  private void setupHeader(Transaction transaction) {
    RealmTransaction realmTransaction = RealmTransaction.toRealmTransaction(transaction);
    transactionDate.setText(realmTransaction.getCreatedAt());
    merchantName.setText(realmTransaction.getMerchantName());
    String salesAmountString = "$"+realmTransaction.getAmount().toString();
    salesTotal.setText(salesAmountString);
  }


}
