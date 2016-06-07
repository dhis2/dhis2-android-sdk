package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;
import android.support.annotation.NonNull;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.api.utils.LoggerImpl;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.AppPreferencesImpl;
import org.hisp.dhis.client.sdk.ui.SyncDateWrapper;
import org.hisp.dhis.client.sdk.utils.Logger;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

final class DefaultAppModuleImpl implements DefaultAppModule {

    @NonNull
    private final Context context;

    @NonNull
    private final String authority;

    @NonNull
    private final String accountType;

    public DefaultAppModuleImpl(Context context, @NonNull String authority, @NonNull String accountType) {
        this.authority = authority;
        this.accountType = accountType;
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
    public AppAccountManager providesAppAccountManager(Context context, AppPreferences preferences) {
        return new AppAccountManager(context, preferences, authority, accountType);
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
    public SyncDateWrapper providesSyncDateWrapper(Context context, AppPreferences appPreferences) {
        return new SyncDateWrapper(context, appPreferences);
    }

    @Override
    public Logger providesLogger() {
        return new LoggerImpl();
    }
}
