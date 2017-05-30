package com.sendkoin.customer.Data.Dagger.Module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by warefhaque on 5/20/17.
 */

@Module
public class AppModule {
    Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}
