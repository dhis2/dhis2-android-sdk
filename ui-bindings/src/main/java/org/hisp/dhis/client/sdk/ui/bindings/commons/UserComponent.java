package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.ui.bindings.views.DefaultHomeActivity;
import org.hisp.dhis.client.sdk.ui.bindings.views.DefaultLauncherActivity;
import org.hisp.dhis.client.sdk.ui.bindings.views.DefaultLoginActivity;
import org.hisp.dhis.client.sdk.ui.bindings.views.DefaultProfileFragment;

public final class UserComponent {
    private final AppModule appModule;
    private final UserModule userModule;

    public UserComponent(AppModule appModule, UserModule userModule) {
        this.appModule = appModule;
        this.userModule = userModule;
    }

    public void inject(DefaultProfileFragment defaultProfileFragment) {
        if (defaultProfileFragment != null) {
            defaultProfileFragment.setLogger(appModule.providesLogger());
            defaultProfileFragment.setProfilePresenter(userModule.providesProfilePresenter());
        }
    }

    public void inject(DefaultLauncherActivity defaultLauncherActivity) {
        if (defaultLauncherActivity != null) {
            defaultLauncherActivity.setLauncherPresenter(userModule.providesLauncherPresenter());
        }
    }

    public void inject(DefaultLoginActivity defaultLoginActivity) {
        if (defaultLoginActivity != null) {
            defaultLoginActivity.setLoginPresenter(userModule.providesLoginPresenter());
        }
    }

    public void inject(DefaultHomeActivity defaultHomeActivity) {
        if (defaultHomeActivity != null) {
            defaultHomeActivity.setHomePresenter(userModule.providesHomePresenter());
        }
    }
}
