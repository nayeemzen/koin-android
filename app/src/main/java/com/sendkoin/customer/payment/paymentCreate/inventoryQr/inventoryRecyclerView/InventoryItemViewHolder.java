package com.sendkoin.customer.payment.paymentCreate.inventoryQr.inventoryRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendkoin.customer.R;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemLocal;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryListItem;
import com.sendkoin.customer.payment.paymentCreate.inventoryQr.InventoryQRPaymentFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 10/5/17.
 */

class InventoryItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  @BindView(R.id.inventory_item_iv) ImageView inventoryItemImageView;
  @BindView(R.id.inventory_name_tv) TextView inventoryItemNameTextView;
  @BindView(R.id.inventory_description_tv) TextView inventoryItemDescriptionTextView;
  @BindView(R.id.inventory_price_tv) TextView inventoryItemPriceTextView;
  private InventoryQRPaymentFragment inventoryQRPaymentFragment;
  private List<InventoryListItem> inventoryListItems;

  InventoryItemViewHolder(View itemView,
                          InventoryQRPaymentFragment inventoryQRPaymentFragment,
                          List<InventoryListItem> inventoryListItems) {
    super(itemView);
    this.inventoryQRPaymentFragment = inventoryQRPaymentFragment;
    this.inventoryListItems = inventoryListItems;
    ButterKnife.bind(this, itemView);
    itemView.setOnClickListener(this);
  }

  public void setItem(InventoryItemLocal inventoryItemLocal) {
    //load the image
    Picasso
        .with(inventoryQRPaymentFragment.getActivity())
        .load(inventoryItemLocal.itemImageUrl)
        .fit()
        .into(inventoryItemImageView);
    //set the name of the item
    inventoryItemNameTextView
        .setText(inventoryItemLocal.itemName);
    // set the description of the item
    inventoryItemDescriptionTextView
        .setText(inventoryItemLocal.itemDescription);
    // set the price of the item
    String price = String.valueOf(inventoryItemLocal.itemPrice);
    inventoryItemPriceTextView.setText("BDT " + price);
  }

  @Override
  public void onClick(View view) {
    inventoryQRPaymentFragment.setUpDetailedInventoryFragmentView(
        (InventoryItemLocal) inventoryListItems.get(getAdapterPosition()));
  }
}