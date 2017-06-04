package com.sendkoin.customer;

import android.app.Application;

import com.sendkoin.customer.Data.Dagger.Component.DaggerNetComponent;
import com.sendkoin.customer.Data.Dagger.Component.NetComponent;
import com.sendkoin.customer.Data.Dagger.Module.AppModule;
import com.sendkoin.customer.Data.Dagger.Module.NetModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by warefhaque on 5/20/17.
 */

public class KoinApplication extends Application{

    public static final String DEFAULT_FONT = "fonts/Nunito-Bold.ttf";
    public static final String KOIN_SERVERURL = "http://custom-env-1.2tfxydg93p.us-west-2.elasticbeanstalk.com/api/v1/";
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
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    public NetComponent getNetComponent() {
        return netComponent;
    }
}
