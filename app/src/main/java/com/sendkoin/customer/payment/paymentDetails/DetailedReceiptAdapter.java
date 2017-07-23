package com.sendkoin.customer.payment.paymentDetails;

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

import net.glxn.qrgen.android.QRCode;

import java.util.List;

import agency.tango.android.avatarview.IImageLoader;

/**
 * Created by warefhaque on 6/26/17.
 */

public class DetailedReceiptAdapter extends SectionRecyclerViewAdapter<DetailedRecieptHeader, Item, DetailedReceiptHeaderViewHolder, DetailedReceiptSaleItemViewHolder> {


  private final Context context;
  private IImageLoader imageLoader;

  public DetailedReceiptAdapter(Context context, List<DetailedRecieptHeader> sectionItemList, IImageLoader imageLoader) {
    super(context, sectionItemList);
    this.context = context;
    this.imageLoader = imageLoader;
  }

  @Override
  public DetailedReceiptHeaderViewHolder onCreateSectionViewHolder(ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.reciept_header, viewGroup, false);
    return new DetailedReceiptHeaderViewHolder(view);
  }

  @Override
  public DetailedReceiptSaleItemViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int i) {
    View view = LayoutInflater.from(context).inflate(R.layout.sale_item, viewGroup, false);
    return new DetailedReceiptSaleItemViewHolder(view);
  }

  @Override
  public void onBindSectionViewHolder(DetailedReceiptHeaderViewHolder detailedReceiptHeaderViewHolder,
                                      int i,
                                      DetailedRecieptHeader detailedRecieptHeader) {
    detailedReceiptHeaderViewHolder.merchant_name_pay_complete
        .setText(detailedRecieptHeader.transaction.merchant.store_name);

    imageLoader.loadImage(
        detailedReceiptHeaderViewHolder.merchantLogoPaymentComplete,
        (String) null,
        detailedRecieptHeader.transaction.merchant.store_name);

    detailedReceiptHeaderViewHolder.saleAmountPayComplete.setText("$" + detailedRecieptHeader.transaction.amount.toString());

    if (detailedRecieptHeader.transaction.state == TransactionState.PROCESSING
        || detailedRecieptHeader.transaction.state == TransactionState.COMPLETE) {
      detailedReceiptHeaderViewHolder.transactionStateIcon.setImageResource(R.drawable.trans_approved);
      detailedReceiptHeaderViewHolder.transactionStateText.setText("Transaction Approved");
      detailedReceiptHeaderViewHolder.transactionStateText.setTextColor(Color.parseColor("#37B3B8"));
    } else {
      detailedReceiptHeaderViewHolder.transactionStateIcon.setImageResource(R.drawable.trans_declined);
      detailedReceiptHeaderViewHolder.transactionStateText.setText("Transaction Failed");
      detailedReceiptHeaderViewHolder.transactionStateText.setTextColor(Color.parseColor("#e74c3c"));
    }

    detailedReceiptHeaderViewHolder.confirmationCodeButton.setOnClickListener(view -> {
      DisplayMetrics metrics = context.getResources().getDisplayMetrics();
      float dp = 175f;
      float fpixels = metrics.density * dp;
      int pixels = (int) (fpixels + 0.5f);
      Bitmap qrCodeBitmap = QRCode.from(detailedRecieptHeader.transaction.token)
          .withErrorCorrection(ErrorCorrectionLevel.L)
          .withColor(0xFF37B3B8, 0x00000000)
          .withSize(
              pixels,
              pixels)
          .bitmap();
      DetailedReceiptActivity detailedReceiptActivity = (DetailedReceiptActivity) context;
      detailedReceiptActivity.showQRCodeDialog(qrCodeBitmap);
    });
  }

  @Override
  public void onBindChildViewHolder(DetailedReceiptSaleItemViewHolder detailedReceiptSaleItemViewHolder, int i, int i1, Item item) {
    detailedReceiptSaleItemViewHolder.itemName.setText(item.itemName);
    detailedReceiptSaleItemViewHolder.itemPrice.setText("$" + item.price);
    detailedReceiptSaleItemViewHolder.itemQuantity.setText("x" + item.quantity);
  }


}
