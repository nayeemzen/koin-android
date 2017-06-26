package com.sendkoin.customer.Payment.TransactionDetails;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sendkoin.customer.R;

import agency.tango.android.avatarview.views.AvatarView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 6/26/17.
 */

public class SectionViewHolder extends RecyclerView.ViewHolder{

  @BindView(R.id.merchant_logo_pay_complete) AvatarView merchantLogoPaymentComplete;
  @BindView(R.id.merchant_name_pay_complete) TextView merchant_name_pay_complete;
  @BindView(R.id.sale_amount_payment_complete) TextView saleAmountPayComplete;
  @BindView(R.id.transaction_state_icon) ImageView transactionStateIcon;
  @BindView(R.id.transaction_state_text) TextView transactionStateText;
  @BindView(R.id.barcode_image) ImageView barcodeImageView;
  @BindView(R.id.transaction_list_header_layout) RelativeLayout transactionHeaderTitle;
  @BindView(R.id.transaction_list_header_text) TextView transactionHeaderText;
  @BindView(R.id.barcode_layout) RelativeLayout barcodeLayout;
  public SectionViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }
}
