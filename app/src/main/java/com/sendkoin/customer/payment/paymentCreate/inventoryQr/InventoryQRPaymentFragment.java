package com.sendkoin.customer.payment.paymentCreate.inventoryQr;

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
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemLocal;
import com.sendkoin.customer.payment.paymentCreate.PaymentFragment;
import com.sendkoin.customer.payment.paymentCreate.QrScannerActivity;
import com.sendkoin.customer.payment.paymentCreate.QrScannerContract;
import com.sendkoin.customer.payment.paymentCreate.inventoryQr.confirmOrder.ConfirmOrderFragment;
import com.sendkoin.customer.payment.paymentCreate.inventoryQr.inventoryRecyclerView.InventoryRecyclerViewAdapter;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Shows the list of Inventory items the merchant has
 */

public class InventoryQRPaymentFragment extends PaymentFragment {
  @BindView(R.id.inventory_recycler_view) RecyclerView inventoryRecyclerView;
  @BindView(R.id.checkout_total_order_layout) RelativeLayout checkoutConfirmationLayout;
  @BindView(R.id.checkout_num_items_text_view) TextView checkoutTotalItemsTextView;
  @BindView(R.id.checkout_amount_text_view) TextView checkoutTotalAmountTextView;

  QrScannerActivity qrScannerActivity;
  private InventoryRecyclerViewAdapter inventoryRecyclerViewAdapter;
  private QrCode qrCode;
  private QrScannerContract.Presenter presenter;

  public InventoryQRPaymentFragment(QrScannerContract.Presenter presenter) {
    this.presenter = presenter;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_inventory_qr, container, false);
    ButterKnife.bind(this, view);
    try {
      setUpArguments();
      setUpRecyclerView();
      qrScannerActivity = (QrScannerActivity) getActivity();
      // fetch the total inventory for the merchant
      qrScannerActivity.mPresenter.getInventory(qrCode.qr_token);
    } catch (IOException e) {
      e.printStackTrace();
      Toast.makeText(qrScannerActivity,
          "There was an error scanning. Please go back and try again",
          Toast.LENGTH_SHORT).show();
    }

    return view;
  }

  /**
   * Recieves the Qr token required by the ConfirmOrderFragment to then proceed
   * with processing payment
   * @throws IOException
   */
  private void setUpArguments() throws IOException {
    Bundle arguments = getArguments();
    byte[] qrCodeBytes = arguments.getByteArray("qr_code");
    this.qrCode = QrCode.ADAPTER.decode(qrCodeBytes);
  }

  @Override
  public void onResume() {
    super.onResume();
    // this is to update the checkout button at the bottom with the total amount
    qrScannerActivity.mPresenter.getOrderItems();
  }

  /**
   * Switch the view to ConfirmOrderFragment with the help of QrScannerActivity
   */
  @OnClick(R.id.checkout_total_order_layout)
  void clickedCheckout() {
    ConfirmOrderFragment confirmOrderFragment = new ConfirmOrderFragment(presenter);
    Bundle bundle = new Bundle();
    bundle.putByteArray(getString(R.string.qr_code_bundle_identifier), qrCode.encode());
    confirmOrderFragment.setArguments(bundle);
    qrScannerActivity.replaceViewWith(confirmOrderFragment);
  }

  public void setUpRecyclerView() {
    inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), VERTICAL, false));
    inventoryRecyclerView.setHasFixedSize(true);
    inventoryRecyclerViewAdapter = new InventoryRecyclerViewAdapter(this);
    inventoryRecyclerView.setAdapter(inventoryRecyclerViewAdapter);
  }

  /**
   * Show the list of Inventory Items to the user
   * @param groupedInventoryItems - inventory items grouped by category
   */
  public void setAdapterList(List<Category> groupedInventoryItems) {
    inventoryRecyclerViewAdapter.setArguments(groupedInventoryItems, qrCode);
    inventoryRecyclerViewAdapter.notifyDataSetChanged();
  }

  /**
   * Show the DetailedInventoryFragment when an item on the inventory list is clicked
   * @param inventoryItemLocal - item to show (friend rice, chicken etc)
   */
  public void setUpDetailedInventoryFragmentView(InventoryItemLocal inventoryItemLocal){
    DetailedInventoryFragment detailedInventoryFragment =
        new DetailedInventoryFragment(qrScannerActivity, inventoryItemLocal,qrCode);
    qrScannerActivity.replaceViewWith(detailedInventoryFragment);
  }

  /**
   * Called by the QrScannerActivity when the updated list of orders received. Mainly
   * to populate the check out button after the customer has set quantity and addtional details
   * @param inventoryOrderEntities - list of order items the customer picked
   */
  @Override
  public void handleCurrentOrderItems(List<InventoryOrderItemEntity> inventoryOrderEntities) {
    if (inventoryOrderEntities.size() > 0) {
      qrScannerActivity.populateCheckoutButton(
          inventoryOrderEntities,
          checkoutConfirmationLayout,
          checkoutTotalAmountTextView,
          checkoutTotalItemsTextView);
    }
  }
}
