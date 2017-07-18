package com.sendkoin.customer.Payment.QRPayment;

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

import com.sendkoin.api.Category;
import com.sendkoin.customer.R;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by warefhaque on 7/9/17.
 */

public class InventoryQRPaymentFragment extends Fragment {
  @BindView(R.id.inventory_recycler_view) RecyclerView inventoryRecyclerView;
  @BindView(R.id.checkout_total_order_layout) RelativeLayout checkoutConfirmationLayout;
  @BindView(R.id.checkout_num_items_text_view) TextView checkoutTotalItemsTextView;
  @BindView(R.id.checkout_amount_text_view) TextView checkoutTotalAmountTextView;

  QRCodeScannerActivity qrCodeScannerActivity;
  public InventoryRecyclerViewAdapter inventoryRecyclerViewAdapter;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_inventory_qr, container, false);
    ButterKnife.bind(this, view);
    setUpRecyclerView();
    qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
    qrCodeScannerActivity.mPresenter.getInventory(qrCodeScannerActivity.qrCode.qr_token);
    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    qrCodeScannerActivity.mPresenter.getOrderItems();
  }

  @OnClick(R.id.checkout_total_order_layout)
  void clickedCheckout() {
    ConfirmOrderFragment confirmOrderFragment = new ConfirmOrderFragment();
    qrCodeScannerActivity.replaceViewWith(confirmOrderFragment);
  }

  public void updateCheckoutView(List<InventoryOrderItemEntity> inventoryOrderEntities) {
    if (inventoryOrderEntities.size() > 0) {
      qrCodeScannerActivity.populateCheckoutButton(
          inventoryOrderEntities,
          checkoutConfirmationLayout,
          checkoutTotalAmountTextView,
          checkoutTotalItemsTextView);
    }
  }

  public void setUpRecyclerView() {
    inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), VERTICAL, false));
    inventoryRecyclerView.setHasFixedSize(true);
    inventoryRecyclerViewAdapter = new InventoryRecyclerViewAdapter(getActivity(),this);
    inventoryRecyclerView.setAdapter(inventoryRecyclerViewAdapter);
  }

  public void setAdapterList(List<Category> groupedInventoryItems) {
    inventoryRecyclerViewAdapter.setQrPaymentListItems(groupedInventoryItems);
    inventoryRecyclerViewAdapter.notifyDataSetChanged();
  }

  public void setUpView(InventoryRecyclerViewAdapter.InventoryQRPaymentListItem inventoryQRPaymentListItem){
    DetailedInventoryFragment detailedInventoryFragment =
        new DetailedInventoryFragment(qrCodeScannerActivity, inventoryQRPaymentListItem,this);
    qrCodeScannerActivity.replaceViewWith(detailedInventoryFragment);
  }
}
