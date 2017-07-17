package com.sendkoin.customer.Payment.TransactionDetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.annimon.stream.Stream;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.TransactionDetail;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.MainActivity;
import com.sendkoin.customer.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;
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
  SweetAlertDialog pDialog;

  String transactionToken;
  boolean fromPayment = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.detailed_reciept_layout);
    ButterKnife.bind(this);
    setUpDagger();
    setUpRecyclerView();
    if (getIntent().hasExtra("transaction_token"))
      transactionToken = getIntent().getStringExtra("transaction_token");
    if (getIntent().hasExtra("from_payment"))
      fromPayment = getIntent().getBooleanExtra("from_payment", false);

    imageLoader = new PicassoLoader();
    mPresenter.fetchTransactionDetails(transactionToken);

  }

  private void setUpRecyclerView() {
    transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this, VERTICAL, false));
    transactionRecyclerView.setHasFixedSize(true);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      if (fromPayment)
        startActivity(new Intent(TransactionDetailsActivity.this, MainActivity.class));
      else
        finish();
    }
    return true;
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
    List<Item> items;

    items = Stream.of(saleItems).map(saleItem -> new Item()
        .setPrice(saleItem.price)
        .setItemName(saleItem.name)
        .setQuantity(saleItem.quantity)).toList();

    List<SectionHeader> sections = new ArrayList<>();
    sections.add(new SectionHeader()
        .setChildList(items)
        .setTransaction(transactionDetail.transaction));

    adapterRecycler = new AdapterSectionRecycler(this, sections, imageLoader);
    transactionRecyclerView.setAdapter(adapterRecycler);
  }

  public void showQRCodeDialog(Bitmap qrCode) {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    LayoutInflater inflater = this.getLayoutInflater();
    View dialogView = inflater.inflate(R.layout.conformation_code_dialog, null);
    dialogBuilder.setView(dialogView);

    ImageView qrCodeImageView = (ImageView) dialogView.findViewById(R.id.confirmation_qr_code);
    FancyButton doneButton = (FancyButton) dialogView.findViewById(R.id.done_button);

    qrCodeImageView.setImageBitmap(qrCode);
    setUpDoneButton(doneButton);

    AlertDialog alertDialog = dialogBuilder.create();
    alertDialog.show();

    doneButton.setOnClickListener(view -> alertDialog.dismiss());
  }

  public void setUpDoneButton(FancyButton doneButton) {
    doneButton.setText("Done");
    doneButton.setBackgroundColor(Color.parseColor("#37B3B8"));
    doneButton.setFocusBackgroundColor(Color.parseColor("#008489"));
    doneButton.setBorderColor(Color.parseColor("#37B3B8"));
    doneButton.setTextSize(16);
    doneButton.setTextColor(Color.parseColor("#FFFFFF"));
    doneButton.setRadius(10);
    doneButton.setCustomTextFont("Nunito-Regular.ttf");
  }
}
