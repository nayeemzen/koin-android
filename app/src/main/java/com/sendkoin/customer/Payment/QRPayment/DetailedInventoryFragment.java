package com.sendkoin.customer.Payment.QRPayment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendkoin.customer.R;
import com.sendkoin.sql.entities.CurrentOrderEntity;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sendkoin.customer.Payment.QRPayment.InventoryRecyclerViewAdapter.*;

/**
 * Created by warefhaque on 7/11/17.
 */

public class DetailedInventoryFragment extends Fragment {

  private static final String TAG = DetailedInventoryFragment.class.getSimpleName();
  @BindView(R.id.detailed_item_description) TextView detailedItemDescriptionTextView;
  @BindView(R.id.detailed_item_count) TextView detailedItemOrderCount;
  @BindView(R.id.detailed_item_confirm_text) TextView detailedItemConfirmTextView;
  @BindView(R.id.detailed_item_confirm_amount) TextView detailedItemConfirmAmountTextView;
  @BindView(R.id.detailed_item_add_instructions) EditText detailedItemAddInstructions;
  @BindView(R.id.detailed_inventory_item_iv) ImageView detailedItemImageView;
  @BindView(R.id.inventory_category_name_tv) TextView detailedItemNameTextView;


  private QRCodeScannerActivity qrCodeScannerActivity;
  private InventoryRecyclerViewAdapter.InventoryQRPaymentListItem inventoryQRPaymentListItem;
  private InventoryQRPaymentFragment inventoryQRPaymentFragment;
  private String imageUrl;
  private int price;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.inventory_detailed_item, container, false);
    ButterKnife.bind(this, view);
    qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
    return view;
  }

  private void setUpViews(String name, String description, String imageUrl, int price, int quantity) {
    this.imageUrl = imageUrl;
    this.price = price;
    detailedItemAddInstructions.setText("");
    Picasso.with(qrCodeScannerActivity)
        .load(imageUrl)
        .fit()
        .into(detailedItemImageView);
    detailedItemNameTextView.setText(name);
    detailedItemDescriptionTextView.setText(description);
    detailedItemOrderCount.setText(String.valueOf(quantity));
    detailedItemConfirmAmountTextView.setText("$ "+ String.valueOf(price * quantity));
    detailedItemConfirmTextView.setText("Add "+String.valueOf(quantity) +" to order");
  }

  public DetailedInventoryFragment(QRCodeScannerActivity qrCodeScannerActivity,
                                   InventoryRecyclerViewAdapter.InventoryQRPaymentListItem inventoryQRPaymentListItem,
                                   InventoryQRPaymentFragment inventoryQRPaymentFragment) {
    this.qrCodeScannerActivity = qrCodeScannerActivity;
    this.inventoryQRPaymentListItem = inventoryQRPaymentListItem;
    this.inventoryQRPaymentFragment = inventoryQRPaymentFragment;
  }

  @OnClick(R.id.detailed_item_plus)
  void clickedIncreaseAmount(){
    int currentAmount = Integer.valueOf(detailedItemOrderCount.getText().toString());
    inventoryQRPaymentListItem.quantity = currentAmount + 1;
    updateViewsAndAdd();
  }

  @OnClick(R.id.detailed_item_minus)
  void clickedReduceAmount(){
    int currentAmount = Integer.valueOf(detailedItemOrderCount.getText().toString());
    if (currentAmount > 1) {
      inventoryQRPaymentListItem.quantity = currentAmount - 1;
      updateViewsAndAdd();
    }
  }

  public void updateViewsAndAdd(){
    String newAmountString = String.valueOf(inventoryQRPaymentListItem.quantity);
    detailedItemOrderCount.setText(newAmountString);
    detailedItemConfirmTextView.setText("Add " + newAmountString + " to order");
    int newPrice = inventoryQRPaymentListItem.itemPrice * inventoryQRPaymentListItem.quantity;
    String newPriceString = String.valueOf(newPrice);
    detailedItemConfirmAmountTextView.setText("$ " + newPriceString);
  }
  @OnClick(R.id.detailed_item_confirm_layout)
  void clickedConfirmItem() {
    qrCodeScannerActivity.mPresenter.putOrder(
        qrCodeScannerActivity.qrCode.qr_token,
        inventoryQRPaymentListItem);
    qrCodeScannerActivity.getFragmentManager().popBackStack();
  }

  @Override
  public void onResume() {
    super.onResume();
    qrCodeScannerActivity.mPresenter.getOrderItems();
  }

  public void updateItemInformation(List<InventoryOrderItemEntity> inventoryOrderEntities){
    boolean foundOrder = false;
    for (InventoryOrderItemEntity inventoryOrderItemEntity : inventoryOrderEntities) {
      if (inventoryOrderItemEntity.getItemName().equals(inventoryQRPaymentListItem.itemName)){
        setUpViews(
            inventoryOrderItemEntity.getItemName(),
            inventoryOrderItemEntity.getItemDescription(),
            inventoryOrderItemEntity.getItemImageUrl(),
            inventoryOrderItemEntity.getItemPrice().intValue(),
            inventoryOrderItemEntity.getItemQuantity().intValue());
        foundOrder = true;
      }
    }

    if (!foundOrder) {
      setUpViews(
          inventoryQRPaymentListItem.itemName,
          inventoryQRPaymentListItem.itemDescription,
          inventoryQRPaymentListItem.itemImageUrl,
          inventoryQRPaymentListItem.itemPrice,
          1);
    }
  }
}
