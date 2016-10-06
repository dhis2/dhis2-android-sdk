package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import org.hisp.dhis.client.sdk.core.user.UserInteractor;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenter;
import org.hisp.dhis.client.sdk.utils.Logger;

public interface DefaultUserModule {
    UserInteractor providesCurrentUserInteractor();

    LauncherPresenter providesLauncherPresenter(UserInteractor currentUserInteractor);

    LoginPresenter providesLoginPresenter(UserInteractor currentUserInteractor,
                                          ApiExceptionHandler apiExceptionHandler, Logger logger);

    HomePresenter providesHomePresenter(UserInteractor currentUserInteractor,
                                        SyncDateWrapper syncDateWrapper, Logger logger);

    ProfilePresenter providesProfilePresenter(UserInteractor currentUserInteractor,
                                              SyncDateWrapper syncDateWrapper,
                                              DefaultAppAccountManager appAccountManager,
                                              DefaultNotificationHandler defaultNotificationHandler,
                                              Logger logger);

    SettingsPresenter providesSettingsPresenter(AppPreferences appPreferences,
                                                DefaultAppAccountManager appAccountManager);

    DefaultAppAccountManager providesAppAccountManager(Context context,
                                                       AppPreferences appPreferences,
                                                       UserInteractor currentUserInteractor,
                                                       Logger logger);

    DefaultNotificationHandler providesNotificationHandler(Context context);
}
