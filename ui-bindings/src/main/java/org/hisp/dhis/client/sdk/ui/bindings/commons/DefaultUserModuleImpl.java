package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;
import android.support.annotation.NonNull;

import org.hisp.dhis.client.sdk.core.D2;
import org.hisp.dhis.client.sdk.core.user.UserInteractor;
import org.hisp.dhis.client.sdk.ui.AppPreferences;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenterImpl;
import org.hisp.dhis.client.sdk.utils.Logger;

final class DefaultUserModuleImpl implements DefaultUserModule {
    private final String authority;
    private final String accountType;

    DefaultUserModuleImpl(@NonNull String authority, @NonNull String accountType) {
        this.authority = authority;
        this.accountType = accountType;
    }

    @Override
    public UserInteractor providesUserInteractor(D2 d2) {
        if (d2 != null && d2.isConfigured()) {
            return d2.me();
        }

        return null;
    }

    @Override
    public DefaultNotificationHandler providesNotificationHandler(Context context) {
        return new DefaultNotificationHandlerImpl(context);
    }

    @Override
    public LauncherPresenter providesLauncherPresenter(UserInteractor currentUserInteractor) {
        return new LauncherPresenterImpl(currentUserInteractor);
    }

    @Override
    public LoginPresenter providesLoginPresenter(
            UserInteractor userInteractor, ApiExceptionHandler handler, Logger logger) {
        return new LoginPresenterImpl(userInteractor, handler, logger);
    }

    @Override
    public HomePresenter providesHomePresenter(
            UserInteractor userInteractor, SyncDateWrapper syncDateWrapper, Logger logger) {
        return new HomePresenterImpl(userInteractor, syncDateWrapper, logger);
    }

    @Override
    public ProfilePresenter providesProfilePresenter(
            UserInteractor userInteractor, SyncDateWrapper dateWrapper,
            DefaultAppAccountManager manager, DefaultNotificationHandler handler, Logger logger) {
        return new ProfilePresenterImpl(userInteractor, dateWrapper, manager, handler, logger);
    }

    @Override
    public SettingsPresenter providesSettingsPresenter(
            AppPreferences appPreferences, DefaultAppAccountManager appAccountManager) {
        return new SettingsPresenterImpl(appPreferences, appAccountManager);
    }

    @Override
    public DefaultAppAccountManager providesAppAccountManager(
            Context context, AppPreferences prefs, UserInteractor interactor, Logger logger) {
        return new DefaultAppAccountManagerImpl(context, prefs, interactor,
                authority, accountType, logger);
    }
}
