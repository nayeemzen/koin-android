package com.sendkoin.customer.rewards;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sendkoin.customer.R;

/**
 * Created by warefhaque on 5/20/17.
 */

public class MainRewardsFragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_transfer, container, false);
        return view;
    }
}
