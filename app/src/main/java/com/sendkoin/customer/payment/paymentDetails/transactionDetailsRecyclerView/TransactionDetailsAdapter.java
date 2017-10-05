package com.sendkoin.customer.payment.paymentDetails.transactionDetailsRecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
import com.sendkoin.api.TransactionState;
import com.sendkoin.customer.R;
import com.sendkoin.customer.payment.paymentDetails.TransactionDetailsActivity;

import net.glxn.qrgen.android.QRCode;

import java.util.List;

import agency.tango.android.avatarview.IImageLoader;

/**
 * Created by warefhaque on 6/26/17.
 */

public class TransactionDetailsAdapter extends SectionRecyclerViewAdapter<TransactionSummary, Item, TransactionSummaryViewHolder, TransactionItemsViewHolder> {


  private final Context context;
  private IImageLoader imageLoader;

  public TransactionDetailsAdapter(Context context, List<TransactionSummary> sectionItemList, IImageLoader imageLoader) {
    super(context, sectionItemList);
    this.context = context;
    this.imageLoader = imageLoader;
  }

  @Override
  public TransactionSummaryViewHolder onCreateSectionViewHolder(ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.reciept_header, viewGroup, false);
    return new TransactionSummaryViewHolder(view);
  }

  @Override
  public TransactionItemsViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.sale_item, viewGroup, false);
    return new TransactionItemsViewHolder(view);
  }

  @Override
  public void onBindSectionViewHolder(TransactionSummaryViewHolder transactionSummaryViewHolder,
                                      int i,
                                      TransactionSummary transactionSummary) {
    transactionSummaryViewHolder.merchant_name_pay_complete
        .setText(transactionSummary.transaction.merchant.store_name);

    imageLoader.loadImage(
        transactionSummaryViewHolder.merchantLogoPaymentComplete,
        (String) null,
        transactionSummary.transaction.merchant.store_name);

    transactionSummaryViewHolder.saleAmountPayComplete.setText("$" + transactionSummary.transaction.amount.toString());

    if (transactionSummary.transaction.state == TransactionState.PROCESSING
        || transactionSummary.transaction.state == TransactionState.COMPLETE) {
      transactionSummaryViewHolder.transactionStateIcon.setImageResource(R.drawable.trans_approved);
      transactionSummaryViewHolder.transactionStateText.setText("Transaction Approved");
      transactionSummaryViewHolder.transactionStateText.setTextColor(Color.parseColor("#37B3B8"));
    } else {
      transactionSummaryViewHolder.transactionStateIcon.setImageResource(R.drawable.trans_declined);
      transactionSummaryViewHolder.transactionStateText.setText("Transaction Failed");
      transactionSummaryViewHolder.transactionStateText.setTextColor(Color.parseColor("#e74c3c"));
    }

    transactionSummaryViewHolder.confirmationCodeButton.setOnClickListener(view -> {
      DisplayMetrics metrics = context.getResources().getDisplayMetrics();
      float dp = 175f;
      float fpixels = metrics.density * dp;
      int pixels = (int) (fpixels + 0.5f);
      Bitmap qrCodeBitmap = QRCode.from(transactionSummary.transaction.token)
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
  public void onBindChildViewHolder(TransactionItemsViewHolder transactionItemsViewHolder, int i, int i1, Item item) {
    transactionItemsViewHolder.itemName.setText(item.itemName);
    transactionItemsViewHolder.itemPrice.setText("$" + item.price);
    transactionItemsViewHolder.itemQuantity.setText(item.quantity + "X");
  }


}
