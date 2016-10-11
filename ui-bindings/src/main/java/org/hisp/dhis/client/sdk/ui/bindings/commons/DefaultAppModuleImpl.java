package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import org.hisp.dhis.client.sdk.core.D2;
import org.hisp.dhis.client.sdk.core.commons.LoggerImpl;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.AppPreferencesImpl;
import org.hisp.dhis.client.sdk.utils.Logger;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

final class DefaultAppModuleImpl implements DefaultAppModule {

    @NonNull
    private final Application application;

    public DefaultAppModuleImpl(Application application) {
        this.application = isNull(application, "Context must not be null");

        D2.init(application);
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
    public Application providesApplication() {
        return application;
    }

    @Override
    public Context providesContext() {
        return application;
    }

    @Override
    public Logger providesLogger() {
        return new LoggerImpl();
    }

}
