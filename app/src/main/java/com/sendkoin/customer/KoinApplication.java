package com.sendkoin.customer;

import android.app.Application;

import com.github.orangegangsters.lollipin.lib.managers.LockManager;
import com.sendkoin.customer.KoinServerMap.KoinServer;
import com.sendkoin.customer.data.dagger.Component.DaggerNetComponent;
import com.sendkoin.customer.data.dagger.Component.NetComponent;
import com.sendkoin.customer.data.dagger.Module.AppModule;
import com.sendkoin.customer.data.dagger.Module.NetModule;
import com.sendkoin.customer.payment.makePayment.pinConfirmation.PinConfirmationActivity;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.sendkoin.customer.KoinServerMap.getServerMap;

/**
 * Created by warefhaque on 5/20/17.
 */

public class KoinApplication extends Application {
  public static final String DEFAULT_FONT = "fonts/Nunito-Regular.ttf";
  public static final String KOIN_SERVER_URL = getServerMap().get(KoinServer.PRODUCTION);
  private NetComponent netComponent;

  @Override
  public void onCreate() {
    super.onCreate();

    this.netComponent = DaggerNetComponent.builder()
        .appModule(new AppModule(this))
        .netModule(new NetModule(KOIN_SERVER_URL))
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
    lockManager.getAppLock().setLogoId(R.drawable.ic_pin_confirmation_logo);
    lockManager.getAppLock().setTimeout(1000);
  }

  public NetComponent getNetComponent() {
    return netComponent;
  }
}
