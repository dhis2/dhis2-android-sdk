package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.SyncDateWrapper;
import org.hisp.dhis.client.sdk.utils.Logger;

public interface DefaultAppModule {
    Context providesContext();

    Logger providesLogger();

    ApiExceptionHandler providesApiExceptionHandler(Context context, Logger logger);

    AppPreferences providesApplicationPreferences(Context context);

    SessionPreferences providesSessionPreferences(Context context);

    SyncDateWrapper providesSyncDateWrapper(Context context, AppPreferences preferences, Logger logger);

    AppAccountManager providesAppAccountManager(Context context,
                                                AppPreferences appPreferences,
                                                CurrentUserInteractor currentUserInteractor,
                                                Logger logger);
}
