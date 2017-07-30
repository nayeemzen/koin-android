package com.sendkoin.customer;

import android.app.Application;

import com.github.orangegangsters.lollipin.lib.managers.LockManager;
import com.sendkoin.customer.data.dagger.Component.DaggerNetComponent;
import com.sendkoin.customer.data.dagger.Component.NetComponent;
import com.sendkoin.customer.data.dagger.Module.AppModule;
import com.sendkoin.customer.data.dagger.Module.NetModule;
import com.sendkoin.customer.payment.makePayment.pinConfirmation.PinConfirmationActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by warefhaque on 5/20/17.
 */

public class KoinApplication extends Application {
  public static final String DEFAULT_FONT = "fonts/Nunito-Regular.ttf";
  public static final String KOIN_SERVERURL =
      "http://custom-env-1.2tfxydg93p.us-west-2.elasticbeanstalk.com/api/v1/";
  private NetComponent netComponent;

  @Override
  public void onCreate() {
    super.onCreate();

    this.netComponent = DaggerNetComponent.builder()
        .appModule(new AppModule(this))
        .netModule(new NetModule(KOIN_SERVERURL))
        .build();


    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
        .setDefaultFontPath(DEFAULT_FONT)
        .setFontAttrId(R.attr.fontPath)
        .build());

    Realm.init(getApplicationContext());
    Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
        .deleteRealmIfMigrationNeeded()
        .build());

    LockManager<PinConfirmationActivity> lockManager = LockManager.getInstance();
    lockManager.enableAppLock(this, PinConfirmationActivity.class);
    lockManager.getAppLock().setLogoId(R.drawable.ic_tab_payment_selected);
    lockManager.getAppLock().setTimeout(1000);
  }

  public NetComponent getNetComponent() {
    return netComponent;
  }
}
