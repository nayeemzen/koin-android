package com.sendkoin.customer.payment.paymentCreate.inventoryQr.confirmOrder;

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
import com.sendkoin.customer.data.payments.Local.LocalOrderDataStore;
import com.sendkoin.customer.payment.paymentCreate.PaymentFragment;
import com.sendkoin.customer.payment.paymentCreate.QrScannerActivity;
import com.sendkoin.customer.payment.paymentCreate.QrScannerContract;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Possible changes made could be using the same recycler view as the inventory layout
 * Reasons why that wasnt done:
 * 1) We can have a much larger number of customer views in this 'final' ordering list. this could include:
 * 'People who have ordered Butter Chicken have also ordered Paneer stuff'. Or recommendations right there
 * to get people to order.
 * 2) other promos or details.
 */
public class ConfirmOrderFragment extends PaymentFragment {

  @BindView(R.id.confirm_order_recycler_view) RecyclerView confirmOrderRecyclerView;
  @BindView(R.id.confirm_order_pay_layout) RelativeLayout confirmOrderPayLayout;
  @BindView(R.id.confirm_order_pay_amount) TextView confirmOrderTotalAmount;
  @BindView(R.id.confirm_order_total_items_text_view) TextView confirmOrderTotalItems;

  private QrScannerActivity qrScannerActivity;
  private ConfirmOrderRecyclerViewAdapter confirmOrderAdapter;
  private List<InventoryOrderItemEntity> inventoryOrderEntities;
  private QrCode qrCode;
  private QrScannerContract.Presenter presenter;
  private LocalOrderDataStore localOrderDataStore;

  public ConfirmOrderFragment(QrScannerContract.Presenter presenter, LocalOrderDataStore localOrderDataStore) {
    this.presenter = presenter;
    this.localOrderDataStore = localOrderDataStore;
  }

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
    qrScannerActivity = (QrScannerActivity) getActivity();
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
    qrScannerActivity.mPresenter.getOrderItems();
  }

  @OnClick(R.id.confirm_order_pay_layout)
  void clickedPay() {
    presenter.createInitiateTransactionRequest(qrCode,
        localOrderDataStore.toSaleItems(inventoryOrderEntities));
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
    qrScannerActivity.populateCheckoutButton(
        inventoryOrderEntities,
        confirmOrderPayLayout,
        confirmOrderTotalAmount,
        confirmOrderTotalItems
    );
  }
}
