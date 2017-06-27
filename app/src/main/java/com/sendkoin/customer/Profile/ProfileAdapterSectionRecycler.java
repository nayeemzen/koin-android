//package com.sendkoin.customer.Profile;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;
//import com.sendkoin.customer.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import mehdi.sakout.fancybuttons.FancyButton;
//
///**
// * Created by warefhaque on 6/26/17.
// */
//
//public class ProfileAdapterSectionRecycler extends SectionRecyclerViewAdapter<ProfileSectionHeader, ProfileChild, ProfileSectionViewHolder, ProfileChildViewHolder> {
//
//  Context context;
//  private List<ProfileSectionHeader> sectionItemList = new ArrayList<>();
//
//
//  public ProfileAdapterSectionRecycler(Context context, List<ProfileSectionHeader> sectionItemList) {
//    super(context, sectionItemList);
//    this.context = context;
//    this.sectionItemList = sectionItemList;
//  }
//
//  @Override
//  public ProfileSectionViewHolder onCreateSectionViewHolder(ViewGroup viewGroup, int i) {
//    View view = LayoutInflater.from(context).inflate(R.layout.profile_section_layout, viewGroup, false);
//    return new ProfileSectionViewHolder(view);
//  }
//
//  @Override
//  public ProfileChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup, int i) {
////    View view
//    View view = LayoutInflater.from(context).inflate(R.layout.profile_child_layout, viewGroup, false);
//    return new ProfileChildViewHolder(view);
//  }
//
//  @Override
//  public void onBindSectionViewHolder(ProfileSectionViewHolder profileSectionViewHolder, int i, ProfileSectionHeader profileSectionHeader) {
////    switch (i) {
////      case 0:
////        setUiState(UIState.LINK_CARD, profileSectionViewHolder, null);
////        break;
////      default:
////        setUiState(UIState.REGULAR_HEADER, profileSectionViewHolder,profileSectionHeader.title);
////        break;
////    }
//    profileSectionViewHolder.linkCardLayout.setVisibility(View.GONE);
//    profileSectionViewHolder.profileHeaderText.setVisibility(View.VISIBLE);
//    if (profileSectionHeader.title != null)
//      profileSectionViewHolder.profileHeaderText.setText(profileSectionHeader.title);
//  }
//
//  @Override
//  public void onBindChildViewHolder(ProfileChildViewHolder profileChildViewHolder, int i, int i1, ProfileChild profileChild) {
//    profileChildViewHolder.profileItemIcon.setImageResource(profileChild.iconId);
//    profileChildViewHolder.profileHeaderText.setText(profileChild.title);
//  }
//}
