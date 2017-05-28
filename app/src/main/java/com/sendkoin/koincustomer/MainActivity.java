package com.sendkoin.koincustomer;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.sendkoin.koincustomer.Payment.MainPaymentFragment;
import com.sendkoin.koincustomer.Profile.MainProfileFragment;
import com.sendkoin.koincustomer.Transfer.MainTransferFragment;

import org.w3c.dom.Text;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.custom_tab_layout);
            setupActionBar("Payments");
        }

        setViewPagerWithTabLayout();
        setUpBottomTabs();

    }

    private void setupActionBar(String title) {

        TextView titleTextview = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.main_tab_title);
        titleTextview.setText(title);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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

                if (getSupportActionBar() != null){
                    if (tab.getPosition()==0)
                        setupActionBar("Payments");
                    else if (tab.getPosition()==1)
                        setupActionBar("Transfers");
                    else
                        setupActionBar("Profile");
                }


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_search, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
