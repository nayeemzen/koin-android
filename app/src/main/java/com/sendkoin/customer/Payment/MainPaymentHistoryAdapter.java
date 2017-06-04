package com.sendkoin.customer.Payment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sendkoin.customer.Data.Payments.Models.RealmTransaction;
import com.sendkoin.customer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 5/26/17.
 */

public class MainPaymentHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final String TAG = "MPaymentHistoryAdapter";
  public List<ListItem> groupedList;

  public MainPaymentHistoryAdapter() {
    groupedList = new ArrayList<>();
  }

  public void setGroupedList(HashMap<String, List<RealmTransaction>> payments) {
    groupedList = groupPaymentsByDate(payments);
    Log.d(TAG, groupedList.toString());
  }

  public List<ListItem> groupPaymentsByDate(HashMap<String, List<RealmTransaction>> groupedHashMap){

    List<ListItem> result = new ArrayList<>();

    for (String date : groupedHashMap.keySet()){
      DateItem dateItem = new DateItem();
      dateItem.date = date;
      result.add(dateItem);

      for (RealmTransaction realmTransaction : groupedHashMap.get(date)){
        PaymentItem paymentItem = new PaymentItem()
            .setPlaceName(realmTransaction.getMerchantName())
            .setPlaceType(realmTransaction.getMerchantType())
            .setPaidAmount(Integer.toString(realmTransaction.getAmount()));

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
   * Infates the view and assigns the variables in the viewholder to the right items
   * @param parent
   * @param viewType
   * @return
   */
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    RecyclerView.ViewHolder viewHolder = null;

    switch (viewType){

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
   * For every position in the cell sets the text and/or images in the viewholder
   * @param holder
   * @param position
   */
  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    int viewType = groupedList.get(position).getType();

    switch (viewType){
      case ListItem.TYPE_PAYMENT:
        PaymentItem paymentItem = (PaymentItem) groupedList.get(position);
        PaymentHistoryViewHolder paymentHistoryViewHolder = (PaymentHistoryViewHolder) holder;
        paymentHistoryViewHolder.paymentPlaceName.setText(paymentItem.placeName);
        paymentHistoryViewHolder.paymentPlaceType.setText(paymentItem.placeType);
        paymentHistoryViewHolder.paymentAmount.setText("$" + paymentItem.paidAmount);
        break;
      case ListItem.TYPE_DATE:
        DateItem dateItem  = (DateItem) groupedList.get(position);
        DateViewHolder dateViewHolder = (DateViewHolder) holder;
        dateViewHolder.paymentDate.setText(dateItem.date);
        break;

    }
  }

  @Override
  public int getItemCount() {
    return groupedList.size();
  }

  public class PaymentHistoryViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.payment_place_name)
    TextView paymentPlaceName;
    @BindView(R.id.payment_place_type)
    TextView paymentPlaceType;
    @BindView(R.id.payment_amount)
    TextView paymentAmount;

    public PaymentHistoryViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this,itemView);
    }
  }

  public class DateViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.payment_date)
    TextView paymentDate;

    public DateViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this,itemView);
    }
  }

  public abstract class ListItem{

    public static final int TYPE_DATE = 0;
    public static final int TYPE_PAYMENT = 1;

    public abstract int getType();
  }

  public class PaymentItem extends ListItem{

    public String placeName;
    public String placeType;
    public String paidAmount;

    public PaymentItem setPlaceName(String placeName) {
      this.placeName = placeName;
      return this;
    }

    public PaymentItem setPlaceType(String placeType) {
      this.placeType = "#"+placeType;
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

  public class DateItem extends ListItem{

    String date;

    @Override
    public int getType() {
      return TYPE_DATE;
    }
  }

}
