package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.utils.Logger;

public interface InjectionModule {
    CurrentUserInteractor providesCurrentUserInteractor();

    Logger providesLogger();

    ProfilePresenter providesProfilePresenter();

    LauncherPresenter providesLauncherPresenter();
}
