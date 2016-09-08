package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenter;
import org.hisp.dhis.client.sdk.utils.Logger;

public interface DefaultUserModule {
    CurrentUserInteractor providesCurrentUserInteractor();

    LauncherPresenter providesLauncherPresenter(CurrentUserInteractor currentUserInteractor);

    LoginPresenter providesLoginPresenter(CurrentUserInteractor currentUserInteractor,
                                          ApiExceptionHandler apiExceptionHandler, Logger logger);

    HomePresenter providesHomePresenter(CurrentUserInteractor currentUserInteractor,
                                        SyncDateWrapper syncDateWrapper, Logger logger);

    ProfilePresenter providesProfilePresenter(CurrentUserInteractor currentUserInteractor,
                                              SyncDateWrapper syncDateWrapper,
                                              DefaultAppAccountManager appAccountManager,
                                              DefaultNotificationHandler defaultNotificationHandler,
                                              Logger logger);

    SettingsPresenter providesSettingsPresenter(AppPreferences appPreferences,
                                                DefaultAppAccountManager appAccountManager);

    DefaultAppAccountManager providesAppAccountManager(Context context,
                                                       AppPreferences appPreferences,
                                                       CurrentUserInteractor currentUserInteractor,
                                                       Logger logger);

    DefaultNotificationHandler providesNotificationHandler(Context context);
}
