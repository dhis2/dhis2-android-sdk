package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.ui.bindings.views.LauncherActivity;
import org.hisp.dhis.client.sdk.ui.bindings.views.ProfileFragment;

public final class InjectionComponent {
    private final InjectionModule injectionModule;

    public InjectionComponent() {
        this.injectionModule = new InjectionModuleImpl();
    }

    public InjectionComponent(InjectionModule injectionModule) {
        this.injectionModule = injectionModule;
    }

    public void inject(ProfileFragment profileFragment) {
        if (profileFragment != null) {
            profileFragment.setLogger(injectionModule.providesLogger());
            profileFragment.setProfilePresenter(injectionModule.providesProfilePresenter());
        }
    }

    public void inject(LauncherActivity launcherActivity) {
        if (launcherActivity != null) {
            launcherActivity.setLauncherPresenter(injectionModule.providesLauncherPresenter());
        }
    }
}
