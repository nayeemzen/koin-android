package com.sendkoin.customer.Profile;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sendkoin.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by warefhaque on 6/26/17.
 */

public class ProfileChildViewHolder extends RecyclerView.ViewHolder {

  @BindView(R.id.profile_header_icon) ImageView profileItemIcon;
  @BindView(R.id.profile_header_text) TextView profileHeaderText;
  public ProfileChildViewHolder(View itemView) {
    super(itemView);
    ButterKnife.bind(this, itemView);
  }
}
