package com.sendkoin.customer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sendkoin.customer.data.authentication.SessionManager;
import com.sendkoin.customer.login.LoginActivity;
import com.sendkoin.customer.login.LogoutEvent;
import com.sendkoin.customer.payment.MainPaymentFragment;
import com.sendkoin.customer.profile.MainProfileFragment;
import com.sendkoin.customer.rewards.MainRewardsFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {
  private static final int REQUEST_CODE_ENABLE = 11;

  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  public static final String SELECTED_FONT_COLOR = "#37B3B8";
  public static final String UNSELECTED_FONT_COLOR = "#ABB7B7";

  @BindView(R.id.main_view_pager)
  ViewPager mMainViewPager;
  @BindView(R.id.main_tab_layout)
  TabLayout mMainTabLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);

    String sessionToken = sessionManager().getSessionToken();
    if (sessionToken == null){
      startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
      getSupportActionBar().setCustomView(R.layout.custom_tab_layout);
      setupActionBar("Payments");
    }

    setViewPagerWithTabLayout();
    setUpBottomTabs();

  }

  @Override
  public void onStart() {
    super.onStart();
    EventBus.getDefault().register(this);
  }

  @Override
  public void onStop() {
    super.onStop();
    EventBus.getDefault().unregister(this);
  }

  private SessionManager sessionManager() {
    return ((KoinApplication) getApplication()).getNetComponent().sessionManager();
  }
  private void setupActionBar(String title) {


    TextView viepPagerTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.main_tab_title);
    viepPagerTitle.setText(title);

  }

  /**
   * For the Calligraphy fonts
   *
   * @param newBase
   */
  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
  }

  private void setViewPagerWithTabLayout() {
    // remove the indicator
    mMainTabLayout.setSelectedTabIndicatorHeight(0);
    // connect Tab Layout with View Pager
    mMainTabLayout.setupWithViewPager(mMainViewPager);

    mMainViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public Fragment getItem(int position) {
        if (position == 0)
          return new MainPaymentFragment();
        else if (position == 1)
          return new MainRewardsFragment();
        else
          return new MainProfileFragment();

      }

      @Override
      public int getCount() {
        return 3;
      }

    });
  }

  private void setUpBottomTabs() {

    setUpInitialTabIcons();
    mMainTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        mMainViewPager.setCurrentItem(tab.getPosition());

        if (getSupportActionBar() != null) {
          ImageView tabIcon = (ImageView) tab.getCustomView().findViewById(R.id.image_tab_payment_selector);
          TextView tabText = (TextView) tab.getCustomView().findViewById(R.id.text_tab_payment_selector);
          tabText.setTextColor(Color.parseColor(SELECTED_FONT_COLOR));
          switch (tab.getPosition()){
            case 0:
              setupActionBar(getString(R.string.payments_title));
              tabIcon.setImageResource(R.drawable.ic_tab_payment_selected);
              tabText.setText(getString(R.string.payments_title));
              break;
            case 1:
              setupActionBar(getString(R.string.rewards_title));
              tabIcon.setImageResource(R.drawable.ic_tab_rewards_selected);
              tabText.setText(getString(R.string.rewards_title));
              break;
            case 2:
              setupActionBar(getString(R.string.profile_title));
              tabIcon.setImageResource(R.drawable.ic_tab_profile_selected);
              tabText.setText(getString(R.string.profile_title));
              break;
            default:
              tabIcon.setImageResource(R.drawable.ic_tab_payment_selected);
              tabText.setText(getString(R.string.payments_title));
          }

        }
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {
        ImageView tabIcon = (ImageView) tab.getCustomView().findViewById(R.id.image_tab_payment_selector);
        TextView tabText = (TextView) tab.getCustomView().findViewById(R.id.text_tab_payment_selector);
        tabText.setTextColor(Color.parseColor(UNSELECTED_FONT_COLOR));
        switch (tab.getPosition()) {
          case 0:
            tabIcon.setImageResource(R.drawable.ic_tab_payment_unselected);
            tabText.setText(getString(R.string.payments_title));
            break;
          case 1:
            tabIcon.setImageResource(R.drawable.ic_tab_rewards_unselected);
            tabText.setText(getString(R.string.rewards_title));
            break;
          case 2:
            tabIcon.setImageResource(R.drawable.ic_tab_profile_unselected);
            tabText.setText(getString(R.string.profile_title));
            break;
        }
      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });
  }

  private void setUpInitialTabIcons() {
    for (int i = 0; i < mMainTabLayout.getTabCount(); i++) {
      TabLayout.Tab tab = mMainTabLayout.getTabAt(i);
      View view = getLayoutInflater().inflate(R.layout.tab_payment_selector, null);
      ImageView tabIcon = (ImageView) view.findViewById(R.id.image_tab_payment_selector);
      TextView tabText = (TextView) view.findViewById(R.id.text_tab_payment_selector);
      tab.setCustomView(view);
      switch (i) {
        case 0:
          tabIcon.setImageResource(R.drawable.ic_tab_payment_selected);
          tabText.setText(R.string.payments_title);
          tabText.setTextColor(Color.parseColor(SELECTED_FONT_COLOR));
          break;
        case 1:
          tabIcon.setImageResource(R.drawable.ic_tab_rewards_unselected);
          tabText.setText(R.string.rewards_title);
          tabText.setTextColor(Color.parseColor(UNSELECTED_FONT_COLOR));
          break;
        case 2:
          tabIcon.setImageResource(R.drawable.ic_tab_profile_unselected);
          tabText.setText(R.string.profile_title);
          tabText.setTextColor(Color.parseColor(UNSELECTED_FONT_COLOR));
          break;
      }
    }
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

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onLogoutRecieved(LogoutEvent logoutEvent) {
    startActivity(new Intent(MainActivity.this, LoginActivity.class));
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode){
      case REQUEST_CODE_ENABLE:
        Toast.makeText(this, "PinCode enabled", Toast.LENGTH_SHORT).show();
        break;
    }
  }
}
