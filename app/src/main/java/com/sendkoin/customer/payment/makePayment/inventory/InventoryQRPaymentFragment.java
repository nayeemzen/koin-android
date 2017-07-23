package com.sendkoin.customer.payment.makePayment.inventory;

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
import android.widget.Toast;

import com.sendkoin.api.Category;
import com.sendkoin.api.QrCode;
import com.sendkoin.customer.R;
import com.sendkoin.customer.payment.makePayment.inventory.confirmInventoryOrder.ConfirmOrderFragment;
import com.sendkoin.customer.payment.makePayment.QRCodeScannerActivity;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.io.IOException;
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
  private InventoryRecyclerViewAdapter inventoryRecyclerViewAdapter;
  private QrCode qrCode;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_inventory_qr, container, false);
    ButterKnife.bind(this, view);
    try {
      setUpArguments();
      setUpRecyclerView();
      qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
      // fetch the total inventory for the merchant
      qrCodeScannerActivity.mPresenter.getInventory(qrCode.qr_token);
    } catch (IOException e) {
      e.printStackTrace();
      Toast.makeText(
          qrCodeScannerActivity,
          "There was an error scanning. Please go back and try again",
          Toast.LENGTH_SHORT).show();
    }

    return view;
  }

  private void setUpArguments() throws IOException {
    Bundle arguments = getArguments();
    byte[] qrCodeBytes = arguments.getByteArray("qr_code");
    this.qrCode = QrCode.ADAPTER.decode(qrCodeBytes);
  }

  @Override
  public void onResume() {
    super.onResume();
    // this is to update the checkout button at the bottom with the total amount
    qrCodeScannerActivity.mPresenter.getOrderItems();
  }

  @OnClick(R.id.checkout_total_order_layout)
  void clickedCheckout() {
    ConfirmOrderFragment confirmOrderFragment = new ConfirmOrderFragment();
    Bundle bundle = new Bundle();
    bundle.putByteArray(getString(R.string.qr_code_bundle_identifier), qrCode.encode());
    confirmOrderFragment.setArguments(bundle);
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
    inventoryRecyclerViewAdapter = new InventoryRecyclerViewAdapter(this);
    inventoryRecyclerView.setAdapter(inventoryRecyclerViewAdapter);
  }

  public void setAdapterList(List<Category> groupedInventoryItems) {
    inventoryRecyclerViewAdapter.setArguments(groupedInventoryItems, qrCode);
    inventoryRecyclerViewAdapter.notifyDataSetChanged();
  }

  public void setUpView(InventoryRecyclerViewAdapter.InventoryQRPaymentListItem inventoryQRPaymentListItem){
    DetailedInventoryFragment detailedInventoryFragment =
        new DetailedInventoryFragment(qrCodeScannerActivity, inventoryQRPaymentListItem,qrCode);
    qrCodeScannerActivity.replaceViewWith(detailedInventoryFragment);
  }
}
