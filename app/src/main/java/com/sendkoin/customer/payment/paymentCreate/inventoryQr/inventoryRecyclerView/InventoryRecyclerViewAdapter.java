package com.sendkoin.customer.payment.paymentCreate.inventoryQr.inventoryRecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sendkoin.api.Category;
import com.sendkoin.api.InventoryItem;
import com.sendkoin.api.QrCode;
import com.sendkoin.customer.R;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemCategory;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemLocal;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemMerchant;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryListItem;
import com.sendkoin.customer.payment.paymentCreate.inventoryQr.InventoryQRPaymentFragment;

import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;

import static com.sendkoin.customer.data.payments.Models.inventory.InventoryListItem.CATEGORY;
import static com.sendkoin.customer.data.payments.Models.inventory.InventoryListItem.INVENTORY_ITEM;
import static com.sendkoin.customer.data.payments.Models.inventory.InventoryListItem.MERCHANT_INFO_ITEM;

/**
 * Created by warefhaque on 7/10/17.
 */

public class InventoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public List<InventoryListItem> inventoryListItems = new ArrayList<>();
  private QrCode qrCode;
  private InventoryQRPaymentFragment inventoryQRPaymentFragment;
  private IImageLoader imageLoader;

  /**
   * TODO: 7/19/17 For passing the information to fragment when an item is clicked you can use an
   * interface like RecyclerViewClickListener so that its not specific to a type like InventoryQRPayment,
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
    InventoryItemMerchant inventoryItemMerchant = new InventoryItemMerchant()
        .setMerchantName(qrCode.merchant_name);
    inventoryListItems.add(inventoryItemMerchant);
    // O(n) still
    for (Category category : groupedCategory) {
      inventoryListItems.add(new InventoryItemCategory()
          .setCategoryName(category.category_name));
      for (InventoryItem inventoryItem : category.inventory_items) {
        inventoryListItems.add(new InventoryItemLocal()
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
    return inventoryListItems.get(position).getType();
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
        viewHolder = new InventoryItemViewHolder(inventoryView,
            inventoryQRPaymentFragment,
            inventoryListItems);
        break;
      case MERCHANT_INFO_ITEM:
        View merchantView = LayoutInflater
            .from(inventoryQRPaymentFragment.getActivity())
            .inflate(R.layout.inventory_qr_merchant_info_item, parent, false);
        viewHolder = new MerchantInfoViewHolder(merchantView,imageLoader);
        break;
    }
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (inventoryListItems.get(position).getType()) {
      case CATEGORY:
        CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
        InventoryItemCategory inventoryItemCategory =
            (InventoryItemCategory) inventoryListItems.get(position);
        categoryViewHolder.setItem(inventoryItemCategory);
        break;
      case INVENTORY_ITEM:
        InventoryItemViewHolder inventoryItemViewHolder = (InventoryItemViewHolder) holder;
        InventoryItemLocal inventoryItemLocal = (InventoryItemLocal) inventoryListItems.get(position);
        inventoryItemViewHolder.setItem(inventoryItemLocal);
        break;
      case MERCHANT_INFO_ITEM:
        MerchantInfoViewHolder merchantInfoViewHolder = (MerchantInfoViewHolder) holder;
        merchantInfoViewHolder.divider.setVisibility(View.GONE);
        InventoryItemMerchant inventoryItemMerchant =
            (InventoryItemMerchant) inventoryListItems.get(position);
        merchantInfoViewHolder.setItem(inventoryItemMerchant);
    }
  }

  @Override
  public int getItemCount() {
    return inventoryListItems.size();
  }

}
