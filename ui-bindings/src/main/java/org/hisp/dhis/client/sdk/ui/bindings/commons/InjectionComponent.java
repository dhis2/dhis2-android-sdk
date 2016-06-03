package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.ui.bindings.views.ProfileFragment;

public abstract class InjectionComponent {
    private final InjectionModule injectionModule;

    protected InjectionComponent(InjectionModule injectionModule) {
        this.injectionModule = injectionModule;
    }

    public void inject(ProfileFragment profileFragment) {
        if (profileFragment != null) {
            profileFragment.setLogger(injectionModule.providesLogger());
            profileFragment.setProfilePresenter(injectionModule.providesProfilePresenter());
        }
    }
}
