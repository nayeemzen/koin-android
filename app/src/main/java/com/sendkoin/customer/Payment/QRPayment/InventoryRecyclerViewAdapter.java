package com.sendkoin.customer.Payment.QRPayment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendkoin.api.Category;
import com.sendkoin.api.InventoryItem;
import com.sendkoin.customer.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 7/10/17.
 */

public class InventoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  public static final int CATEGORY = 0;
  public static final int INVENTORY_ITEM = 1;

  public List<QRPaymentListItem> qrPaymentListItems = new ArrayList<>();
  private Context context;
  private InventoryQRPaymentFragment inventoryQRPaymentFragment;

  public InventoryRecyclerViewAdapter(Context context,
                                      InventoryQRPaymentFragment inventoryQRPaymentFragment) {

    this.inventoryQRPaymentFragment = inventoryQRPaymentFragment;
    this.context = context;
  }

  public void setQrPaymentListItems(List<Category> groupedCategory){
    categoryToQRPaymentItems(groupedCategory);
  }
  private void categoryToQRPaymentItems(List<Category> groupedCategory) {
    for (Category category : groupedCategory) {
      qrPaymentListItems.add(new CategoryQRPaymentListItemItem().setCategoryName(category.category_name));
      for (InventoryItem inventoryItem : category.inventory_items) {
        qrPaymentListItems.add(new InventoryQRPaymentListItem()
            .setItemName(inventoryItem.name)
            .setItemDescription(inventoryItem.description)
            .setItemImageUrl(inventoryItem.image_url)
            .setItemPrice(inventoryItem.price)
            .setAdditionalNotes(null)
            .setQuantity(1)
            .setInventoryItemId(inventoryItem.inventory_item_id));
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
            R.layout.inventory_header,
            parent,
            false);
        viewHolder = new CategoryViewHolder(categoryView);
        break;
      case INVENTORY_ITEM:
        View inventoryView = LayoutInflater.from(context).inflate(
            R.layout.inventory_item,
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
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
        CategoryQRPaymentListItemItem categoryQRPaymentListItemItem =
            (CategoryQRPaymentListItemItem) qrPaymentListItems.get(position);
        // holder.name = listItems.get(position).name;
        categoryViewHolder.inventoryCategoryNameTextView.setText(categoryQRPaymentListItemItem.categoryName);
        break;
      case INVENTORY_ITEM:
        InventoryItemViewHolder inventoryItemViewHolder = (InventoryItemViewHolder) holder;
        InventoryQRPaymentListItem inventoryQRPaymentListItem =
            (InventoryQRPaymentListItem) qrPaymentListItems.get(position);

        //load the image
        Picasso.with(context)
            .load(inventoryQRPaymentListItem.itemImageUrl)
            .fit()
            .into(inventoryItemViewHolder.inventoryItemImageView);
        //set the name of the item
        inventoryItemViewHolder.inventoryItemNameTextView.setText(inventoryQRPaymentListItem.itemName);
        // set the description of the item
        inventoryItemViewHolder.inventoryItemDescriptionTextView
            .setText(inventoryQRPaymentListItem.itemDescription);
        // set the price of the item
        String price = String.valueOf(inventoryQRPaymentListItem.itemPrice);
        inventoryItemViewHolder.inventoryItemPriceTextView.setText("BDT " + price);
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

    public String itemName;
    public String itemDescription;
    public String itemImageUrl;
    public int itemPrice;
    public int quantity;
    public String additionalNotes;
    public long inventoryItemId;

    public InventoryQRPaymentListItem setInventoryItemId(long inventoryItemId) {
      this.inventoryItemId = inventoryItemId;
      return this;
    }

    public InventoryQRPaymentListItem setQuantity(int quantity) {
      this.quantity = quantity;
      return this;
    }

    public InventoryQRPaymentListItem setAdditionalNotes(String additionalNotes) {
      this.additionalNotes = additionalNotes;
      return this;
    }

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
      return INVENTORY_ITEM;
    }
  }

  public class InventoryItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.inventory_item_iv) ImageView inventoryItemImageView;
    @BindView(R.id.inventory_name_tv) TextView inventoryItemNameTextView;
    @BindView(R.id.inventory_description_tv) TextView inventoryItemDescriptionTextView;
    @BindView(R.id.inventory_price_tv) TextView inventoryItemPriceTextView;

    public InventoryItemViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      inventoryQRPaymentFragment
          .setUpView((InventoryQRPaymentListItem) qrPaymentListItems.get(getAdapterPosition()));
    }
  }

  public class CategoryViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.inventory_category_name_tv) TextView inventoryCategoryNameTextView;

    public CategoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
