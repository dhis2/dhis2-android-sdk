package org.hisp.dhis2.android.sdk.persistence;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 *  Application for initiating the DbFlow Back end
 */
public class Dhis2Application extends Application{

    public static Bus bus = new Bus(ThreadEnforcer.ANY);

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FlowManager.destroy();
    }

}
