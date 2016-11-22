/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.squareup.okhttp.HttpUrl;
import com.squareup.otto.Subscribe;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.controllers.DhisController;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.network.Credentials;
import org.hisp.dhis.android.sdk.persistence.preferences.AppPreferences;
import org.hisp.dhis.android.sdk.network.APIException;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.utils.UiUtils;

/**
 *
 */
public class LoginActivity extends Activity implements OnClickListener {
    /**
     *
     */
    private final static String CLASS_TAG = "LoginActivity";
    public static final String KEY_SAVED_SERVER_URL = "KEY:SERVER_URL";

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText serverEditText;
    private Button loginButton;
    private ProgressBar progressBar;
    private View viewsContainer;

    private AppPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPrefs = new AppPreferences(getApplicationContext());
        setupUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        Dhis2Application.bus.unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Dhis2Application.bus.register(this);
    }

    /**
     * Sets up the initial UI elements
     */
    private void setupUI() {
        viewsContainer = findViewById(R.id.login_views_container);
        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        serverEditText = (EditText) findViewById(R.id.server_url);
        loginButton = (Button) findViewById(R.id.login_button);

        String server = null;//mPrefs.getServerUrl();
        String username = null;//mPrefs.getUsername();
        String password = null;

        DhisController.getInstance().init();
        if(DhisController.isUserLoggedIn()) {
            server = DhisController.getInstance().getSession().getServerUrl().toString();
            username = DhisController.getInstance().getSession().getCredentials().getUsername();
            password = DhisController.getInstance().getSession().getCredentials().getPassword();
        }

        if (server == null) {
            server = mPrefs.getServerUrl();
            if(server == null) {
                server = "https://";
            }
        }

        if (username == null) {
            username = "";
            password = "";
        }

        serverEditText.setText(server);
        usernameEditText.setText(username);
        passwordEditText.setText(password);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String serverURL = serverEditText.getText().toString();

        if(username.isEmpty()) {
            showLoginFailedDialog(getString(R.string.enter_username));
            return;
        }

        if(password.isEmpty()) {
            showLoginFailedDialog(getString(R.string.enter_password));
            return;
        }

        if(serverURL.isEmpty()) {
            showLoginFailedDialog(getString(R.string.enter_serverurl));
            return;
        }

        //remove whitespace as last character for username
        if (username.charAt(username.length() - 1) == ' ') {
            username = username.substring(0, username.length() - 1);
        }

        mPrefs.putServerUrl(serverURL);

        login(serverURL, username, password);
    }

    public void login(String serverUrl, String username, String password) {
        showProgress();
        HttpUrl serverUri = HttpUrl.parse(serverUrl);
        if(serverUri == null) {
            showLoginFailedDialog(getString(R.string.invalid_server_url));
            return;
        }
        DhisService.logInUser(
                serverUri, new Credentials(username, password)
        );
    }

    @Subscribe
    public void onLoginFinished(NetworkJob.NetworkJobResult<ResourceType> result) {
        if(result!=null && ResourceType.USERS.equals(result.getResourceType())) {
            if(result.getResponseHolder().getApiException() == null) {
                launchMainActivity();
            } else {
                onLoginFail(result.getResponseHolder().getApiException());
            }
        }
    }

    private void showProgress() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.out_up);
        viewsContainer.startAnimation(anim);
        viewsContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showLoginFailedDialog(String error) {
        Dialog.OnClickListener listener = new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLoginDialog();
            }
        };
        UiUtils.showErrorDialog(this, getString(R.string.error_message), error, listener);
    }

    public void onLoginFail(APIException e) {
        if (e.getResponse() == null) {
            String type = "error";
            //if (e.isHttpError()) type = "HttpError";
            //else if (e.isUnknownError()) type = "UnknownError";
            //else if (e.isNetworkError()) type = "NetworkError";
            //else if (e.isConversionError()) type = "ConversionError";
            showLoginFailedDialog(type + ": "
                    + e.getMessage());
        } else {
            if (e.getResponse().getStatus() == 401) {
                showLoginFailedDialog(getString(R.string.invalid_username_or_password));
            } else {
                showLoginFailedDialog(getString(R.string.unable_to_login) + " " + e.getMessage());
            }
        }
    }

    private void showLoginDialog() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.in_down);
        progressBar.setVisibility(View.GONE);
        viewsContainer.setVisibility(View.VISIBLE);
        viewsContainer.startAnimation(anim);
    }

    private void handleUser() {
        mPrefs.putServerUrl(serverEditText.getText().toString());
        mPrefs.putUserName(usernameEditText.getText().toString());
        launchMainActivity();
    }

    public void launchMainActivity() {
        startActivity(new Intent(LoginActivity.this,
                ((Dhis2Application) getApplication()).getMainActivity()));
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
        super.onBackPressed();
    }
}
