package com.sendkoin.customer.payment.paymentCreate.inventoryQr.inventoryRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendkoin.customer.R;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemMerchant;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 10/5/17.
 */

class MerchantInfoViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.merchant_logo) AvatarView merchantLogo;
  @BindView(R.id.merchant_name) TextView merchantName;
  @BindView(R.id.inventory_category_line_iv) ImageView divider;
  private IImageLoader imageLoader;

  MerchantInfoViewHolder(View itemView, IImageLoader imageLoader) {
    super(itemView);
    this.imageLoader = imageLoader;
    ButterKnife.bind(this, itemView);
  }

  public void setItem(InventoryItemMerchant inventoryItemMerchant) {
    merchantName.setText(inventoryItemMerchant.getMerchantName());
    imageLoader.loadImage(merchantLogo, (String) null, inventoryItemMerchant.getMerchantName());
  }
}
