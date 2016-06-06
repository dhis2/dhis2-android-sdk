package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.api.utils.LoggerImpl;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.AppPreferencesImpl;
import org.hisp.dhis.client.sdk.ui.SyncDateWrapper;
import org.hisp.dhis.client.sdk.utils.Logger;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

final class AppModuleImpl implements AppModule {

    @NonNull
    private final Context context;

    @Nullable
    private AppPreferences appPreferences;

    @Nullable
    private SyncDateWrapper syncDateWrapper;

    @Nullable
    private ApiExceptionHandler apiExceptionHandler;

    @Nullable
    private Logger logger;

    public AppModuleImpl(Context context) {
        this.context = isNull(context, "Context must not be null");

        D2.init(context);
    }

    @Override
    public Context providesContext() {
        return context;
    }

    @Override
    public ApiExceptionHandler providesApiExceptionHandler() {
        if (apiExceptionHandler == null) {
            apiExceptionHandler = new ApiExceptionHandlerImpl(
                    providesContext(), providesLogger());
        }

        return apiExceptionHandler;
    }

    @Override
    public AppPreferences providesApplicationPreferences() {
        if (appPreferences == null) {
            appPreferences = new AppPreferencesImpl(providesContext());
        }

        return appPreferences;
    }

    @Override
    public SyncDateWrapper providesSyncDateWrapper() {
        if (syncDateWrapper == null) {
            syncDateWrapper = new SyncDateWrapper(providesContext(),
                    providesApplicationPreferences());
        }

        return syncDateWrapper;
    }

    @Override
    public Logger providesLogger() {
        if (logger == null) {
            logger = new LoggerImpl();
        }

        return logger;
    }
}
