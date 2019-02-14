package org.hisp.dhis.android.core;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class AppContextDIModule {
    private final Context context;

    public AppContextDIModule(Context context) {
        this.context = context.getApplicationContext();
    }

    @Provides
    Context appContext() {
        return context;
    }
}
