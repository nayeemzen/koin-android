package com.sendkoin.customer.profile;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sendkoin.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by warefhaque on 6/27/17.
 */

public class ProfileLinkCardViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.link_card_button) FancyButton linkCardButton;

  public ProfileLinkCardViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this,itemView);
  }
}
