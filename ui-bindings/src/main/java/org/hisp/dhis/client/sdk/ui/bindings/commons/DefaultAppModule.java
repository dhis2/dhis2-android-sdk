package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.app.Application;
import android.content.Context;

import org.hisp.dhis.client.sdk.core.D2;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.utils.Logger;

public interface DefaultAppModule {
    Logger providesLogger();

    Context providesContext();

    Application providesApplication();

    D2 providesSdkInstance(Application application);

    AppPreferences providesApplicationPreferences(Context context);

    SessionPreferences providesSessionPreferences(Context context);

    ApiExceptionHandler providesApiExceptionHandler(Context context, Logger logger);

    SyncDateWrapper providesSyncDateWrapper(Context context, AppPreferences preferences, Logger logger);
}
