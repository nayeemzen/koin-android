package com.sendkoin.customer.payment.makePayment;

import android.app.Fragment;

import com.sendkoin.sql.entities.InventoryOrderItemEntity;
import com.sendkoin.sql.entities.PaymentEntity;

import java.util.List;

/**
 * Created by warefhaque on 7/23/17.
 */

public abstract class PaymentFragment extends Fragment {

  public abstract void handleCurrentOrderItems(List<InventoryOrderItemEntity>orderItemEntities);
}
