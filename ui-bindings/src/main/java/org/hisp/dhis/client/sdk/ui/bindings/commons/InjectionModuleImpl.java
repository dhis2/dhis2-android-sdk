package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;
import android.support.annotation.Nullable;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.utils.Logger;

final class InjectionModuleImpl implements InjectionModule {

    @Nullable
    private CurrentUserInteractor currentUserInteractor;

    @Nullable
    private LauncherPresenter launcherPresenter;

    public InjectionModuleImpl(Context context) {
        D2.init(context);
    }

    @Override
    public CurrentUserInteractor providesCurrentUserInteractor() {
        if (currentUserInteractor == null) {
            currentUserInteractor = D2.me();
        }

        return currentUserInteractor;
    }

    @Override
    public LauncherPresenter providesLauncherPresenter() {
        if (launcherPresenter == null) {
            launcherPresenter = new LauncherPresenterImpl(providesCurrentUserInteractor());
        }

        return launcherPresenter;
    }

    @Override
    public Logger providesLogger() {
        return null;
    }

    @Override
    public ProfilePresenter providesProfilePresenter() {
        return null;
    }
}
