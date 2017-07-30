package com.sendkoin.customer.payment.makePayment.inventory.confirmInventoryOrder;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sendkoin.api.QrCode;
import com.sendkoin.api.SaleItem;
import com.sendkoin.customer.R;
import com.sendkoin.customer.payment.makePayment.PaymentFragment;
import com.sendkoin.customer.payment.makePayment.QRCodeScannerActivity;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;
import com.sendkoin.sql.entities.PaymentEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by warefhaque on 7/11/17.
 */

/**
 * Possible changes made could be using the same recycler view as the inventory layout
 * Reasons why that wasnt done:
 * 1) We can have a much larger number of customer views in this 'final' ordering list. this could include:
 * 'People who have ordered Butter Chicken have also ordered Paneer stuff'. Or recommendations right there
 * to get people to order.
 * 2) other promos or details.
 *
 * These would make it a little bit clunky if you had around 5 types of views in that adapter.
 */
public class ConfirmOrderFragment extends PaymentFragment {

  @BindView(R.id.confirm_order_recycler_view) RecyclerView confirmOrderRecyclerView;
  @BindView(R.id.confirm_order_pay_layout) RelativeLayout confirmOrderPayLayout;
  @BindView(R.id.confirm_order_pay_amount) TextView confirmOrderTotalAmount;
  @BindView(R.id.confirm_order_total_items_text_view) TextView confirmOrderTotalItems;

  private QRCodeScannerActivity qrCodeScannerActivity;
  private ConfirmOrderRecyclerViewAdapter confirmOrderAdapter;
  private List<InventoryOrderItemEntity> inventoryOrderEntities;
  private QrCode qrCode;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_confirm_order, container, false);
    ButterKnife.bind(this, view);
    try {
      setUpArguments();
    } catch (IOException e) {
      e.printStackTrace();
    }
    qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
    setUpRecyclerView();
    return view;

  }

  private void setUpArguments() throws IOException {
    Bundle arguments = getArguments();
    byte[] qrCodeBytes = arguments.getByteArray(getString(R.string.qr_code_bundle_identifier));
    this.qrCode = QrCode.ADAPTER.decode(qrCodeBytes);
  }

  @Override
  public void onResume() {
    super.onResume();
    qrCodeScannerActivity.mPresenter.getOrderItems();
  }

  @OnClick(R.id.confirm_order_pay_layout)
  void clickedPay() {
    List<SaleItem> saleItems = convertPaymentEntitiesToSaleItems();
    qrCodeScannerActivity.showpinConfirmationActivity(qrCode, saleItems);
  }

  private List<SaleItem> convertPaymentEntitiesToSaleItems() {
    List<SaleItem> saleItems = new ArrayList<>();
    for (InventoryOrderItemEntity inventoryOrderItemEntity : inventoryOrderEntities) {
      SaleItem saleItem = new SaleItem.Builder()
          .name(inventoryOrderItemEntity.getItemName())
          .price(inventoryOrderItemEntity.getItemPrice().intValue())
          .quantity(inventoryOrderItemEntity.getItemQuantity().intValue())
          .customer_notes(inventoryOrderItemEntity.getItemAdditionalNotes())
          .sale_type(SaleItem.SaleType.QUICK_SALE)
          .build();
      saleItems.add(saleItem);
    }

    return saleItems;
  }

  private void setUpRecyclerView() {
    confirmOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), VERTICAL, false));
    confirmOrderRecyclerView.setHasFixedSize(true);
    confirmOrderAdapter = new ConfirmOrderRecyclerViewAdapter(qrCode,this);
    confirmOrderRecyclerView.setAdapter(confirmOrderAdapter);
  }

  @Override
  public void handleCurrentOrderItems(List<InventoryOrderItemEntity> inventoryOrderEntities) {
    this.inventoryOrderEntities = inventoryOrderEntities;
    confirmOrderAdapter.setListItems(inventoryOrderEntities);
    confirmOrderAdapter.notifyDataSetChanged();

    // populate the checkout order to finalize the order
    qrCodeScannerActivity.populateCheckoutButton(
        inventoryOrderEntities,
        confirmOrderPayLayout,
        confirmOrderTotalAmount,
        confirmOrderTotalItems
    );
  }
}
