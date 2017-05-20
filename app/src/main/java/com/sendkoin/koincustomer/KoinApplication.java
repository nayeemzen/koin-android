package com.sendkoin.koincustomer;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by warefhaque on 5/20/17.
 */

public class KoinApplication extends Application{

    public static final String DEFAULT_FONT = "fonts/Lato-Bold.ttf";


    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(DEFAULT_FONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
