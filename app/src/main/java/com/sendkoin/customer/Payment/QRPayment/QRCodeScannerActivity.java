package com.sendkoin.customer.Payment.QRPayment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sendkoin.api.Category;
import com.sendkoin.api.QrCode;
import com.sendkoin.api.Transaction;
import com.sendkoin.customer.KoinApplication;
import com.sendkoin.customer.Payment.TransactionDetails.TransactionDetailsActivity;
import com.sendkoin.customer.R;
import com.sendkoin.sql.entities.InventoryOrderItemEntity;

import java.util.List;

import javax.inject.Inject;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.loader.PicassoLoader;
import agency.tango.android.avatarview.views.AvatarView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.pedant.SweetAlert.SweetAlertDialog;
import mehdi.sakout.fancybuttons.FancyButton;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by warefhaque on 5/23/17.
 */

// TODO: 7/14/17 One possible refactoring is keeping the Checkout relative layout in the activity
// as both the inventory qr and the cofirm order use it
public class QRCodeScannerActivity extends Activity implements QRScannerContract.View {

  private static final String TAG = QRCodeScannerActivity.class.getSimpleName();
  @Inject
  QRScannerContract.Presenter mPresenter;

  @Inject
  Gson mGson;
  @BindView(R.id.scanner_fragment_layout)
  FrameLayout scannerFrameLayout;
  @BindView(R.id.payment_confirmation_layout)
  RelativeLayout paymentConfirmationLayout;
  @BindView(R.id.pay_button)
  FancyButton payButton;
  @BindView(R.id.merchant_name)
  TextView merchantName;
  @BindView(R.id.sale_amount)
  TextView saleAmount;
  @BindView(R.id.enter_sales)
  EditText enterSaleAmount;
  @BindView(R.id.confirmation_bar_code)
  ImageView barCode;
  @BindView(R.id.merchant_logo)
  AvatarView merchantLogo;
  @BindView(R.id.enter_sales_message)
  TextView enterSalesMessage;
  @BindView(R.id.done_button)
  FancyButton doneButton;
  @BindView(R.id.payment_complete)
  RelativeLayout paymentCompleteLayout;
  @BindView(R.id.merchant_logo_pay_complete)
  AvatarView merchantLogoPaymentComplete;
  @BindView(R.id.merchant_name_pay_complete)
  TextView merchant_name_pay_complete;
  @BindView(R.id.sale_amount_payment_complete)
  TextView saleAmountPayComplete;
  @BindView(R.id.payment_in_process_layout)
  RelativeLayout paymentInProcessLayout;
  @BindView(R.id.transaction_state_icon)
  ImageView transactionStateIcon;
  @BindView(R.id.transaction_state_text)
  TextView transactionStateText;
  @BindView(R.id.barcode_image)
  ImageView barcodeImageView;

  IImageLoader imageLoader;
  SweetAlertDialog pDialog;
  QRScannerFragment qrScannerFragement;
  private Unbinder unbinder;
  public QrCode qrCode;
  public String name;
  public String dynamicQRAmount;
  private InventoryQRPaymentFragment inventoryQRPaymentFragment;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qrscanner);
    setUpDagger();
    unbinder = ButterKnife.bind(this);
    qrScannerFragement = new QRScannerFragment();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.add(R.id.scanner_fragment_layout, qrScannerFragement);
    transaction.commit();
    imageLoader = new PicassoLoader();
//    mPresenter.removeAllOrderItems();
//    mPresenter.removeAllOrders();

  }

  private void setUpDagger() {
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

  public void showTransactionConfirmationScreen(String qrContent) {
    this.qrCode = mGson.fromJson(qrContent, QrCode.class);
    name = qrCode.merchant_name;
    merchantName.setText(qrCode.merchant_name);
    imageLoader.loadImage(merchantLogo, (String) null, qrCode.merchant_name);
    switch (qrCode.qr_type) {
      case DYNAMIC:
        dynamicQRAmount = qrCode.amount.toString();
        DynamicQRPaymentFragment dynamicQRPaymentFragment = new DynamicQRPaymentFragment();
        replaceViewWith(dynamicQRPaymentFragment);
        break;
      case STATIC:
        StaticQPaymentFragment staticQPaymentFragment = new StaticQPaymentFragment();
        replaceViewWith(staticQPaymentFragment);
        break;
      case INVENTORY_STATIC:
        inventoryQRPaymentFragment = new InventoryQRPaymentFragment();
        replaceViewWith(inventoryQRPaymentFragment);
        break;
      default:
        throw new UnsupportedOperationException("invalid qr state");
    }
  }

  public void setUpLogo(AvatarView logoView) {
    imageLoader = new PicassoLoader();
    imageLoader.loadImage(logoView, (String) null, name);
  }

  @Override
  public void showTransactionReciept(Transaction transaction) {
    Intent intent = new Intent(QRCodeScannerActivity.this, TransactionDetailsActivity.class);
    intent.putExtra("transaction_token", transaction.token);
    intent.putExtra("from_payment", true);
    startActivity(intent);
  }

  @Override
  public void showTransactionError(String errorMessage) {
    Toast.makeText(this, "Could not process transaction. Internal error.", Toast.LENGTH_SHORT).show();
  }

  @Override
  public void showInventoryItems(List<Category> groupedInventoryItems) {
    // call a function in INventoryFragment which will transfer to adapter
    if (inventoryQRPaymentFragment != null) {
      inventoryQRPaymentFragment.setAdapterList(groupedInventoryItems);
    }
  }

  // TODO: 7/13/17 (WAREF) you can make an abstract class that extends from Fragment and then you could have on call
  @Override
  public void handleOrderItems(List<InventoryOrderItemEntity> inventoryOrderEntities) {
    Fragment currentFragment = getFragmentManager().findFragmentById(R.id.scanner_fragment_layout);
    if (currentFragment instanceof InventoryQRPaymentFragment) {
      InventoryQRPaymentFragment inventoryQRPaymentFragment = (InventoryQRPaymentFragment) currentFragment;
      inventoryQRPaymentFragment.updateCheckoutView(inventoryOrderEntities);
    } else if (currentFragment instanceof DetailedInventoryFragment) {
      DetailedInventoryFragment detailedInventoryFragment = (DetailedInventoryFragment) currentFragment;
      detailedInventoryFragment.updateItemInformation(inventoryOrderEntities);
    } else if (currentFragment instanceof ConfirmOrderFragment) {
      ConfirmOrderFragment confirmOrderFragment = (ConfirmOrderFragment) currentFragment;
      confirmOrderFragment.showFinalOrder(inventoryOrderEntities);
    }
  }

  @Override
  public void showOrderDeleted(boolean orderPlaced) {
    getFragmentManager().beginTransaction()
        .replace(R.id.scanner_fragment_layout, qrScannerFragement)
        .commit();
  }


  public void replaceViewWith(android.app.Fragment fragment) {
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.scanner_fragment_layout, fragment);
    transaction.addToBackStack(null);
    transaction.commit();
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
      Fragment currentFragment = getFragmentManager().findFragmentById(R.id.scanner_fragment_layout);
      if (currentFragment instanceof InventoryQRPaymentFragment) {
        showOrderCancelDialog();
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
    pDialog.setCancelText("No").setOnCancelListener(dialogInterface
        -> pDialog.dismiss());
    pDialog.setConfirmText("Yes").setOnDismissListener(dialogInterface
        -> mPresenter.removeAllOrders(false));
    pDialog.setTitleText("Would you like to cancel your order?");
    pDialog.setCancelable(false);
    pDialog.show();
  }

  /**
   * Not using onResume to subscribe as the Fragment with the scanner does that
   */

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

}
