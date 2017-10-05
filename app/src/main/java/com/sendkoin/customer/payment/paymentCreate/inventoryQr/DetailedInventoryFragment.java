package com.sendkoin.customer.payment.paymentCreate.inventoryQr;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendkoin.api.QrCode;
import com.sendkoin.customer.R;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemLocal;
import com.sendkoin.customer.payment.paymentCreate.PaymentFragment;
import com.sendkoin.customer.payment.paymentCreate.QRCodeScannerActivity;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by warefhaque on 7/11/17.
 */

public class DetailedInventoryFragment extends PaymentFragment {

  private static final String TAG = DetailedInventoryFragment.class.getSimpleName();
  @BindView(R.id.detailed_item_description) TextView detailedItemDescriptionTextView;
  @BindView(R.id.detailed_item_count) TextView detailedItemOrderCount;
  @BindView(R.id.detailed_item_confirm_text) TextView detailedItemConfirmTextView;
  @BindView(R.id.detailed_item_confirm_amount) TextView detailedItemConfirmAmountTextView;
  @BindView(R.id.detailed_item_add_instructions) EditText detailedItemAddInstructions;
  @BindView(R.id.detailed_inventory_item_iv) ImageView detailedItemImageView;
  @BindView(R.id.inventory_category_name_tv) TextView detailedItemNameTextView;


  private QRCodeScannerActivity qrCodeScannerActivity;
  private InventoryItemLocal inventoryItemLocal;
  private QrCode qrCode;

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
                                  InventoryItemLocal inventoryItemLocal,
                                   QrCode qrCode) {
    this.qrCodeScannerActivity = qrCodeScannerActivity;
    this.inventoryItemLocal = inventoryItemLocal;
    this.qrCode = qrCode;
  }

  @OnClick(R.id.detailed_item_plus)
  void clickedIncreaseAmount(){
    int currentAmount = Integer.valueOf(detailedItemOrderCount.getText().toString());
    inventoryItemLocal.quantity = currentAmount + 1;
    updateViewsAndAdd();
  }

  @OnClick(R.id.detailed_item_minus)
  void clickedReduceAmount(){
    int currentAmount = Integer.valueOf(detailedItemOrderCount.getText().toString());
    if (currentAmount > 1) {
      inventoryItemLocal.quantity = currentAmount - 1;
      updateViewsAndAdd();
    }
  }

  public void updateViewsAndAdd(){
    String newAmountString = String.valueOf(inventoryItemLocal.quantity);
    detailedItemOrderCount.setText(newAmountString);
    detailedItemConfirmTextView.setText("Add " + newAmountString + " to order");
    int newPrice = inventoryItemLocal.itemPrice * inventoryItemLocal.quantity;
    String newPriceString = String.valueOf(newPrice);
    detailedItemConfirmAmountTextView.setText("$ " + newPriceString);
  }

  @OnClick(R.id.detailed_item_confirm_layout)
  void clickedConfirmItem() {
    qrCodeScannerActivity.mPresenter.putOrder(
        qrCode.qr_token, inventoryItemLocal);
    qrCodeScannerActivity.getFragmentManager().popBackStack();
  }

  @Override
  public void onResume() {
    super.onResume();
    qrCodeScannerActivity.mPresenter.getOrderItems();
  }


  /**
   * Pulls the customers orders from the local Db. If the customer has ordered this item before
   * show the quantity he/she selected else show the default quantity
   * todo : change the name of PaymentListItem... + pass PaymentListItem in the parameter to handleCurrentOrderItems and do if else
   * @param inventoryOrderEntities
   */
  @Override
  public void handleCurrentOrderItems(List<InventoryOrderItemEntity> inventoryOrderEntities) {
    boolean foundOrder = false;
    for (InventoryOrderItemEntity inventoryOrderItemEntity : inventoryOrderEntities) {
      if (inventoryOrderItemEntity.getItemName().equals(inventoryItemLocal.itemName)){
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
          inventoryItemLocal.itemName,
          inventoryItemLocal.itemDescription,
          inventoryItemLocal.itemImageUrl,
          inventoryItemLocal.itemPrice,
          1);
    }
  }
}
