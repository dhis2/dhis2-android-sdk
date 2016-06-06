package org.hisp.dhis.client.sdk.ui.bindings.commons;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.api.utils.LoggerImpl;
import org.hisp.dhis.client.sdk.android.user.CurrentUserInteractor;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LauncherPresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenterImpl;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.ProfilePresenter;
import org.hisp.dhis.client.sdk.utils.Logger;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

final class InjectionModuleImpl implements InjectionModule {

    @NonNull
    private final Context context;

    @Nullable
    private CurrentUserInteractor currentUserInteractor;

    @Nullable
    private LauncherPresenter launcherPresenter;

    @Nullable
    private LoginPresenter loginPresenter;

    @Nullable
    private ApiExceptionHandler apiExceptionHandler;

    public InjectionModuleImpl(Context context) {
        this.context = isNull(context, "Context must not be null");

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
    public LoginPresenter providesLoginPresenter() {
        if (loginPresenter == null) {
            loginPresenter = new LoginPresenterImpl(
                    providesCurrentUserInteractor(), providesApiExceptionHandler(),
                    providesLogger());
        }

        return loginPresenter;
    }

    @Override
    public ApiExceptionHandler providesApiExceptionHandler() {
        if (apiExceptionHandler == null) {
            apiExceptionHandler = new ApiExceptionHandlerImpl(
                    providesContext(), providesLogger());
        }

        return apiExceptionHandler;
    }

    @Override
    public Context providesContext() {
        return context;
    }

    @Override
    public Logger providesLogger() {
        return new LoggerImpl();
    }

    @Override
    public ProfilePresenter providesProfilePresenter() {
        return null;
    }
}
