package com.sendkoin.customer.payment.paymentDetails.transactionDetailsRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sendkoin.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 6/26/17.
 */

public class TransactionItemsViewHolder extends RecyclerView.ViewHolder {
  @BindView(R.id.item_name)
  TextView itemName;
  @BindView(R.id.item_price)
  TextView itemPrice;
  @BindView(R.id.item_quantity)
  TextView itemQuantity;

  public TransactionItemsViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }
}
