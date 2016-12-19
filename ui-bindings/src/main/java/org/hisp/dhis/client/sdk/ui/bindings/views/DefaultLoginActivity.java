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

package org.hisp.dhis.client.sdk.ui.bindings.views;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Toast;

import org.hisp.dhis.client.sdk.core.commons.ApiException;
import org.hisp.dhis.client.sdk.ui.activities.AbsLoginActivity;
import org.hisp.dhis.client.sdk.ui.bindings.App;
import org.hisp.dhis.client.sdk.ui.bindings.R;
import org.hisp.dhis.client.sdk.ui.bindings.commons.NavigationHandler;
import org.hisp.dhis.client.sdk.ui.bindings.presenters.LoginPresenter;

public class DefaultLoginActivity extends AbsLoginActivity implements LoginView {
    private LoginPresenter loginPresenter;
    private AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginPresenter = App.from(getApplication()).getUserComponent().loginPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loginPresenter.attachView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // to avoid leaks on configuration changes:
        if (alertDialog != null) {
            alertDialog.dismiss();
        }

        loginPresenter.detachView();
    }

    @Override
    protected void onLoginButtonClicked(Editable server, Editable username, Editable password) {
        try {
            // String authority = getString(R.string.authority);
            // String accountType = getString(R.string.account_type);

            // recreate object graph (since URL has changed, as well D2 instance)
            loginPresenter = App.from(getApplication())
                    .createUserComponent(server.toString()).loginPresenter();
        } catch (ApiException e) {
            loginPresenter.handleError(e);
            return;
        }

        // since we have re-instantiated LoginPresenter, we
        // also have to re-attach view to it
        loginPresenter.attachView(this);

        loginPresenter.validateCredentials(
                server.toString(), username.toString(), password.toString());
    }

    @Override
    public void showProgress() {
        onStartLoading();
    }

    @Override
    public void hideProgress(final OnProgressFinishedListener listener) {
        onFinishLoading(new OnAnimationFinishListener() {
            @Override
            public void onFinish() {
                if (listener != null) {
                    listener.onProgressFinished();
                }
            }
        });
    }

    @Override
    public void showServerError(String message) {
        if (message == null) {
            message = getString(R.string.generic_server_error);
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        getServerUrl().setError(message);
        getServerUrl().requestFocus();
    }

    @Override
    public void showInvalidCredentialsError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        getPassword().setError(message);
        getPassword().requestFocus();
        getUsername().setError(message);
        getUsername().requestFocus();
    }

    @Override
    public void showUnexpectedError(String message) {
        showErrorDialog(getString(R.string.title_error_unexpected), message);
    }

    @Override
    public void navigateToHome() {
        navigateTo(NavigationHandler.homeActivity());
    }

    private void showErrorDialog(String title, String message) {
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this)
                    .setPositiveButton(android.R.string.ok, null)
                    .create();
        }

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.show();
    }
}
