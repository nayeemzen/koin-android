package com.sendkoin.koincustomer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sendkoin.koincustomer.Payment.MainPaymentFragment;
import com.sendkoin.koincustomer.Profile.MainProfileFragment;
import com.sendkoin.koincustomer.Transfer.MainTransferFragment;

public class MainActivity extends AppCompatActivity {

    ViewPager mMainViewPager;
    TabLayout mMainTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mMainViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mMainTabLayout = (TabLayout) findViewById(R.id.main_tab_layout);

        // remove the indicator
        mMainTabLayout.setSelectedTabIndicatorHeight(0);
        // connect Tab Layout with View Pager
        mMainTabLayout.setupWithViewPager(mMainViewPager);

        // Declare the view pager fragments
        final MainPaymentFragment mainPaymentFragment = new MainPaymentFragment();
        final MainProfileFragment mainProfileFragment = new MainProfileFragment();
        final MainTransferFragment mainTransferFragment = new MainTransferFragment();

        mMainViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position==0)
                    return mainPaymentFragment;
                else if (position==1)
                    return mainTransferFragment;
                else
                    return mainProfileFragment;

            }

            @Override
            public int getCount() {
                return 3;
            }

        });

        mMainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mMainViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mMainTabLayout.setupWithViewPager(mMainViewPager);
        mMainTabLayout.getTabAt(0).setText("Pay");
        mMainTabLayout.getTabAt(1).setText("Transfer");
        mMainTabLayout.getTabAt(2).setText("Profile");


    }

}
