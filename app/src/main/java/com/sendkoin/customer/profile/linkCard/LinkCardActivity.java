package com.sendkoin.customer.profile.linkCard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.braintreepayments.cardform.view.SupportedCardTypesView;
import com.sendkoin.customer.R;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by warefhaque on 7/26/17.
 */

public class LinkCardActivity extends AppCompatActivity
    implements CardEditText.OnCardTypeChangedListener, OnCardFormSubmitListener{

  private static final CardType[] SUPPORTED_CARD_TYPES = {
      CardType.VISA,
      CardType.MASTERCARD,
      CardType.AMEX
  };
  private static final String TAG = LinkCardActivity.class.getSimpleName();
  private static final int REQUEST_SCAN = 100;
  public static final int MAX_POSTAL_CODE_LEN = 4;

  @BindView(R.id.form_cc) CardForm mCardForm;
  @BindView(R.id.view_supported_card_types) SupportedCardTypesView mSupportedCardTypesView;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_card_scanner);
    ButterKnife.bind(this);
    JodaTimeAndroid.init(this);
    mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);

    mCardForm.cardRequired(true)
        .expirationRequired(true)
        .cvvRequired(true)
        .postalCodeRequired(true)
        .mobileNumberRequired(true)
        .mobileNumberExplanation("Make sure SMS is enabled for this mobile number")
        .actionLabel("Add Credit Card")
        .setup(this);
    mCardForm.setOnCardFormSubmitListener(this);
    mCardForm.setOnCardTypeChangedListener(this);
    // bd postal codes as numbers only
    mCardForm.getPostalCodeEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
    mCardForm.getPostalCodeEditText().setFilters(
        new InputFilter[] {new InputFilter.LengthFilter(MAX_POSTAL_CODE_LEN)});
    if (getSupportActionBar()!=null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle("Add Card Details");
    }

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
  public void onCardTypeChanged(CardType cardType) {
    if (cardType == CardType.EMPTY) {
      mSupportedCardTypesView.setSupportedCardTypes(SUPPORTED_CARD_TYPES);
    } else {
      mSupportedCardTypesView.setSelected(cardType);
    }
  }

  @Override
  public void onCardFormSubmit() {
    if (mCardForm.isValid()) {
      Toast.makeText(this, R.string.valid, Toast.LENGTH_SHORT).show();
    } else {
      mCardForm.validate();
      Toast.makeText(this, R.string.invalid, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_add_cc, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);

    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        break;
      case R.id.action_scan:
        scanCard();
        break;
    }
    return false;
  }

  private void scanCard() {
    Intent intent = new Intent(this, CardIOActivity.class)
        .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true)
        .putExtra(CardIOActivity.EXTRA_SCAN_EXPIRY,true)
        .putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY,true)
        .putExtra(CardIOActivity.EXTRA_USE_PAYPAL_ACTIONBAR_ICON,true)
        .putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME,true)
        .putExtra(CardIOActivity.EXTRA_GUIDE_COLOR, Color.parseColor("#008489"))
        .putExtra(CardIOActivity.EXTRA_SUPPRESS_CONFIRMATION, true)
        .putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true);

//    try {
//      int unblurDigits = Integer.parseInt(mUnblurEdit.getText().toString());
//      intent.putExtra(CardIOActivity.EXTRA_UNBLUR_DIGITS, unblurDigits);
//    } catch(NumberFormatException ignored) {}

    startActivityForResult(intent, REQUEST_SCAN);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.v(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");

    String outStr = new String();
    Bitmap cardTypeImage = null;

    if ((requestCode == REQUEST_SCAN) && data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
      CreditCard result = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
      if (result != null) {

        mCardForm.getCardEditText().setText(result.cardNumber);
        mCardForm.getCardEditText().focusNextView();

        // TODO: 7/28/17 Pass the entire card information to Presenter as then you can test if formats correctly changed bby passingg in diff strings
        if (result.isExpiryValid()) {
          String expiry = Integer.toString(result.expiryMonth) + " "+ Integer.toString(result.expiryYear);

          DateTimeFormatter inputDatePattern = DateTimeFormat.forPattern("MM yyyy");
          DateTimeFormatter outputDatePattern = DateTimeFormat.forPattern("MMyy");

          DateTime dateTime = inputDatePattern.parseDateTime(expiry);
          String formattedDateString = dateTime.toString(outputDatePattern);

          Log.d(TAG, formattedDateString);
          mCardForm.getExpirationDateEditText().setText(formattedDateString);
          mCardForm.getExpirationDateEditText().focusNextView();
        }
      }
    }
  }
}
