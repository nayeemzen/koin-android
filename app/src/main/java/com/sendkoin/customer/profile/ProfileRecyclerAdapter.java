package com.sendkoin.customer.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.orangegangsters.lollipin.lib.PinActivity;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;
import com.sendkoin.customer.MainActivity;
import com.sendkoin.customer.R;
import com.sendkoin.customer.payment.makePayment.pinConfirmation.PinConfirmationActivity;
import com.sendkoin.customer.profile.linkCard.LinkCardActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by warefhaque on 6/27/17.
 */

public class ProfileRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public static final int TYPE_HEADER = 0;
  public static final int TYPE_ITEM = 1;
  public static final int TYPE_LINK_CARD = 2;
  private static final int REQUEST_CODE_ENABLE = 11;
  public List<ProfileListItem> listItems;
  private Context context;

  public ProfileRecyclerAdapter(Context context) {
    this.context = context;
    setUpRecyclerList();
  }

  @Override
  public int getItemViewType(int position) {
    return listItems.get(position).getType();
  }

  private void setUpRecyclerList() {
    listItems = new ArrayList<>();
    listItems.add(new ProfileLinkCardHeader()
        .setType(TYPE_LINK_CARD));
    listItems.add(new ProfileSectionHeader()
        .setTitle("Account")
        .setType(TYPE_HEADER));
    listItems.add(new ProfileChild()
        .setTitle("Invite Friends")
        .setIconId(R.drawable.ic_noun_invite_friends)
        .setType(TYPE_ITEM));
    listItems.add(new ProfileChild()
        .setTitle("Account Settings")
        .setIconId(R.drawable.ic_noun_account_settings)
        .setType(TYPE_ITEM));
    listItems.add(new ProfileChild()
        .setTitle("Enable Pin")
        .setIconId(R.drawable.ic_noun_account_settings)
        .setType(TYPE_ITEM));
    listItems.add(new ProfileSectionHeader()
        .setTitle("Options")
        .setType(TYPE_HEADER));
    listItems.add(new ProfileChild()
        .setTitle("Notifications")
        .setType(TYPE_ITEM)
        .setIconId(R.drawable.ic_noun_notifications));
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    RecyclerView.ViewHolder viewHolder = null;
    switch (viewType) {
      case TYPE_ITEM:
        View itemView = LayoutInflater.from(context).inflate(R.layout.profile_child_layout, parent, false);
        viewHolder = new ProfileChildViewHolder(itemView);
        break;
      case TYPE_HEADER:
        View headerView = LayoutInflater.from(context).inflate(R.layout.profile_section_layout, parent, false);
        viewHolder = new ProfileSectionViewHolder(headerView);
        break;
      case TYPE_LINK_CARD:
        View linkCardView = LayoutInflater.from(context).inflate(R.layout.profile_section_layout_link_card, parent, false);
        viewHolder = new ProfileLinkCardViewHolder(linkCardView);
        break;
    }
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    switch (listItems.get(position).getType()) {
      case TYPE_ITEM:
        ProfileChild profileChild = (ProfileChild) listItems.get(position);
        ProfileChildViewHolder profileChildViewHolder = (ProfileChildViewHolder) holder;
        profileChildViewHolder.profileHeaderText.setText(profileChild.title);
        profileChildViewHolder.profileItemIcon.setImageResource(profileChild.iconId);
        break;
      case TYPE_HEADER:
        ProfileSectionViewHolder viewHolder = (ProfileSectionViewHolder) holder;
        ProfileSectionHeader profileSectionHeader = (ProfileSectionHeader) listItems.get(position);
        viewHolder.profileHeaderText.setVisibility(View.VISIBLE);
        if (profileSectionHeader.title != null)
          viewHolder.profileHeaderText.setText(profileSectionHeader.title);
        break;
      case TYPE_LINK_CARD:
        ProfileLinkCardViewHolder linkCardViewHolder = (ProfileLinkCardViewHolder) holder;
        ProfileLinkCardHeader linkCardHeader = (ProfileLinkCardHeader) listItems.get(position);
        linkCardViewHolder.linkCardButton.setOnClickListener(view -> {
          MainActivity mainActivity = (MainActivity) context;
          mainActivity.startActivity(new Intent(mainActivity, LinkCardActivity.class));
        });
        setUpLinkCardbutton(linkCardViewHolder);
        break;
    }
  }

  @Override
  public int getItemCount() {
    return listItems.size();
  }

  public abstract class ProfileListItem {
    public abstract int getType();
  }

  public class ProfileChild extends ProfileListItem {
    String title;
    int iconId;
    int type;
    // the arrow is gonna be static

    public ProfileChild setType(int type) {
      this.type = type;
      return this;
    }

    public ProfileChild setTitle(String title) {
      this.title = title;
      return this;
    }

    public ProfileChild setIconId(int iconId) {
      this.iconId = iconId;
      return this;
    }

    public ProfileChild() {
    }

    @Override
    public int getType() {
      return type;
    }
  }

  public class ProfileSectionHeader extends ProfileListItem {

    String title;
    int type;

    public ProfileSectionHeader setType(int type) {
      this.type = type;
      return this;
    }

    public ProfileSectionHeader setTitle(String title) {
      this.title = title;
      return this;
    }

    public ProfileSectionHeader() {
    }

    @Override
    public int getType() {
      return type;
    }
  }

  public class ProfileLinkCardHeader extends ProfileListItem{
    int type;

    public ProfileLinkCardHeader setType(int type) {
      this.type = type;
      return this;
    }

    @Override
    public int getType() {
      return type;
    }
  }

  private void setUpLinkCardbutton(ProfileLinkCardViewHolder profileLinkCardViewHolder) {

    FancyButton linkCardButton = profileLinkCardViewHolder.linkCardButton;

    linkCardButton.setText("Link Cards");
    linkCardButton.setBackgroundColor(Color.parseColor("#00FFFFFF"));
    linkCardButton.setFocusBackgroundColor(Color.parseColor("#008489"));
    linkCardButton.setBorderWidth(5);
    linkCardButton.setBorderColor(Color.parseColor("#37B3B8"));
    linkCardButton.setTextSize(18);
    linkCardButton.setTextColor(Color.parseColor("#37B3B8"));
    linkCardButton.setRadius(20);
    linkCardButton.setPadding(10, 20, 10, 20);
    linkCardButton.setCustomTextFont("Nunito-Regular.ttf");
  }

  public class ProfileChildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.profile_header_icon)
    ImageView profileItemIcon;
    @BindView(R.id.profile_header_text)
    TextView profileHeaderText;
    public ProfileChildViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
      itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
      if (listItems.get(getAdapterPosition()).getType() == TYPE_ITEM){
        ProfileChild profileChild = (ProfileChild) listItems.get(getAdapterPosition());
        MainActivity mainActivity = ((MainActivity) context);
        switch (profileChild.title){
          case "Invite Friends":
            mainActivity.startActivity(new Intent(mainActivity, InviteFriendsActivity.class));
            break;
          case "Enable Pin":
            Intent intent = new Intent(mainActivity, PinConfirmationActivity.class);
            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
            mainActivity.startActivityForResult(intent, REQUEST_CODE_ENABLE);
            break;
        }
      }
    }
  }

}
