package com.sendkoin.customer.payment.paymentCreate.inventoryQr.confirmInventoryOrder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendkoin.api.QrCode;
import com.sendkoin.customer.R;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * todo need to break adapter like inventoryRecyclerView
 */

public class ConfirmOrderRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private QrCode qrCode;
  private ConfirmOrderFragment confirmOrderFragment;
  public static final int CONFIRM_ORDER_SALE_ITEM = 0;
  public static final int CONFIRM_ORDER_MERCHANT_ITEM = 1;
  List<ConfirmOrderItem> confirmOrderItems = new ArrayList<>();
  IImageLoader imageLoader;

  public ConfirmOrderRecyclerViewAdapter(QrCode qrCode, ConfirmOrderFragment confirmOrderFragment) {
    this.qrCode = qrCode;
    this.confirmOrderFragment = confirmOrderFragment;
    imageLoader = new PicassoLoader();
  }

  public void setListItems(List<InventoryOrderItemEntity> inventoryOrderEntities) {

    // 1. set the merchant name
    ConfirmOrderMerchantInfoItem confirmOrderMerchantInfoItem = new ConfirmOrderMerchantInfoItem()
        .setMerchantName(qrCode.merchant_name);
    confirmOrderItems.add(confirmOrderMerchantInfoItem);

    //2. setup the list of items that the guy bought
    for (InventoryOrderItemEntity inventoryOrderItemEntity : inventoryOrderEntities) {
      ConfirmOrderSaleItem confirmOrderSaleItem = new ConfirmOrderSaleItem()
          .setItemName(inventoryOrderItemEntity.getItemName())
          .setItemPrice(inventoryOrderItemEntity.getItemPrice().intValue())
          .setItemDescription(inventoryOrderItemEntity.getItemDescription())
          .setInventoryItemId(inventoryOrderItemEntity.getOrderItemId())
          .setQuantity(inventoryOrderItemEntity.getItemQuantity().intValue())
          .setItemImageUrl(inventoryOrderItemEntity.getItemImageUrl())
          .setAdditionalNotes(inventoryOrderItemEntity.getItemAdditionalNotes());
      confirmOrderItems.add(confirmOrderSaleItem);
    }
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder = null;
    switch (viewType) {
      case CONFIRM_ORDER_SALE_ITEM:
        View saleItemView = LayoutInflater
            .from(confirmOrderFragment.getActivity())
            .inflate(R.layout.item_confirm_order, parent, false);
        viewHolder = new SaleItemViewHolder(saleItemView);
        break;
      case CONFIRM_ORDER_MERCHANT_ITEM:
        View mercahtnItemView = LayoutInflater
            .from(confirmOrderFragment.getActivity())
            .inflate(R.layout.inventory_qr_merchant_info_item, parent,false);
        viewHolder = new MerchantInfoViewHolder(mercahtnItemView);
        break;
    }
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (confirmOrderItems.get(position).getType()) {
      case CONFIRM_ORDER_SALE_ITEM:
        SaleItemViewHolder saleItemViewHolder = (SaleItemViewHolder) holder;
        saleItemViewHolder.itemQuantity.setVisibility(View.VISIBLE);
        ConfirmOrderSaleItem confirmOrderSaleItem =
            (ConfirmOrderSaleItem) confirmOrderItems.get(position);
        saleItemViewHolder.itemName.
            setText(confirmOrderSaleItem.itemName);
        saleItemViewHolder.itemQuantity
            .setText(String.valueOf(confirmOrderSaleItem.quantity)+"X");
        saleItemViewHolder.itemPrice
            .setText("BDT "+String.valueOf(confirmOrderSaleItem.itemPrice));
        Picasso.with(confirmOrderFragment.getActivity())
            .load(confirmOrderSaleItem.itemImageUrl)
            .fit()
            .into(saleItemViewHolder.itemImage);
        saleItemViewHolder.itemDescription
            .setText(confirmOrderSaleItem.itemDescription);
        break;
      case CONFIRM_ORDER_MERCHANT_ITEM:
        MerchantInfoViewHolder merchantInfoViewHolder = (MerchantInfoViewHolder) holder;
        merchantInfoViewHolder.divider.setVisibility(View.VISIBLE);
        ConfirmOrderMerchantInfoItem confirmOrderMerchantInfoItem =
            (ConfirmOrderMerchantInfoItem) confirmOrderItems.get(position);

        merchantInfoViewHolder.merchantName.setText(confirmOrderMerchantInfoItem.merchantName);
        imageLoader.loadImage(
            merchantInfoViewHolder.merchantLogo,
            (String)null,
            confirmOrderMerchantInfoItem.merchantName);
        break;
    }
  }

  @Override
  public int getItemViewType(int position) {
    return confirmOrderItems.get(position).getType();
  }

  @Override
  public int getItemCount() {
    return confirmOrderItems.size();
  }

  public abstract class ConfirmOrderItem {
    public abstract int getType();
  }

  public class ConfirmOrderSaleItem extends ConfirmOrderItem {

    public String itemName;
    public String itemDescription;
    public String itemImageUrl;
    public int itemPrice;
    public int quantity;
    public String additionalNotes;
    public long inventoryItemId;

    public ConfirmOrderSaleItem setInventoryItemId(long inventoryItemId) {
      this.inventoryItemId = inventoryItemId;
      return this;
    }

    public ConfirmOrderSaleItem setQuantity(int quantity) {
      this.quantity = quantity;
      return this;
    }

    public ConfirmOrderSaleItem setAdditionalNotes(String additionalNotes) {
      this.additionalNotes = additionalNotes;
      return this;
    }

    public ConfirmOrderSaleItem setItemName(String itemName) {
      this.itemName = itemName;
      return this;
    }

    public ConfirmOrderSaleItem setItemDescription(String itemDescription) {
      this.itemDescription = itemDescription;
      return this;
    }

    public ConfirmOrderSaleItem setItemImageUrl(String itemImageUrl) {
      this.itemImageUrl = itemImageUrl;
      return this;
    }

    public ConfirmOrderSaleItem setItemPrice(int itemPrice) {
      this.itemPrice = itemPrice;
      return this;
    }

    @Override
    public int getType() {
      return CONFIRM_ORDER_SALE_ITEM;
    }
  }

  public class ConfirmOrderMerchantInfoItem extends ConfirmOrderItem {

    String merchantName;

    public ConfirmOrderMerchantInfoItem setMerchantName(String merchantName) {
      this.merchantName = merchantName;
      return this;
    }

    @Override

    public int getType() {
      return CONFIRM_ORDER_MERCHANT_ITEM;
    }
  }

  public class SaleItemViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.inventory_item_iv) ImageView itemImage;
    @BindView(R.id.inventory_name_tv) TextView itemName;
    @BindView(R.id.inventory_description_tv) TextView itemDescription;
    @BindView(R.id.inventory_price_tv) TextView itemPrice;
    @BindView(R.id.order_quantity) TextView itemQuantity;

    public SaleItemViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public class MerchantInfoViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.merchant_logo) AvatarView merchantLogo;
    @BindView(R.id.merchant_name) TextView merchantName;
    @BindView(R.id.inventory_category_line_iv) ImageView divider;

    public MerchantInfoViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
