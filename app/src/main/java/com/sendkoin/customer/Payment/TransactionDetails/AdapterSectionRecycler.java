package com.sendkoin.customer.Payment.TransactionDetails;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.intrusoft.sectionedrecyclerview.Section;
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.sendkoin.api.Transaction;
import com.sendkoin.api.TransactionState;
import com.sendkoin.customer.Payment.QRPayment.InventoryRecyclerViewAdapter;
import com.sendkoin.customer.R;

import net.glxn.qrgen.android.QRCode;

import java.util.List;

import agency.tango.android.avatarview.IImageLoader;

/**
 * Created by warefhaque on 6/26/17.
 */

public class AdapterSectionRecycler extends SectionRecyclerViewAdapter<SectionHeader, Item, SectionViewHolder, ItemViewHolder> {


  private final Context context;
  private IImageLoader imageLoader;

  public AdapterSectionRecycler(Context context, List<SectionHeader> sectionItemList, IImageLoader imageLoader) {
    super(context, sectionItemList);
    this.context = context;
    this.imageLoader = imageLoader;
  }

  @Override
  public SectionViewHolder onCreateSectionViewHolder(ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.reciept_header, viewGroup, false);
    return new SectionViewHolder(view);
  }

  @Override
  public ItemViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.sale_item, viewGroup, false);
    return new ItemViewHolder(view);
  }

  @Override
  public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder,
                                      int i,
                                      SectionHeader sectionHeader) {
    sectionViewHolder.merchant_name_pay_complete
        .setText(sectionHeader.transaction.merchant.store_name);

    imageLoader.loadImage(
        sectionViewHolder.merchantLogoPaymentComplete,
        (String) null,
        sectionHeader.transaction.merchant.store_name);

    sectionViewHolder.saleAmountPayComplete.setText("$" + sectionHeader.transaction.amount.toString());

    if (sectionHeader.transaction.state == TransactionState.PROCESSING
        || sectionHeader.transaction.state == TransactionState.COMPLETE) {
      sectionViewHolder.transactionStateIcon.setImageResource(R.drawable.trans_approved);
      sectionViewHolder.transactionStateText.setText("Transaction Approved");
      sectionViewHolder.transactionStateText.setTextColor(Color.parseColor("#37B3B8"));
    } else {
      sectionViewHolder.transactionStateIcon.setImageResource(R.drawable.trans_declined);
      sectionViewHolder.transactionStateText.setText("Transaction Failed");
      sectionViewHolder.transactionStateText.setTextColor(Color.parseColor("#e74c3c"));
    }

    sectionViewHolder.confirmationCodeButton.setOnClickListener(view -> {
      DisplayMetrics metrics = context.getResources().getDisplayMetrics();
      float dp = 175f;
      float fpixels = metrics.density * dp;
      int pixels = (int) (fpixels + 0.5f);
      Bitmap qrCodeBitmap = QRCode.from(sectionHeader.transaction.token)
          .withErrorCorrection(ErrorCorrectionLevel.L)
          .withColor(0xFF37B3B8, 0x00000000)
          .withSize(
              pixels,
              pixels)
          .bitmap();
      TransactionDetailsActivity transactionDetailsActivity = (TransactionDetailsActivity) context;
      transactionDetailsActivity.showQRCodeDialog(qrCodeBitmap);
    });
  }

  @Override
  public void onBindChildViewHolder(ItemViewHolder itemViewHolder, int i, int i1, Item item) {
    itemViewHolder.itemName.setText(item.itemName);
    itemViewHolder.itemPrice.setText("$" + item.price);
    itemViewHolder.itemQuantity.setText("x" + item.quantity);
  }


}
