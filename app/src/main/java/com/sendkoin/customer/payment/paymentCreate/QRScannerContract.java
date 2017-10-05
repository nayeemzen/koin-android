package com.sendkoin.customer.payment.paymentCreate;

import android.content.Context;

import com.sendkoin.api.Category;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.BasePresenter;
import com.sendkoin.customer.data.payments.Models.inventory.InventoryItemLocal;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.util.List;

/**
 * Created by warefhaque on 5/23/17.
 */

public interface QRScannerContract {

  interface View {
    Context getContext();
    void showInventoryItems(List<Category> groupedInventoryItems);
    void handleOrderItems(List<InventoryOrderItemEntity> inventoryOrderEntities);
    void showOrderDeleted();
  }

  interface Presenter extends BasePresenter {
    void getInventory(String qrToken);
    void getOrderItems();
    void putOrder(String qrToken,InventoryItemLocal inventoryItemLocal);
    void removeAllOrders(Transaction transaction);
  }


}
