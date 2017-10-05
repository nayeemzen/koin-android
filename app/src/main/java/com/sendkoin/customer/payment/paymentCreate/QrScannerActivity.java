package com.sendkoin.customer.payment.paymentCreate;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.google.gson.Gson;
import com.sendkoin.api.AcceptTransactionRequest;
import com.sendkoin.api.Category;
import com.sendkoin.api.InitiateStaticTransactionRequest;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.QrType;
import com.sendkoin.api.SaleItem;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.payment.paymentCreate.dynamicQr.DynamicQrPaymentFragment;
import com.sendkoin.customer.payment.paymentCreate.pinConfirmation.PinConfirmationActivity;
import com.sendkoin.customer.R;
import com.sendkoin.customer.payment.paymentCreate.inventoryQr.InventoryQRPaymentFragment;
import com.sendkoin.customer.payment.paymentCreate.staticQr.StaticQrPaymentFragment;
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
 * Decides:
 *   - whether to inflate static, dynamic, inventory static payment fragments
 *   - whether to tell PinConfirmationActivity to process static or dynamic payments
 *   - whether to show/hide order confirmation button
 *
 * @see QrScannerContract
 * @see PaymentFragment
 * @see DynamicQrPaymentFragment
 * @see StaticQrPaymentFragment
 * @see InventoryQRPaymentFragment
 */

public class QrScannerActivity extends Activity implements QrScannerContract.View {

  private static final String TAG = QrScannerActivity.class.getSimpleName();
  @Inject public QrScannerContract.Presenter mPresenter;
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
    DaggerQrScannerComponent.builder().netComponent(((KoinApplication) getApplication()
        .getApplicationContext())
        .getNetComponent())
        .qRPaymentModule(new QrScannerModule(this))
        .build()
        .inject(this);
  }


  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  @Override
  public Context getContext() {
    return getApplicationContext();
  }

  /**
   * Decides which Fragment to inflate based on the kind of payment this is
   * @param qrContent - QrCode coming in from QrScannerFragment
   * @see QrScannerFragment
   */
  public void showTransactionMethodScreen(String qrContent) {

    QrCode qrCode = mGson.fromJson(qrContent, QrCode.class);
    Bundle bundle = new Bundle();
    bundle.putByteArray(getString(R.string.qr_code_bundle_identifier), QrCode.ADAPTER.encode(qrCode));
    switch (qrCode.qr_type) {
      case DYNAMIC:
        DynamicQrPaymentFragment dynamicQrPaymentFragment = new DynamicQrPaymentFragment();
        dynamicQrPaymentFragment.setArguments(bundle);
        replaceViewWith(dynamicQrPaymentFragment);
        break;
      case STATIC:
        StaticQrPaymentFragment staticQrPaymentFragment = new StaticQrPaymentFragment();
        staticQrPaymentFragment.setArguments(bundle);
        replaceViewWith(staticQrPaymentFragment);
        break;
      case INVENTORY_STATIC:
        inventoryQRPaymentFragment = new InventoryQRPaymentFragment();
        inventoryQRPaymentFragment.setArguments(bundle);
        replaceViewWith(inventoryQRPaymentFragment);
        break;
      default:
        throw new UnsupportedOperationException("Invalid QR state");
    }
  }

  public void setUpLogo(AvatarView logoView, String name) {
    imageLoader = new PicassoLoader();
    imageLoader.loadImage(logoView, (String) null, name);
  }

  /**
   * Tells InventoryQrPaymentFragment to populate its list with the merchant's inventory
   * @param groupedInventoryItems - merchant inventory grouped by categories
   * @see InventoryQRPaymentFragment
   */
  @Override
  public void showInventoryItems(List<Category> groupedInventoryItems) {
    if (inventoryQRPaymentFragment != null) {
      inventoryQRPaymentFragment.setAdapterList(groupedInventoryItems);
    }
  }

  @Override
  public void handleOrderItems(List<InventoryOrderItemEntity> inventoryOrderEntities) {
    this.currentOrderItems = inventoryOrderEntities;
    PaymentFragment paymentFragment
        = (PaymentFragment) getFragmentManager().findFragmentById(R.id.frame_layout);
    paymentFragment.handleCurrentOrderItems(inventoryOrderEntities);
  }

  /**
   * Clean up done when back pressed. Can hold onto the order if need be in the future for custom
   * features
   */
  @Override
  public void showOrderDeleted() {
    finish();
  }


  /**
   * Helps the activity to switch to the right fragment once the decision was made in
   * showTransactionMethodScreen(...)
   * @param fragment - the fragment to switch the view to
   */
  public void replaceViewWith(android.app.Fragment fragment) {
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.frame_layout, fragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }

  /**
   * Handles the custom fragment switches when the customer presses back from
   * InventoryQRPaymentFragment || StaticQrPaymentFragment || DynamicQrPaymentFragment
   */
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      Fragment currentFragment = getFragmentManager().findFragmentById(R.id.frame_layout);
      if (currentFragment instanceof InventoryQRPaymentFragment) {
        if (currentOrderItems != null && currentOrderItems.size() > 0)
          showOrderCancelDialog();
        else
          finish();
      } else if (currentFragment instanceof StaticQrPaymentFragment
          || currentFragment instanceof DynamicQrPaymentFragment){
        finish();
      }
      else getFragmentManager().popBackStack();
    }
    return true;
  }

  /**
   * Delete the current order when the customer goes back to scan again
   */
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

  /**
   * Populate the check out button for 2 Fragments:
   *  - InventoryQrPaymentFragment
   *  - ConfirmOrderFragment
   * @param inventoryOrderEntities - list of orders to find the total
   * @param confirmationlayout - layout either in ConfirmOrder or InventoryQr
   * @param totalAmount -
   * @param totalNumItems
   */
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

  /**
   * Helper to calculate total number of items purchased
   * @param inventoryOrderEntities - list of orders by customer
   * @return total quantity of items
   */
  public int calculateTotalOrderItems(List<InventoryOrderItemEntity> inventoryOrderEntities) {
    return Stream.of(inventoryOrderEntities)
        .mapToInt(inventoryOrderItemEntity -> inventoryOrderItemEntity.getItemQuantity().intValue())
        .sum();
  }

  /**
   * Helper to calculate total cost of items purchased
   * @param inventoryOrderEntities - list of orders by customer
   * @return total cost of order
   */
  public int calculateTotalOrderAmount(List<InventoryOrderItemEntity> inventoryOrderEntities){
   return Stream.of(inventoryOrderEntities)
       .mapToInt(inventoryOrderItemEntity -> inventoryOrderItemEntity.getItemPrice().intValue()
           * inventoryOrderItemEntity.getItemQuantity().intValue())
       .sum();
  }

  /**
   * Pass the proper transaction object to the PinConfirmationActivity which makes the payment
   * @param qrCode - contains the required token to process payment
   * @param saleItemList - items the customer bought
   */
  public void showPinConfirmationActivity(QrCode qrCode, List<SaleItem> saleItemList) {

    Bundle bundle = new Bundle();
    bundle.putByteArray(QrType.class.getSimpleName(), QrType.ADAPTER.encode(qrCode.qr_type));
    if (qrCode.qr_type == QrType.DYNAMIC) {
      bundleDynamicTransaction(qrCode, bundle);
    } else {
      bundleStaticTransaction(qrCode, saleItemList, bundle);
    }
    Intent intent = new Intent(QrScannerActivity.this, PinConfirmationActivity.class);
    intent.putExtra(getString(R.string.bundle_id_sale_summary), bundle);
    startActivity(intent);
  }

  /**
   * Create the bundle for static and inventory static transactions
   * @param qrCode - contains the required token to process payment
   * @param saleItemList -
   *                     inventory static : items the customer bought
   *                     static : empty list
   * @param bundle - holds the token and items
   */
  private void bundleStaticTransaction(QrCode qrCode, List<SaleItem> saleItemList, Bundle bundle) {
    InitiateStaticTransactionRequest initiateStaticTransactionRequest =
        new InitiateStaticTransactionRequest.Builder()
            .sale_items(saleItemList)
            .qr_token(qrCode.qr_token)
            .build();
    bundle.putByteArray(
        InitiateStaticTransactionRequest.class.getSimpleName(),
        InitiateStaticTransactionRequest.ADAPTER.encode(initiateStaticTransactionRequest));
  }

  /**
   * Create the bundle for dynamic transactions
   * @param qrCode - contains the required token to process payment
   * @param bundle - holds the token and items
   */
  private void bundleDynamicTransaction(QrCode qrCode, Bundle bundle) {
    AcceptTransactionRequest acceptTransactionRequest =
        new AcceptTransactionRequest.Builder()
            .idempotence_token(UUID.randomUUID().toString())
            .qr_token(qrCode.qr_token)
            .build();
    bundle.putByteArray(
        AcceptTransactionRequest.class.getSimpleName(),
        AcceptTransactionRequest.ADAPTER.encode(acceptTransactionRequest));
  }

}
