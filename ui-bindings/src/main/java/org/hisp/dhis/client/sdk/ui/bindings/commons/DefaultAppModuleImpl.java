package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;
import android.support.annotation.NonNull;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.api.utils.LoggerImpl;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.AppPreferencesImpl;
import org.hisp.dhis.client.sdk.utils.Logger;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

final class DefaultAppModuleImpl implements DefaultAppModule {

    @NonNull
    private final Context context;

    public DefaultAppModuleImpl(Context context) {
        this.context = isNull(context, "Context must not be null");

        D2.init(context);
    }

    @Override
    public Context providesContext() {
        return context;
    }

    @Override
    public ApiExceptionHandler providesApiExceptionHandler(Context context, Logger logger) {
        return new ApiExceptionHandlerImpl(context, logger);
    }

    @Override
    public AppPreferences providesApplicationPreferences(Context context) {
        return new AppPreferencesImpl(context);
    }

    @Override
    public SessionPreferences providesSessionPreferences(Context context) {
        return new SessionPreferencesImpl(context);
    }

    @Override
    public SyncDateWrapper providesSyncDateWrapper(Context context, AppPreferences preferences, Logger logger) {
        return new SyncDateWrapper(context, preferences);
    }

    @Override
    public Logger providesLogger() {
        return new LoggerImpl();
    }

}
