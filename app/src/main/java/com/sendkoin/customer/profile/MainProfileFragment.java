package com.sendkoin.customer.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sendkoin.customer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v7.widget.LinearLayoutManager.VERTICAL;

/**
 * Created by warefhaque on 5/20/17.
 */

public class MainProfileFragment extends android.support.v4.app.Fragment {
  @BindView(R.id.profile_recycler_view)
  RecyclerView profileRecyclerView;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_main_profile, container, false);
    ButterKnife.bind(this, view);
    setUpRecyclerView();
    return view;
  }

  private void setUpRecyclerView() {
    profileRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), VERTICAL, false));
    profileRecyclerView.setHasFixedSize(true);
    ProfileRecyclerAdapter profileRecyclerAdapter = new ProfileRecyclerAdapter(getActivity());
    profileRecyclerView.setAdapter(profileRecyclerAdapter);
  }

}
