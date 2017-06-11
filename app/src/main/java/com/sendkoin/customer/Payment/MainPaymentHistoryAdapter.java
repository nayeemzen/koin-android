package com.sendkoin.customer.Payment;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sendkoin.api.Transaction;
import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.MainActivity;
import com.sendkoin.customer.Payment.TransactionDetails.TransactionDetailsActivity;
import com.sendkoin.customer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by warefhaque on 5/26/17.
 */

public class MainPaymentHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final String TAG = "MPaymentHistoryAdapter";
  public List<ListItem> groupedList;
  private MainActivity mainActivity;

  public MainPaymentHistoryAdapter(MainActivity mainActivity) {
    this.mainActivity = mainActivity;
    groupedList = new ArrayList<>();
  }

  public void setGroupedList(LinkedHashMap<String, List<RealmTransaction>> payments) {
    groupedList = groupPaymentsByDate(payments);
//    Log.d(TAG, groupedList.toString());
  }

  public List<ListItem> groupPaymentsByDate(LinkedHashMap<String, List<RealmTransaction>> groupedHashMap) {

    List<ListItem> result = new ArrayList<>();

    for (String date : groupedHashMap.keySet()) {
      DateItem dateItem = new DateItem();
      dateItem.date = date;
      result.add(dateItem);

      for (RealmTransaction realmTransaction : groupedHashMap.get(date)) {
        PaymentItem paymentItem = new PaymentItem()
            .setPlaceName(realmTransaction.getMerchantName())
            .setPlaceType(realmTransaction.getMerchantType())
            .setTransactionToken(realmTransaction.getTransactionToken())
            .setPaidAmount(Integer.toString(realmTransaction.getAmount()));

        result.add(paymentItem);
      }
    }

    return result;
  }

  @Override
  public int getItemViewType(int position) {

    return (groupedList.get(position) == null) ? ListItem.TYPE_LOADING :
        groupedList.get(position).getType();
  }

  /**
   * Infates the view and assigns the variables in the viewholder to the right items
   *
   * @param parent
   * @param viewType
   * @return
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
      case ListItem.TYPE_LOADING:
        LayoutInflater loadingLayoutInflater = LayoutInflater.from(parent.getContext());
        View loadingView = loadingLayoutInflater.inflate(R.layout.payment_loading_item, parent, false);
        viewHolder = new LoadingViewHolder(loadingView);
    }
    return viewHolder;
  }

  /**
   * For every position in the cell sets the text and/or images in the viewholder
   *
   * @param holder
   * @param position
   */
  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    int viewType = groupedList.get(position).getType();

    switch (viewType) {
      case ListItem.TYPE_PAYMENT:
        PaymentItem paymentItem = (PaymentItem) groupedList.get(position);
        PaymentHistoryViewHolder paymentHistoryViewHolder = (PaymentHistoryViewHolder) holder;
        paymentHistoryViewHolder.paymentPlaceName.setText(paymentItem.placeName);
        paymentHistoryViewHolder.paymentPlaceType.setText(paymentItem.placeType);
        paymentHistoryViewHolder.paymentAmount.setText("$" + paymentItem.paidAmount);
        break;
      case ListItem.TYPE_DATE:
        DateItem dateItem = (DateItem) groupedList.get(position);
        DateViewHolder dateViewHolder = (DateViewHolder) holder;
        dateViewHolder.paymentDate.setText(dateItem.date);
        break;
      case ListItem.TYPE_LOADING:
        LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
        loadingViewHolder.progressBar.setIndeterminate(true);
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

    public PaymentHistoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      ListItem clickedItem = groupedList.get(getAdapterPosition());
      if (clickedItem.getType() == ListItem.TYPE_PAYMENT){
        PaymentItem paymentItem = (PaymentItem) clickedItem;

        Intent intent = new Intent(mainActivity, TransactionDetailsActivity.class);
        intent.putExtra("transaction_token", paymentItem.getTransactionToken());
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

  public class LoadingViewHolder extends RecyclerView.ViewHolder {

    // TODO: 6/4/17 change to ButterKnife later as it wasn't recognizing it now
    public ProgressBar progressBar;
    public LoadingViewHolder(View itemView) {
      super(itemView);
      progressBar = (ProgressBar) itemView.findViewById(R.id.payment_progress_bar);

    }
  }

  public abstract class ListItem {

    public static final int TYPE_DATE = 0;
    public static final int TYPE_PAYMENT = 1;
    public static final int TYPE_LOADING = 2;

    public abstract int getType();
  }

  public class LoadingItem extends ListItem {

    @Override
    public int getType() {
      return TYPE_LOADING;
    }
  }

  public class PaymentItem extends ListItem {

    public String placeName;
    public String placeType;
    public String paidAmount;
    public String transactionToken;

    public String getTransactionToken() {
      return transactionToken;
    }

    public PaymentItem setTransactionToken(String transactionToken) {
      this.transactionToken = transactionToken;
      return this;
    }

    public PaymentItem setPlaceName(String placeName) {
      this.placeName = placeName;
      return this;
    }

    public PaymentItem setPlaceType(String placeType) {
      this.placeType = "#" + placeType;
      return this;
    }

    public PaymentItem setPaidAmount(String paidAmount) {
      this.paidAmount = paidAmount;
      return this;
    }


    @Override
    public int getType() {
      return TYPE_PAYMENT;
    }
  }

  public class DateItem extends ListItem {

    String date;

    @Override
    public int getType() {
      return TYPE_DATE;
    }
  }

}
