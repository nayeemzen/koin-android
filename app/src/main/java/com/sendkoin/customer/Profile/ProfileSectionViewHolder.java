package com.sendkoin.customer.Profile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sendkoin.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by warefhaque on 6/26/17.
 */

public class ProfileSectionViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.profile_header_text) TextView profileHeaderText;

  public ProfileSectionViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this,itemView);
  }
}
