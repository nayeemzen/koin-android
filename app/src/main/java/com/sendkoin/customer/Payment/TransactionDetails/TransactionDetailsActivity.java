package com.sendkoin.customer.Payment.TransactionDetails;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.api.TransactionDetail;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.R;

import net.glxn.qrgen.android.QRCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by warefhaque on 6/5/17.
 */

public class TransactionDetailsActivity
    extends AppCompatActivity
    implements TransactionDetailsContract.View {

  @Inject
  TransactionDetailsContract.Presenter mPresenter;
  @BindView(R.id.sectioned_rv)
  RecyclerView transactionRecyclerView;
  IImageLoader imageLoader;
  AdapterSectionRecycler adapterRecycler;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.detailed_reciept_layout);
    ButterKnife.bind(this);
    setUpDagger();
    setUpRecyclerView();
    String transactionToken = getIntent().getStringExtra("transaction_token");
    imageLoader = new PicassoLoader();
    mPresenter.fetchTransactionDetails(transactionToken);

  }

  private void setUpRecyclerView() {
    transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
    transactionRecyclerView.setHasFixedSize(true);
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

    List<SaleItem> saleItems = transactionDetail.sale_items;
    SaleItem.SaleType saleType = saleItems.get(0).sale_type;
    List<Item> items = new ArrayList<>();
    if (saleType != SaleItem.SaleType.STATIC_QR){
      // TODO: 6/26/17 WAREF - remove this version issue
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        items = saleItems.stream().map(saleItem -> new Item()
            .setPrice(saleItem.price)
            .setItemName(saleItem.name)
            .setQuantity(saleItem.quantity)).collect(Collectors.toList());
      }
    }
    List<SectionHeader> sections = new ArrayList<>();
    sections.add(new SectionHeader()
        .setChildList(items)
        .setTransaction(transactionDetail.transaction));

    adapterRecycler = new AdapterSectionRecycler(this, sections, imageLoader);
    transactionRecyclerView.setAdapter(adapterRecycler);
  }
}
