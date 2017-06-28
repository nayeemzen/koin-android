package com.sendkoin.customer.Profile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.sendkoin.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by warefhaque on 6/27/17.
 */

public class InviteFriendsActivity extends AppCompatActivity {
  @BindView(R.id.invite_buton) FancyButton inviteButton;
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_invite_friends);
    ButterKnife.bind(this);
    setupInviteButton();
  }

  private void setupInviteButton() {
    inviteButton.setText("Share your code");
    inviteButton.setBackgroundColor(Color.parseColor("#37B3B8"));
    inviteButton.setFocusBackgroundColor(Color.parseColor("#008489"));
    inviteButton.setTextSize(17);
    inviteButton.setTextColor(Color.parseColor("#FFFFFF"));
    inviteButton.setRadius(15);
    inviteButton.setPadding(10, 20, 10, 20);
    inviteButton.setCustomTextFont("Nunito-Regular.ttf");
  }

  @OnClick(R.id.invite_buton)
  void inviteClicked(){

  }
}
