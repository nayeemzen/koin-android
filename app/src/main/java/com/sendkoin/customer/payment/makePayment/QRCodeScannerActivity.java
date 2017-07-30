package com.sendkoin.customer.payment.makePayment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.github.orangegangsters.lollipin.lib.PinCompatActivity;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.google.gson.Gson;
import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.Category;
import com.sendkoin.api.InitiateDynamicTransactionRequest;
import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.api.SaleItem;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.payment.makePayment.pinConfirmation.PinConfirmationActivity;
import com.sendkoin.customer.payment.paymentDetails.DetailedReceiptActivity;
import com.sendkoin.customer.R;
import com.sendkoin.customer.payment.makePayment.dynamicQr.DynamicQRPaymentFragment;
import com.sendkoin.customer.payment.makePayment.inventory.DetailedInventoryFragment;
import com.sendkoin.customer.payment.makePayment.inventory.InventoryQRPaymentFragment;
import com.sendkoin.customer.payment.makePayment.inventory.confirmInventoryOrder.ConfirmOrderFragment;
import com.sendkoin.customer.payment.makePayment.staticQr.StaticQPaymentFragment;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by warefhaque on 5/23/17.
 */

// TODO: 7/14/17 One possible refactoring is keeping the Checkout relative layout in the activity
// as both the inventory qr and the cofirm order use it
public class QRCodeScannerActivity extends Activity implements QRScannerContract.View {

  private static final String TAG = QRCodeScannerActivity.class.getSimpleName();
  private static final int REQUEST_CODE_ENABLE = 11;
  @Inject public QRScannerContract.Presenter mPresenter;
  @Inject Gson mGson;

  @BindView(R.id.frame_layout) FrameLayout mFrameLayout;

  IImageLoader imageLoader;
  SweetAlertDialog pDialog;
  private Unbinder unbinder;
  private InventoryQRPaymentFragment inventoryQRPaymentFragment;
  private List<InventoryOrderItemEntity> currentOrderItems;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qrscanner);
    setUpDagger();
    unbinder = ButterKnife.bind(this);
    imageLoader = new PicassoLoader();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (getIntent().hasExtra("qr_string")) {
      showTransactionMethodScreen(getIntent().getStringExtra("qr_string"));
    }
  }

  void setUpDagger() {
    DaggerQRComponent.builder().netComponent(((KoinApplication) getApplication()
        .getApplicationContext())
        .getNetComponent())
        .qRPaymentModule(new QRPaymentModule(this))
        .build()
        .inject(this);
  }

  /**
   * For the Calligraphy fonts
   *
   * @param newBase
   */
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  public Context getContext() {
    return getApplicationContext();
  }

  public void showTransactionMethodScreen(String qrContent) {

    QrCode qrCode = mGson.fromJson(qrContent, QrCode.class);
    Bundle bundle = new Bundle();
    bundle.putByteArray(getString(R.string.qr_code_bundle_identifier), QrCode.ADAPTER.encode(qrCode));
    switch (qrCode.qr_type) {
      case DYNAMIC:
        DynamicQRPaymentFragment dynamicQRPaymentFragment = new DynamicQRPaymentFragment();
        dynamicQRPaymentFragment.setArguments(bundle);
        replaceViewWith(dynamicQRPaymentFragment);
        break;
      case STATIC:
        StaticQPaymentFragment staticQPaymentFragment = new StaticQPaymentFragment();
        staticQPaymentFragment.setArguments(bundle);
        replaceViewWith(staticQPaymentFragment);
        break;
      case INVENTORY_STATIC:
        inventoryQRPaymentFragment = new InventoryQRPaymentFragment();
        inventoryQRPaymentFragment.setArguments(bundle);
        replaceViewWith(inventoryQRPaymentFragment);
        break;
      default:
        throw new UnsupportedOperationException("invalid qr state");
    }
  }

  public void setUpLogo(AvatarView logoView, String name) {
    imageLoader = new PicassoLoader();
    imageLoader.loadImage(logoView, (String) null, name);
  }

  @Override
  public void showTransactionReciept(Transaction transaction) {
    Intent intent = new Intent(QRCodeScannerActivity.this, DetailedReceiptActivity.class);
    intent.putExtra("transaction_token", transaction.token);
    intent.putExtra("from_payment", true);
    startActivity(intent);
  }

  @Override
  public void showTransactionError(String errorMessage) {
    Toast.makeText(this, R.string.static_qr_transaction_error_message, Toast.LENGTH_SHORT).show();
  }

  @Override
  public void showInventoryItems(List<Category> groupedInventoryItems) {
    // call a function in InventoryFragment which will transfer to adapter
    if (inventoryQRPaymentFragment != null) {
      inventoryQRPaymentFragment.setAdapterList(groupedInventoryItems);
    }
  }

  // TODO: 7/13/17 you can make an abstract class that extends from Fragment and then you could have on call
  @Override
  public void handleOrderItems(List<InventoryOrderItemEntity> inventoryOrderEntities) {
    this.currentOrderItems = inventoryOrderEntities;
    PaymentFragment paymentFragment
        = (PaymentFragment) getFragmentManager().findFragmentById(R.id.frame_layout);
    paymentFragment.handleCurrentOrderItems(inventoryOrderEntities);
  }

  @Override
  public void showOrderDeleted() {
//    getFragmentManager().beginTransaction()
//        .replace(R.id.frame_layout, qrScannerFragement)
//        .commit();
  }


  public void replaceViewWith(android.app.Fragment fragment) {
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.frame_layout, fragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      Fragment currentFragment = getFragmentManager().findFragmentById(R.id.frame_layout);
      if (currentFragment instanceof InventoryQRPaymentFragment) {
        if (currentOrderItems != null && currentOrderItems.size() > 0)
          showOrderCancelDialog();
        else
          finish();
      } else if (currentFragment instanceof QRScannerFragment) {
        finish();
      } else {
        getFragmentManager().popBackStack();
      }
    }
    return true;
  }

  private void showOrderCancelDialog() {
    pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
    pDialog.setCancelText("No").setCancelClickListener(dialogInterface -> pDialog.cancel());
    pDialog.setConfirmText("Yes").setConfirmClickListener(dialogInterface ->{
      pDialog.dismissWithAnimation();
      mPresenter.removeAllOrders(null);
    });
    pDialog.setTitleText("Delete Order");
    pDialog.setContentText(getString(R.string.cancel_order_text));
    pDialog.setCancelable(false);
    pDialog.show();
  }


  @Override
  protected void onPause() {
    super.onPause();
    mPresenter.unsubscribe();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (unbinder != null) {
      unbinder.unbind();
    }
  }

  public void populateCheckoutButton(
      List<InventoryOrderItemEntity> inventoryOrderEntities,
      RelativeLayout confirmationlayout,
      TextView totalAmount,
      TextView totalNumItems) {
    confirmationlayout.setVisibility(View.VISIBLE);
    totalAmount
        .setText("BDT " +String.valueOf(calculateTotalOrderAmount(inventoryOrderEntities)));
    totalNumItems.setText(String.valueOf(calculateTotalOrderItems(inventoryOrderEntities)) + " Items");

  }

  public int calculateTotalOrderItems(List<InventoryOrderItemEntity> inventoryOrderEntities) {
    int totalItems = 0;
    for (InventoryOrderItemEntity inventoryOrderItemEntity : inventoryOrderEntities){
      totalItems = (totalItems + (inventoryOrderItemEntity.getItemQuantity().intValue()));
    }
    return totalItems;
  }

  public int calculateTotalOrderAmount(List<InventoryOrderItemEntity> inventoryOrderEntities){
    int totalOrderAmount = 0;
    for (InventoryOrderItemEntity inventoryOrderItemEntity : inventoryOrderEntities){
      totalOrderAmount = (totalOrderAmount + (inventoryOrderItemEntity.getItemPrice().intValue()
          * inventoryOrderItemEntity.getItemQuantity().intValue()));
    }
    return totalOrderAmount;
  }

  public void showpinConfirmationActivity(QrCode qrCode, List<SaleItem> saleItemList) {

    Bundle bundle = new Bundle();
    bundle.putByteArray(QrType.class.getSimpleName(), QrType.ADAPTER.encode(qrCode.qr_type));
    if (qrCode.qr_type == QrType.DYNAMIC) {
      AcceptTransactionRequest acceptTransactionRequest =
          new AcceptTransactionRequest.Builder()
              .idempotence_token(UUID.randomUUID().toString())
              .qr_token(qrCode.qr_token)
              .build();
      bundle.putByteArray(
          AcceptTransactionRequest.class.getSimpleName(),
          AcceptTransactionRequest.ADAPTER.encode(acceptTransactionRequest));
    } else {
      InitiateStaticTransactionRequest initiateStaticTransactionRequest =
          new InitiateStaticTransactionRequest.Builder()
              .sale_items(saleItemList)
              .qr_token(qrCode.qr_token)
              .build();
      bundle.putByteArray(
          InitiateStaticTransactionRequest.class.getSimpleName(),
          InitiateStaticTransactionRequest.ADAPTER.encode(initiateStaticTransactionRequest));
    }
    Intent intent = new Intent(QRCodeScannerActivity.this, PinConfirmationActivity.class);
    intent.putExtra(getString(R.string.bundle_id_sale_summary), bundle);
    startActivity(intent);
  }

}
