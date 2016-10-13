package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import org.hisp.dhis.client.sdk.core.D2;
import org.hisp.dhis.client.sdk.core.user.UserInteractor;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenter;
import org.hisp.dhis.client.sdk.utils.Logger;

public interface DefaultUserModule {
    UserInteractor providesUserInteractor(D2 d2);

    LauncherPresenter providesLauncherPresenter(UserInteractor userInteractor);

    LoginPresenter providesLoginPresenter(
            UserInteractor userInteractor, ApiExceptionHandler apiExceptionHandler, Logger logger);

    HomePresenter providesHomePresenter(
            UserInteractor userInteractor, SyncDateWrapper syncDateWrapper, Logger logger);

    ProfilePresenter providesProfilePresenter(UserInteractor userInteractor,
                                              SyncDateWrapper syncDateWrapper,
                                              DefaultAppAccountManager appAccountManager,
                                              DefaultNotificationHandler defaultNotificationHandler,
                                              Logger logger);

    SettingsPresenter providesSettingsPresenter(
            AppPreferences appPreferences, DefaultAppAccountManager appAccountManager);

    DefaultAppAccountManager providesAppAccountManager(
            Context context, AppPreferences prefs, UserInteractor userInteractor, Logger logger);

    DefaultNotificationHandler providesNotificationHandler(Context context);
}
