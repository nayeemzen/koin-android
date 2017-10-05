package com.sendkoin.customer.payment.paymentCreate;

import android.app.Fragment;

import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.util.List;

/**
 * Extended by InventoryQRFragment & DetailedInventoryFragment
 * @see com.sendkoin.customer.payment.paymentCreate.inventoryQr.DetailedInventoryFragment
 * @see com.sendkoin.customer.payment.paymentCreate.inventoryQr.InventoryQRPaymentFragment
 */

public abstract class PaymentFragment extends Fragment {

  /**
   * Recieves orders to populate the "Confirm Order" and "Add to Order"
   * @param orderItemEntities - list of order items customer selected
   */
  public abstract void handleCurrentOrderItems(List<InventoryOrderItemEntity>orderItemEntities);
}
