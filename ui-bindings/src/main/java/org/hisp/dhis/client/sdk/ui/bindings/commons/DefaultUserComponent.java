package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.core.D2;
import org.hisp.dhis.client.sdk.ui.bindings.BuildConfig;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.SettingsPresenter;

import okhttp3.logging.HttpLoggingInterceptor;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class DefaultUserComponent implements UserComponent {
    private final AppComponent appComponent;
    private final D2 sdkInstance;

    public DefaultUserComponent(AppComponent appComponent, String serverUrl) {
        this.appComponent = isNull(appComponent, "AppComponent must not be null");
        this.sdkInstance = provideSdkInstance();
    }

    @Override
    public LauncherPresenter launcherPresenter() {
        return null;
    }

    @Override
    public LoginPresenter loginPresenter() {
        return null;
    }

    @Override
    public D2 sdkInstance() {
        return sdkInstance;
    }

    @Override
    public HomePresenter homePresenter() {
        return null;
    }

    @Override
    public ProfilePresenter profilePresenter() {
        return null;
    }

    @Override
    public SettingsPresenter settingsPresenter() {
        return null;
    }

    private D2 provideSdkInstance() {
        D2.Builder builder = D2.builder(appComponent.application());

        if (BuildConfig.DEBUG) {
            // if debug build, it could be nice to track networks requests
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

            builder = builder.interceptor(logInterceptor);
        }

        return builder.build();
    }
}
