/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.ui.bindings.presenters;

import org.hisp.dhis.client.sdk.core.commons.ApiException;
import org.hisp.dhis.client.sdk.core.user.UserInteractor;
import org.hisp.dhis.client.sdk.models.user.User;
import org.hisp.dhis.client.sdk.ui.bindings.commons.ApiExceptionHandler;
import org.hisp.dhis.client.sdk.ui.bindings.commons.AppError;
import org.hisp.dhis.client.sdk.ui.bindings.commons.RxUtils;
import org.hisp.dhis.client.sdk.ui.bindings.views.LoginView;
import org.hisp.dhis.client.sdk.ui.bindings.views.View;
import org.hisp.dhis.client.sdk.utils.Logger;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class LoginPresenterImpl implements LoginPresenter, LoginPresenter.OnLoginFinishedListener {
    private static final String TAG = LoginPresenter.class.getSimpleName();
    private final UserInteractor userAccountInteractor;
    private final CompositeSubscription subscription;
    private final Logger logger;

    private final ApiExceptionHandler apiExceptionHandler;
    private LoginView loginView;

    public LoginPresenterImpl(UserInteractor userAccountInteractor,
            ApiExceptionHandler apiExceptionHandler, Logger logger) {
        this.userAccountInteractor = userAccountInteractor;
        this.subscription = new CompositeSubscription();
        this.apiExceptionHandler = apiExceptionHandler;
        this.logger = logger;
    }

    @Override
    public void attachView(View view) {
        isNull(view, "LoginView must not be null");
        loginView = (LoginView) view;

        if (userAccountInteractor != null && userAccountInteractor.isLoggedIn()) {
            onSuccess();
        }
    }

    @Override
    public void detachView() {
        loginView = null;
    }

    @Override
    public void validateCredentials(final String serverUrl,
            final String username, final String password) {
        loginView.showProgress();

        subscription.add(RxUtils.single(userAccountInteractor.logIn(username, password))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                               @Override
                               public void call(User user) {
                                   LoginPresenterImpl.this.onSuccess();
                               }
                           }, new Action1<Throwable>() {
                               @Override
                               public void call(Throwable throwable) {
                                   LoginPresenterImpl.this.handleError(throwable);
                               }
                           }
                ));
    }

    @Override
    public void onServerError(final AppError error) {
        loginView.hideProgress(new LoginView.OnProgressFinishedListener() {
            @Override
            public void onProgressFinished() {
                loginView.showServerError(error.getDescription());
            }
        });
    }

    @Override
    public void onUnexpectedError(final AppError error) {
        loginView.hideProgress(new LoginView.OnProgressFinishedListener() {
            @Override
            public void onProgressFinished() {
                loginView.showUnexpectedError(error.getDescription());
            }
        });
    }

    @Override
    public void onInvalidCredentialsError(final AppError error) {
        loginView.hideProgress(new LoginView.OnProgressFinishedListener() {
            @Override
            public void onProgressFinished() {
                loginView.showInvalidCredentialsError(error.getDescription());
            }
        });
    }

    @Override
    public void onSuccess() {
        loginView.navigateToHome();
    }

    public void handleError(final Throwable throwable) {
        AppError error = apiExceptionHandler.handleException(TAG, throwable);

        if (throwable instanceof ApiException && loginView != null) {
            ApiException exception = (ApiException) throwable;

            if (exception.getResponse() != null) {
                switch (exception.getResponse().code()) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED: {
                        onInvalidCredentialsError(error);
                        break;
                    }
                    case HttpURLConnection.HTTP_NOT_FOUND: {
                        onServerError(error);
                        break;
                    }
                    default: {
                        onUnexpectedError(error);
                        break;
                    }
                }
            } else if (throwable.getCause() instanceof MalformedURLException) {
                // handle the case where the url was malformed and
                onServerError(error);
            }
        } else {
            logger.e(TAG, "handleError", throwable);
        }
    }
}
