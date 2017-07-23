package com.sendkoin.customer.payment.makePayment.inventory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendkoin.api.Category;
import com.sendkoin.api.InventoryItem;
import com.sendkoin.api.QrCode;
import com.sendkoin.customer.R;
import com.sendkoin.customer.payment.makePayment.QRCodeScannerActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 7/10/17.
 */

public class InventoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  public static final int CATEGORY = 0;
  public static final int INVENTORY_ITEM = 1;
  public static final int MERCHANT_INFO_ITEM = 2;

  public List<QRPaymentListItem> qrPaymentListItems = new ArrayList<>();
  private QrCode qrCode;
  private InventoryQRPaymentFragment inventoryQRPaymentFragment;
  private IImageLoader imageLoader;

  /**
   * TODO: 7/19/17 For passing the information to fragment when an item is clicked you can use an
   * interface like RecyclerViewClickListener so that its not specific to a type like InventoryQRPayment,
   * however is it worth considering that this Adapter is custom made for inventory fragment only?
   */

  public InventoryRecyclerViewAdapter(InventoryQRPaymentFragment inventoryQRPaymentFragment) {
    this.inventoryQRPaymentFragment = inventoryQRPaymentFragment;
    imageLoader = new PicassoLoader();
  }

  public void setArguments(List<Category> groupedCategory, QrCode qrCode) {
    this.qrCode = qrCode;
    categoryToQRPaymentItems(groupedCategory);
  }

  private void categoryToQRPaymentItems(List<Category> groupedCategory) {
    InventoryQRMerchantItem inventoryQRMerchantItem = new InventoryQRMerchantItem()
        .setMerchantName(qrCode.merchant_name);
    qrPaymentListItems.add(inventoryQRMerchantItem);
    // O(n) still
    for (Category category : groupedCategory) {
      qrPaymentListItems.add(new CategoryQRPaymentListItemItem()
          .setCategoryName(category.category_name));
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
        View categoryView = LayoutInflater
            .from(inventoryQRPaymentFragment.getActivity())
            .inflate(R.layout.inventory_header, parent, false);
        viewHolder = new CategoryViewHolder(categoryView);
        break;
      case INVENTORY_ITEM:
        View inventoryView = LayoutInflater
            .from(inventoryQRPaymentFragment.getActivity())
            .inflate(R.layout.inventory_item, parent, false);
        viewHolder = new InventoryItemViewHolder(inventoryView);
        break;
      case MERCHANT_INFO_ITEM:
        View merchantView = LayoutInflater
            .from(inventoryQRPaymentFragment.getActivity())
            .inflate(R.layout.inventory_qr_merchant_info_item, parent, false);
        viewHolder = new MerchantInfoViewHolder(merchantView);
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
        categoryViewHolder.inventoryCategoryNameTextView
            .setText(categoryQRPaymentListItemItem.categoryName);
        break;
      case INVENTORY_ITEM:
        InventoryItemViewHolder inventoryItemViewHolder = (InventoryItemViewHolder) holder;
        inventoryItemViewHolder.orderQuantity.setVisibility(View.GONE);
        InventoryQRPaymentListItem inventoryQRPaymentListItem =
            (InventoryQRPaymentListItem) qrPaymentListItems.get(position);

        //load the image
        Picasso
            .with(inventoryQRPaymentFragment.getActivity())
            .load(inventoryQRPaymentListItem.itemImageUrl)
            .fit()
            .into(inventoryItemViewHolder.inventoryItemImageView);
        //set the name of the item
        inventoryItemViewHolder.inventoryItemNameTextView
            .setText(inventoryQRPaymentListItem.itemName);
        // set the description of the item
        inventoryItemViewHolder.inventoryItemDescriptionTextView
            .setText(inventoryQRPaymentListItem.itemDescription);
        // set the price of the item
        String price = String.valueOf(inventoryQRPaymentListItem.itemPrice);
        inventoryItemViewHolder.inventoryItemPriceTextView.setText("BDT " + price);
        // holder.name = listItems.get(position).name;
        break;
      case MERCHANT_INFO_ITEM:
        MerchantInfoViewHolder merchantInfoViewHolder = (MerchantInfoViewHolder) holder;
        merchantInfoViewHolder.divider.setVisibility(View.GONE);
        InventoryQRMerchantItem inventoryQRMerchantItem =
            (InventoryQRMerchantItem) qrPaymentListItems.get(position);

        merchantInfoViewHolder.merchantName.
            setText(inventoryQRMerchantItem.merchantName);
        imageLoader.loadImage(
            merchantInfoViewHolder.merchantLogo,
            (String) null,
            inventoryQRMerchantItem.merchantName);
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

  class InventoryItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.inventory_item_iv) ImageView inventoryItemImageView;
    @BindView(R.id.inventory_name_tv) TextView inventoryItemNameTextView;
    @BindView(R.id.inventory_description_tv) TextView inventoryItemDescriptionTextView;
    @BindView(R.id.inventory_price_tv) TextView inventoryItemPriceTextView;
    @BindView(R.id.order_quantity) TextView orderQuantity;

    InventoryItemViewHolder(View itemView) {
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

  class CategoryViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.inventory_category_name_tv) TextView inventoryCategoryNameTextView;

    public CategoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  private class InventoryQRMerchantItem extends QRPaymentListItem {

    String merchantName;

    public InventoryQRMerchantItem setMerchantName(String merchantName) {
      this.merchantName = merchantName;
      return this;
    }

    @Override

    public int getType() {
      return MERCHANT_INFO_ITEM;
    }
  }

  class MerchantInfoViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.merchant_logo) AvatarView merchantLogo;
    @BindView(R.id.merchant_name) TextView merchantName;
    @BindView(R.id.inventory_category_line_iv) ImageView divider;

    MerchantInfoViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
