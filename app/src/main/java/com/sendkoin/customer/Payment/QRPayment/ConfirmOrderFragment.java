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

import com.sendkoin.api.SaleItem;
import com.sendkoin.customer.R;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.util.ArrayList;
import java.util.List;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;
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
public class ConfirmOrderFragment extends Fragment {

  @BindView(R.id.confirm_order_recycler_view) RecyclerView confirmOrderRecyclerView;
  @BindView(R.id.confirm_order_pay_layout) RelativeLayout confirmOrderPayLayout;
  @BindView(R.id.confirm_order_pay_amount) TextView confirmOrderTotalAmount;
  @BindView(R.id.confirm_order_total_items_text_view) TextView confirmOrderTotalItems;

  private QRCodeScannerActivity qrCodeScannerActivity;
  private ConfirmOrderRecyclerViewAdapter confirmOrderAdapter;
  private List<InventoryOrderItemEntity> inventoryOrderEntities;


  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_confirm_order, container, false);
    ButterKnife.bind(this, view);

    qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
    // TODO: 7/14/17 setUpRecyclerView might be a common function and so nay be good to have in
    // base activity and extend from it
    setUpRecyclerView();
    return view;

  }

  @Override
  public void onResume() {
    super.onResume();
    qrCodeScannerActivity.mPresenter.getOrderItems();
  }

  @OnClick(R.id.confirm_order_pay_layout)
  void clickedPay() {
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
    qrCodeScannerActivity.mPresenter.acceptTransaction(
        qrCodeScannerActivity.qrCode,
        saleItems);
  }
  public void showFinalOrder(List<InventoryOrderItemEntity> inventoryOrderEntities) {
    this.inventoryOrderEntities = inventoryOrderEntities;
    confirmOrderAdapter.setListItems(inventoryOrderEntities);
    confirmOrderAdapter.notifyDataSetChanged();
    qrCodeScannerActivity.populateCheckoutButton(
        inventoryOrderEntities,
        confirmOrderPayLayout,
        confirmOrderTotalAmount,
        confirmOrderTotalItems
    );
  }

  private void setUpRecyclerView() {
    confirmOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), VERTICAL, false));
    confirmOrderRecyclerView.setHasFixedSize(true);
    confirmOrderAdapter = new ConfirmOrderRecyclerViewAdapter(getActivity(),this);
    confirmOrderRecyclerView.setAdapter(confirmOrderAdapter);
  }
}
