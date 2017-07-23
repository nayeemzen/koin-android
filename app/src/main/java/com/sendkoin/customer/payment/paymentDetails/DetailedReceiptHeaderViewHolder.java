package com.sendkoin.customer.payment.paymentDetails;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sendkoin.customer.R;

import agency.tango.android.avatarview.views.AvatarView;
import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by warefhaque on 6/26/17.
 */
public class DetailedReceiptHeaderViewHolder extends RecyclerView.ViewHolder{

  @BindView(R.id.merchant_logo_pay_complete) AvatarView merchantLogoPaymentComplete;
  @BindView(R.id.merchant_name_pay_complete) TextView merchant_name_pay_complete;
  @BindView(R.id.sale_amount_payment_complete) TextView saleAmountPayComplete;
  @BindView(R.id.transaction_state_icon) ImageView transactionStateIcon;
  @BindView(R.id.transaction_state_text) TextView transactionStateText;
  @BindView(R.id.barcode_image) ImageView barcodeImageView;
  @BindView(R.id.transaction_list_header_layout) RelativeLayout transactionHeaderTitle;
  @BindView(R.id.transaction_list_header_text) TextView transactionHeaderText;
  @BindView(R.id.barcode_layout) RelativeLayout barcodeLayout;
  @BindView(R.id.confirmation_code_button) FancyButton confirmationCodeButton;

  public DetailedReceiptHeaderViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
    setUpConfirmButton();
  }


  private void setUpConfirmButton() {
    confirmationCodeButton.setText("Confirmation Code");
    confirmationCodeButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
    confirmationCodeButton.setFocusBackgroundColor(Color.parseColor("#008489"));
    confirmationCodeButton.setBorderWidth(5);
    confirmationCodeButton.setBorderColor(Color.parseColor("#37B3B8"));
    confirmationCodeButton.setTextSize(16);
    confirmationCodeButton.setTextColor(Color.parseColor("#37B3B8"));
    confirmationCodeButton.setRadius(20);
    confirmationCodeButton.setPadding(10, 20, 10, 20);
    confirmationCodeButton.setCustomTextFont("Nunito-Regular.ttf");
  }
}
