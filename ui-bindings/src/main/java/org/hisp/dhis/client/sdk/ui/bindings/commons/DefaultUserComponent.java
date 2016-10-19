package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.core.D2;
import org.hisp.dhis.client.sdk.ui.bindings.BuildConfig;
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

import okhttp3.logging.HttpLoggingInterceptor;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class DefaultUserComponent implements UserComponent {
    private final D2 sdkInstance;

    // presenters
    private final LauncherPresenter launcherPresenter;
    private final LoginPresenter loginPresenter;
    private final HomePresenter homePresenter;
    private final ProfilePresenter profilePresenter;
    private final SettingsPresenter settingsPresenter;

    public DefaultUserComponent(AppComponent appComponent) {
        this(appComponent, provideSdkInstance(appComponent).build());
    }

    public DefaultUserComponent(AppComponent appComponent, String serverUrl) {
        this(appComponent, provideSdkInstance(appComponent).baseUrl(serverUrl).build());
    }

    private DefaultUserComponent(AppComponent appComponent, D2 sdkInstance) {
        isNull(appComponent, "AppComponent must not be null");

        this.sdkInstance = sdkInstance;
        this.launcherPresenter = new LauncherPresenterImpl(sdkInstance.me());
        this.loginPresenter = new LoginPresenterImpl(sdkInstance.me(), null, appComponent.logger());

        if (sdkInstance.me() != null) {
            this.homePresenter = new HomePresenterImpl(sdkInstance.me(), null, appComponent.logger());
            this.profilePresenter = new ProfilePresenterImpl(sdkInstance.me(), null, null, null, null);
            this.settingsPresenter = new SettingsPresenterImpl(null, null);
        } else {
            this.homePresenter = null;
            this.profilePresenter = null;
            this.settingsPresenter = null;
        }
    }

    @Override
    public D2 sdkInstance() {
        return sdkInstance;
    }

    @Override
    public LauncherPresenter launcherPresenter() {
        return launcherPresenter;
    }

    @Override
    public LoginPresenter loginPresenter() {
        return loginPresenter;
    }

    @Override
    public HomePresenter homePresenter() {
        return homePresenter;
    }

    @Override
    public ProfilePresenter profilePresenter() {
        return profilePresenter;
    }

    @Override
    public SettingsPresenter settingsPresenter() {
        return settingsPresenter;
    }

    private static D2.Builder provideSdkInstance(AppComponent appComponent) {
        D2.Builder builder = D2.builder(appComponent.application());

        if (BuildConfig.DEBUG) {
            // if debug build, it could be nice to track networks requests
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            // builder = builder.interceptor(logInterceptor);
        }

        return builder;
    }
}
