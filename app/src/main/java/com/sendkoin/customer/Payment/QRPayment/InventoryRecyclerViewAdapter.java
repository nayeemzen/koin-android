package com.sendkoin.customer.Payment.QRPayment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sendkoin.api.Category;
import com.sendkoin.api.InventoryItem;
import com.sendkoin.customer.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 7/10/17.
 */

public class InventoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  public static final int CATEGORY = 0;
  public static final int INVENTORY = 1;
  List<QRPaymentListItem> qrPaymentListItems;
  private Context context;

  public InventoryRecyclerViewAdapter(List<Category> groupedCategory, Context context) {
    categoryToQRPaymentItems(groupedCategory);
    this.context = context;
  }

  private void categoryToQRPaymentItems(List<Category> groupedCategory) {
    qrPaymentListItems = new ArrayList<>();
    for (Category category : groupedCategory) {
      qrPaymentListItems.add(new CategoryQRPaymentListItemItem().setCategoryName(category.category_name));
      for (InventoryItem inventoryItem : category.inventory_items) {
        qrPaymentListItems.add(new InventoryQRPaymentListItem()
            .setItemName(inventoryItem.name)
            .setItemDescription(inventoryItem.description)
            .setItemImageUrl(inventoryItem.image_url)
            .setItemPrice(inventoryItem.price));
      }
    }
  }

  @Override
  public int getItemViewType(int position) {
    return qrPaymentListItems.get(position).getType();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder = null;
    switch (viewType) {
      case CATEGORY:
        View categoryView = LayoutInflater.from(context).inflate(
            R.layout.profile_child_layout,
            parent,
            false);
        viewHolder = new CategoryViewHolder(categoryView);
        break;
      case INVENTORY:
        View inventoryView = LayoutInflater.from(context).inflate(
            R.layout.profile_child_layout,
            parent,
            false);
        viewHolder = new InventoryItemViewHolder(inventoryView);
        break;
    }
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (qrPaymentListItems.get(position).getType()) {
      case CATEGORY:
        InventoryItemViewHolder inventoryItemViewHolder = (InventoryItemViewHolder) holder;
        // holder.name = listItems.get(position).name;
        break;
      case INVENTORY:
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
        // holder.name = listItems.get(position).name;
        break;
    }
  }

  @Override
  public int getItemCount() {
    return qrPaymentListItems.size();
  }

  public abstract class QRPaymentListItem {
    public abstract int getType();
  }

  public class CategoryQRPaymentListItemItem extends QRPaymentListItem {

    String categoryName;

    public CategoryQRPaymentListItemItem setCategoryName(String categoryName) {
      this.categoryName = categoryName;
      return this;
    }

    @Override
    public int getType() {
      return CATEGORY;
    }
  }

  public class InventoryQRPaymentListItem extends QRPaymentListItem {

    String itemName;
    String itemDescription;
    String itemImageUrl;
    int itemPrice;

    public InventoryQRPaymentListItem setItemName(String itemName) {
      this.itemName = itemName;
      return this;
    }

    public InventoryQRPaymentListItem setItemDescription(String itemDescription) {
      this.itemDescription = itemDescription;
      return this;
    }

    public InventoryQRPaymentListItem setItemImageUrl(String itemImageUrl) {
      this.itemImageUrl = itemImageUrl;
      return this;
    }

    public InventoryQRPaymentListItem setItemPrice(int itemPrice) {
      this.itemPrice = itemPrice;
      return this;
    }

    @Override
    public int getType() {
      return INVENTORY;
    }
  }

  public class InventoryItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public InventoryItemViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

    }
  }

  public class CategoryViewHolder extends RecyclerView.ViewHolder {

    public CategoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
