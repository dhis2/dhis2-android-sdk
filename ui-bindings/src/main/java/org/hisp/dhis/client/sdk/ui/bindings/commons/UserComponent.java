package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.views.DefaultHomeActivity;
import org.hisp.dhis.client.sdk.ui.bindings.views.DefaultLauncherActivity;
import org.hisp.dhis.client.sdk.ui.bindings.views.DefaultLoginActivity;
import org.hisp.dhis.client.sdk.ui.bindings.views.DefaultProfileFragment;
import org.hisp.dhis.client.sdk.ui.bindings.views.DefaultSettingsFragment;
import org.hisp.dhis.client.sdk.utils.Logger;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class UserComponent {
    private final Logger logger;
    private final ProfilePresenter profilePresenter;
    private final SettingsPresenter settingsPresenter;
    private final LauncherPresenter launcherPresenter;
    private final LoginPresenter loginPresenter;
    private final HomePresenter homePresenter;

    public UserComponent(DefaultAppModule defaultAppModule, DefaultUserModule defaultUserModule) {
        isNull(defaultAppModule, "DefaultAppModule must not be null");
        isNull(defaultUserModule, "DefaultUserModule must not be null");

        Context context = defaultAppModule.providesContext();

        // application module dependencies
        logger = defaultAppModule.providesLogger();

        AppPreferences appPreferences = defaultAppModule
                .providesApplicationPreferences(context);
        SyncDateWrapper syncDateWrapper = defaultAppModule
                .providesSyncDateWrapper(context, appPreferences, logger);
        ApiExceptionHandler apiExceptionHandler = defaultAppModule
                .providesApiExceptionHandler(context, logger);

        // user module related dependencies
        CurrentUserInteractor currentUserInteractor = defaultUserModule
                .providesCurrentUserInteractor();
        DefaultAppAccountManager accountManager = defaultUserModule
                .providesAppAccountManager(context, appPreferences, currentUserInteractor, logger);
        DefaultNotificationHandler defaultNotificationHandler = defaultUserModule.providesNotificationHandler(context);
        profilePresenter = defaultUserModule
                .providesProfilePresenter(currentUserInteractor, syncDateWrapper, accountManager, defaultNotificationHandler, logger);
        settingsPresenter = defaultUserModule
                .providesSettingsPresenter(appPreferences, accountManager);
        launcherPresenter = defaultUserModule
                .providesLauncherPresenter(currentUserInteractor);
        loginPresenter = defaultUserModule
                .providesLoginPresenter(currentUserInteractor, apiExceptionHandler, logger);
        homePresenter = defaultUserModule
                .providesHomePresenter(currentUserInteractor, syncDateWrapper, logger);

    }

    public void inject(DefaultProfileFragment defaultProfileFragment) {
        if (defaultProfileFragment != null) {
            defaultProfileFragment.setLogger(logger);
            defaultProfileFragment.setProfilePresenter(profilePresenter);
        }
    }

    public void inject(DefaultSettingsFragment defaultSettingsFragment) {
        if (defaultSettingsFragment != null) {
            defaultSettingsFragment.setSettingsPresenter(settingsPresenter);
        }
    }

    public void inject(DefaultLauncherActivity defaultLauncherActivity) {
        if (defaultLauncherActivity != null) {
            defaultLauncherActivity.setLauncherPresenter(launcherPresenter);
        }
    }

    public void inject(DefaultLoginActivity defaultLoginActivity) {
        if (defaultLoginActivity != null) {
            defaultLoginActivity.setLoginPresenter(loginPresenter);
        }
    }

    public void inject(DefaultHomeActivity defaultHomeActivity) {
        if (defaultHomeActivity != null) {
            defaultHomeActivity.setHomePresenter(homePresenter);
        }
    }
}
