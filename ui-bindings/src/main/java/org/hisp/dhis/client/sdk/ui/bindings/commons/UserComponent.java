package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.ui.bindings.views.HomeActivity;
import org.hisp.dhis.client.sdk.ui.bindings.views.LauncherActivity;
import org.hisp.dhis.client.sdk.ui.bindings.views.LoginActivity;
import org.hisp.dhis.client.sdk.ui.bindings.views.ProfileFragment;

public final class UserComponent {
    private final AppModule appModule;
    private final UserModule userModule;

    public UserComponent(AppModule appModule, UserModule userModule) {
        this.appModule = appModule;
        this.userModule = userModule;
    }

    public void inject(ProfileFragment profileFragment) {
        if (profileFragment != null) {
            profileFragment.setLogger(appModule.providesLogger());
            profileFragment.setProfilePresenter(userModule.providesProfilePresenter());
        }
    }

    public void inject(LauncherActivity launcherActivity) {
        if (launcherActivity != null) {
            launcherActivity.setLauncherPresenter(userModule.providesLauncherPresenter());
        }
    }

    public void inject(LoginActivity loginActivity) {
        if (loginActivity != null) {
            loginActivity.setLoginPresenter(userModule.providesLoginPresenter());
        }
    }

    public void inject(HomeActivity homeActivity) {
        if (homeActivity != null) {
            homeActivity.setHomePresenter(userModule.providesHomePresenter());
        }
    }
}
