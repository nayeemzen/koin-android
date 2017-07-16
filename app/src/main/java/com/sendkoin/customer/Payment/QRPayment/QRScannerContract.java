package com.sendkoin.customer.Payment.QRPayment;

import android.content.Context;

import com.sendkoin.api.Category;
import com.sendkoin.api.InventoryItem;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.BasePresenter;
import com.sendkoin.sql.entities.CurrentOrderEntity;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.util.List;

/**
 * Created by warefhaque on 5/23/17.
 */

public interface QRScannerContract {

  interface View {
    Context getContext();
    void showTransactionReciept(Transaction transaction);
    void showTransactionError(String errorMessage);
    void showInventoryItems(List<Category> groupedInventoryItems);
    void handleOrderItems(List<InventoryOrderItemEntity> inventoryOrderEntities);
    void showOrderDeleted();
  }

  interface Presenter extends BasePresenter {
    void acceptTransaction(QrCode qrCode, List<SaleItem> saleItemList);
    void getInventory(String qrToken);
    void getOrderItems();
    void putOrder(String qrToken, InventoryRecyclerViewAdapter.InventoryQRPaymentListItem inventoryQRPaymentListItem);
    void removeAllOrders(Transaction transaction);
  }


}
