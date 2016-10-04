package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.client.sdk.core.D2;
import org.hisp.dhis.client.sdk.core.UserInteractor;
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

import static org.hisp.dhis.client.sdk.utils.StringUtils.isEmpty;

final class DefaultUserModuleImpl implements DefaultUserModule {

    private final String authority;

    private final String accountType;

    public DefaultUserModuleImpl(@NonNull String authority, @NonNull String accountType, @Nullable String serverUrl) {
        this.authority = authority;
        this.accountType = accountType;
        if (!isEmpty(serverUrl)) {

            // configure D2
//            Configuration configuration = new Configuration(serverUrl);
//            D2.configure(configuration).toBlocking().first();
            D2.configure(serverUrl);
        }
    }

    public DefaultUserModuleImpl(@NonNull String authority, @NonNull String accountType) {
        this(authority, accountType, null);
    }

    @Override
    public UserInteractor providesCurrentUserInteractor() {
        return D2.isConfigured() ? D2.me() : null;
    }

    @Override
    public LauncherPresenter providesLauncherPresenter(UserInteractor currentUserInteractor) {
        return new LauncherPresenterImpl(currentUserInteractor);
    }

    @Override
    public LoginPresenter providesLoginPresenter(UserInteractor currentUserInteractor,
                                                 ApiExceptionHandler apiExceptionHandler,
                                                 Logger logger) {
        return new LoginPresenterImpl(currentUserInteractor, apiExceptionHandler, logger);
    }

    @Override
    public HomePresenter providesHomePresenter(UserInteractor currentUserInteractor,
                                               SyncDateWrapper syncDateWrapper, Logger logger) {
        return new HomePresenterImpl(currentUserInteractor, syncDateWrapper, logger);
    }

    @Override
    public ProfilePresenter providesProfilePresenter(UserInteractor currentUserInteractor,
                                                     SyncDateWrapper syncDateWrapper,
                                                     DefaultAppAccountManager appAccountManager,
                                                     DefaultNotificationHandler defaultNotificationHandler,
                                                     Logger logger) {
        return new ProfilePresenterImpl(currentUserInteractor, syncDateWrapper, appAccountManager,
                defaultNotificationHandler, logger);
    }

    @Override
    public SettingsPresenter providesSettingsPresenter(AppPreferences appPreferences,
                                                       DefaultAppAccountManager appAccountManager) {
        return new SettingsPresenterImpl(appPreferences, appAccountManager);
    }

    @Override
    public DefaultAppAccountManager providesAppAccountManager(Context context,
                                                              AppPreferences appPreferences,
                                                              UserInteractor currentUserInteractor,
                                                              Logger logger) {
        return new DefaultAppAccountManagerImpl(context, appPreferences, currentUserInteractor, authority, accountType, logger);
    }

    @Override
    public DefaultNotificationHandler providesNotificationHandler(Context context) {
        return new DefaultNotificationHandlerImpl(context);
    }

}
