/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import org.hisp.dhis.android.sdk.ui.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

import static android.text.TextUtils.isEmpty;

public class LoginActivity extends AppCompatActivity {
    private static final String IS_LOADING = "state:isLoading";

    // @Bind(R.id.log_in_views_container)
    View mViewsContainer;

    //@Bind(R.id.progress_bar_circular_white)
    CircularProgressBar mProgressBar;

    //@Bind(R.id.server_url)
    EditText mServerUrl;

    //@Bind(R.id.username)
    EditText mUsername;

    //@Bind(R.id.password)
    EditText mPassword;

    // @Bind(R.id.log_in_button)
    Button mLogInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        hideProgress(false);
        checkEditTextFields();

        mServerUrl.setText("https://apps.dhis2.org/demo");
        mUsername.setText("admin");
        mPassword.setText("district");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_LOADING, mProgressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.getBoolean(IS_LOADING, false)) {
            showProgress(false);
        } else {
            hideProgress(false);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    //@OnTextChanged(value = {R.id.server_url, R.id.username, R.id.password})
    public void checkEditTextFields() {
        mLogInButton.setEnabled(
                !isEmpty(mServerUrl.getText()) &&
                        !isEmpty(mUsername.getText()) &&
                        !isEmpty(mPassword.getText())
        );
    }

    //@OnClick(R.id.log_in_button)
    @SuppressWarnings("unused")
    public void logIn() {
        showProgress(true);

        String serverUrl = mServerUrl.getText().toString();
        String username = mUsername.getText().toString();
        String password = mPassword.getText().toString();

        /* HttpUrl serverUri = HttpUrl.parse(serverUrl);
        getDhisService().logInUser(
                serverUri, new Credentials(username, password)
        ); */
    }

    /* @Subscribe
    @SuppressWarnings("unused")
    public void onResultReceived(NetworkJob.NetworkJobResult<UserAccount> jobResult) {
        if (ResourceType.USERS.equals(jobResult.getResourceType())) {
            ResponseHolder<UserAccount> responseHolder = jobResult.getResponseHolder();

            if (responseHolder.getApiException() == null) {
                startActivity(new Intent(this, MenuActivity.class));
                finish();
            } else {
                hideProgress(true);
                showApiExceptionMessage(responseHolder.getApiException());
            }
        }
    } */

    private void showProgress(boolean withAnimation) {
        if (withAnimation) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.out_up);
            mViewsContainer.startAnimation(anim);
        }
        mViewsContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress(boolean withAnimation) {
        if (withAnimation) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.in_down);
            mViewsContainer.startAnimation(anim);
        }
        mViewsContainer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }
}