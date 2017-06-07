package com.sendkoin.customer.Payment.TransactionDetails;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sendkoin.api.SaleItem;
import com.sendkoin.customer.Payment.MainPaymentHistoryAdapter;
import com.sendkoin.customer.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 6/6/17.
 */

public class TransactionDetailsAdapter extends RecyclerView.Adapter {

  public List<SaleItem> saleItems;

  public TransactionDetailsAdapter() {
    this.saleItems = new ArrayList<>();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    LayoutInflater paymentLayoutInflater = LayoutInflater.from(parent.getContext());
    View saleItemView = paymentLayoutInflater.inflate(R.layout.sale_item, parent, false);
    return new SaleItemViewHolder(saleItemView);

  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    SaleItemViewHolder saleItemViewHolder = (SaleItemViewHolder) holder;
    saleItemViewHolder.itemName.setText(saleItems.get(position).name);
    String itemQuantityString = "x" + saleItems.get(position).quantity.toString();
    saleItemViewHolder.itemQuantity.setText(itemQuantityString);
    String itemPriceString = saleItems.get(position).price.toString();
    saleItemViewHolder.itemPrice.setText(itemPriceString);
  }

  @Override
  public int getItemCount() {
    return saleItems.size();
  }

  public class SaleItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.item_name)
    TextView itemName;
    @BindView(R.id.item_price)
    TextView itemPrice;
    @BindView(R.id.item_quantity)
    TextView itemQuantity;

    public SaleItemViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
