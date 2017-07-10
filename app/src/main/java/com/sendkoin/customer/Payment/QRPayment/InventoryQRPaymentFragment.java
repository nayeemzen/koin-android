package com.sendkoin.customer.Payment.QRPayment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sendkoin.api.Category;
import com.sendkoin.customer.Profile.ProfileRecyclerAdapter;
import com.sendkoin.customer.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by warefhaque on 7/9/17.
 */

public class InventoryQRPaymentFragment extends Fragment {

  @BindView(R.id.inventory_recycler_view) RecyclerView inventoryRecyclerView;

  QRCodeScannerActivity qrCodeScannerActivity;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_inventory_qr, container, false);
    ButterKnife.bind(this, view);
    qrCodeScannerActivity = (QRCodeScannerActivity) getActivity();
    qrCodeScannerActivity.mPresenter.getInventory(qrCodeScannerActivity.qrCode.qr_token);
    return view;
  }

  public void setUpRecyclerView(
      List<Category> groupedCategory,
      Context context) {
    inventoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), VERTICAL, false));
    inventoryRecyclerView.setHasFixedSize(true);
    InventoryRecyclerViewAdapter inventoryRecyclerViewAdapter = new InventoryRecyclerViewAdapter(
        groupedCategory, context);
    inventoryRecyclerView.setAdapter(inventoryRecyclerViewAdapter);
  }
}
