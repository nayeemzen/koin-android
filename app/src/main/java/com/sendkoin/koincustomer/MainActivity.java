package com.sendkoin.koincustomer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.sendkoin.koincustomer.Payment.MainPaymentFragment;
import com.sendkoin.koincustomer.Profile.MainProfileFragment;
import com.sendkoin.koincustomer.Transfer.MainTransferFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String PAY = "Pay";
    public static final String TRANSFER = "Transfer";
    public static final String PROFILE = "Profile";

    @BindView(R.id.main_view_pager) ViewPager mMainViewPager;
    @BindView(R.id.main_tab_layout) TabLayout mMainTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setViewPagerWithTabLayout();
        setUpBottomTabs();
    }

    private void setViewPagerWithTabLayout() {
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
    }

    private void setUpBottomTabs() {
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
        mMainTabLayout.getTabAt(0).setText(PAY);
        mMainTabLayout.getTabAt(1).setText(TRANSFER);
        mMainTabLayout.getTabAt(2).setText(PROFILE);
    }

}
