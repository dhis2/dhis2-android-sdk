package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;

import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.utils.Logger;

public interface InjectionModule {
    Context providesContext();

    Logger providesLogger();

    CurrentUserInteractor providesCurrentUserInteractor();

    ProfilePresenter providesProfilePresenter();

    LauncherPresenter providesLauncherPresenter();

    LoginPresenter providesLoginPresenter();

    ApiExceptionHandler providesApiExceptionHandler();
}
