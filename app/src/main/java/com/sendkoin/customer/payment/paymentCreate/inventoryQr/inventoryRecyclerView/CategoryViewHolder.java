package com.sendkoin.customer.payment.paymentCreate.inventoryQr.inventoryRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sendkoin.customer.R;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemCategory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 10/5/17.
 */

class CategoryViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.inventory_category_name_tv) TextView inventoryCategoryNameTextView;

  public CategoryViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }

  public void setItem(InventoryItemCategory inventoryItemCategory) {
    // holder.name = listItems.get(position).name;
    inventoryCategoryNameTextView.setText(inventoryItemCategory.getCategoryName());
  }
}