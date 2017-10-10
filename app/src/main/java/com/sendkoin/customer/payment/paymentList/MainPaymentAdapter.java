package com.sendkoin.customer.payment.paymentList;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sendkoin.api.Transaction;
import com.sendkoin.customer.MainActivity;
import com.sendkoin.customer.payment.paymentDetails.TransactionDetailsActivity;
import com.sendkoin.customer.R;
import com.sendkoin.customer.utility.AvatarImageView;
import com.sendkoin.sql.entities.PaymentEntity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * List of payments data fed to be shown by the MainPaymentFragment
 */

public class MainPaymentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final String TAG = MainPaymentAdapter.class.getSimpleName();
  public List<ListItem> groupedList;
  private MainActivity mainActivity;

  public MainPaymentAdapter(MainActivity mainActivity) {
    this.mainActivity = mainActivity;
    groupedList = new ArrayList<>();
  }

  public void setGroupedList(LinkedHashMap<String, List<PaymentEntity>> payments) {
    groupedList = groupPaymentsByDate(payments);
//    Log.d(TAG, groupedList.toString());
  }

  public List<ListItem> groupPaymentsByDate(LinkedHashMap<String, List<PaymentEntity>> groupedHashMap) {

    List<ListItem> result = new ArrayList<>();

    for (String date : groupedHashMap.keySet()) {
      DateItem dateItem = new DateItem().setDate(date);
      result.add(dateItem);

      for (PaymentEntity paymentEntity : groupedHashMap.get(date)) {
        PaymentItem paymentItem = new PaymentItem().setPaymentEntity(paymentEntity);
        result.add(paymentItem);
      }
    }

    return result;
  }

  @Override
  public int getItemViewType(int position) {
    return groupedList.get(position).getType();
  }

  /**
   * Infates the view and assigns the variables in the ViewHolder to the right items
   */
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    RecyclerView.ViewHolder viewHolder = null;

    switch (viewType) {

      case ListItem.TYPE_PAYMENT:
        LayoutInflater paymentLayoutInflater = LayoutInflater.from(parent.getContext());
        View paymentView = paymentLayoutInflater.inflate(R.layout.main_payment_history_item, parent, false);
        viewHolder = new PaymentHistoryViewHolder(paymentView);
        break;
      case ListItem.TYPE_DATE:
        LayoutInflater dateLayoutInflater = LayoutInflater.from(parent.getContext());
        View dateView = dateLayoutInflater.inflate(R.layout.main_payment_date_item, parent, false);
        viewHolder = new DateViewHolder(dateView);
        break;
    }
    return viewHolder;
  }

  /**
   * For every position in the cell sets the text and/or images in the ViewHolder
   */
  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    int viewType = groupedList.get(position).getType();

    switch (viewType) {
      case ListItem.TYPE_PAYMENT:
        PaymentItem paymentItem = (PaymentItem) groupedList.get(position);
        PaymentHistoryViewHolder paymentHistoryViewHolder = (PaymentHistoryViewHolder) holder;
        paymentHistoryViewHolder.setItem(paymentItem);
        break;
      case ListItem.TYPE_DATE:
        DateItem dateItem = (DateItem) groupedList.get(position);
        DateViewHolder dateViewHolder = (DateViewHolder) holder;
        dateViewHolder.paymentDate.setText(dateItem.date);
        break;
    }
  }

  @Override
  public int getItemCount() {
    return groupedList.size();
  }

  public class PaymentHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.payment_place_name)
    TextView paymentPlaceName;
    @BindView(R.id.payment_place_type)
    TextView paymentPlaceType;
    @BindView(R.id.payment_amount)
    TextView paymentAmount;
    @BindView(R.id.image_merchant_logo)
    AvatarImageView merchantLogo;

    public PaymentHistoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    public void setItem(PaymentItem paymentItem) {
      paymentPlaceName.setText(paymentItem.paymentEntity.getMerchantName());
      paymentPlaceType.setText(paymentItem.paymentEntity.getMerchantType());
      paymentAmount.setText("$" + paymentItem.paymentEntity.getAmount().intValue());
      merchantLogo.setBackgroundColor(Color.parseColor("#f2f2f2"));
      merchantLogo.setName(paymentItem.paymentEntity.getMerchantName());
      merchantLogo.setTextColor(Color.parseColor("#37B3B8"));
    }

    @Override
    public void onClick(View view) {
      ListItem clickedItem = groupedList.get(getAdapterPosition());
      if (clickedItem.getType() == ListItem.TYPE_PAYMENT){
        PaymentItem paymentItem = (PaymentItem) clickedItem;

        Intent intent = new Intent(mainActivity, TransactionDetailsActivity.class);
        intent.putExtra(view.getContext()
            .getString(R.string.trans_token_extra), paymentItem.paymentEntity.getTransactionToken());
        mainActivity.startActivity(intent);
      }
    }
  }

  public class DateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.payment_date)
    TextView paymentDate;

    public DateViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public abstract class ListItem{

    static final int TYPE_DATE = 0;
    static final int TYPE_PAYMENT = 1;

    public abstract int getType();
  }
  public class PaymentItem extends ListItem {
    PaymentEntity paymentEntity;

    public PaymentItem setPaymentEntity(PaymentEntity paymentEntity) {
      this.paymentEntity = paymentEntity;
      return this;
    }

    @Override
    public int getType() {
      return TYPE_PAYMENT;
    }
  }

  public class DateItem extends ListItem {
    String date;

    public DateItem setDate(String date) {
      this.date = date;
      return this;
    }

    @Override
    public int getType() {
      return TYPE_DATE;
    }
  }

}
