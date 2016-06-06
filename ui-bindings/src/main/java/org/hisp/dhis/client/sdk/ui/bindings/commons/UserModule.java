package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.HomePresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;

public interface UserModule {
    CurrentUserInteractor providesCurrentUserInteractor();

    LauncherPresenter providesLauncherPresenter();

    LoginPresenter providesLoginPresenter();

    HomePresenter providesHomePresenter();

    ProfilePresenter providesProfilePresenter();
}
