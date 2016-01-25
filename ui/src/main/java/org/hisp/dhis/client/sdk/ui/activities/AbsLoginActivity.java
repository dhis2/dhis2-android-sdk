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

package org.hisp.dhis.client.sdk.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

import static android.text.TextUtils.isEmpty;

public abstract class AbsLoginActivity extends AppCompatActivity {
    private static final String IS_LOADING = "state:isLoading";

    private RelativeLayout mLayoutContent;

    private FrameLayout mProgressBarContainer;
    private CircularProgressBar mProgressBar;

    private View mLoginViewsContainer;
    private EditText mServerUrl;
    private EditText mUsername;
    private EditText mPassword;
    private Button mLogInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLayoutContent = (RelativeLayout) findViewById(R.id.layout_content);
        mProgressBarContainer = (FrameLayout) findViewById(R.id.layout_dhis_logo);

        // Configuring progress bar (setting width of 6dp)
        float progressBarStrokeWidth = getResources()
                .getDimensionPixelSize(R.dimen.default_stroke_width);
        mProgressBar = (CircularProgressBar) findViewById(R.id.progress_bar_circular);
        mProgressBar.setIndeterminateDrawable(new CircularProgressDrawable.Builder(this)
                .color(ContextCompat.getColor(this, R.color.color_primary_default))
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .strokeWidth(progressBarStrokeWidth)
                .rotationSpeed(1f)
                .sweepSpeed(1f)
                .build());

        mLoginViewsContainer = findViewById(R.id.layout_login_views);
        mLogInButton = (Button) findViewById(R.id.button_log_in);

        mServerUrl = (EditText) findViewById(R.id.edittext_server_url);
        mUsername = (EditText) findViewById(R.id.edittext_username);
        mPassword = (EditText) findViewById(R.id.edittext_password);

        FieldTextWatcher watcher = new FieldTextWatcher();
        mServerUrl.addTextChangedListener(watcher);
        mUsername.addTextChangedListener(watcher);
        mPassword.addTextChangedListener(watcher);

        hideProgress(false);
        onTextChanged();

        if (savedInstanceState == null) {
            startIntroAnimation();
        }

        mLogInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showProgress(true);

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideProgress(true);
                    }
                }, 3000);

//                onLogInButtonClicked(mServerUrl.getText(), mUsername.getText(),
//                        mPassword.getText());
            }
        });
    }


    @Override
    protected final void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_LOADING, mProgressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected final void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.getBoolean(IS_LOADING, false)) {
            showProgress(false);
        } else {
            hideProgress(false);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void showProgress(boolean withAnimation) {
        if (withAnimation) {
            Animation loginViewsAnimation = AnimationUtils
                    .loadAnimation(this, R.anim.out_down);
//            Animation progressBarAnimation = AnimationUtils
//                    .loadAnimation(this, R.anim.out_down_half);

            mLoginViewsContainer.startAnimation(loginViewsAnimation);
//            mProgressBarContainer.startAnimation(progressBarAnimation);
        }

        mLoginViewsContainer.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress(boolean withAnimation) {
        if (withAnimation) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.in_up);
            mLoginViewsContainer.startAnimation(anim);

            mProgressBar.progressiveStop(new CircularProgressDrawable.OnEndListener() {
                @Override
                public void onEnd(CircularProgressDrawable circularProgressDrawable) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        mLoginViewsContainer.setVisibility(View.VISIBLE);
    }

    private void startIntroAnimation() {

    }

    private void onTextChanged() {
        mLogInButton.setEnabled(
                !isEmpty(mServerUrl.getText()) &&
                        !isEmpty(mUsername.getText()) &&
                        !isEmpty(mPassword.getText())
        );
    }

    private class FieldTextWatcher extends AbsTextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            AbsLoginActivity.this.onTextChanged();
        }
    }

    /**
     * Should be called in order to show progressbar.
     */
    protected final void onStartLoading() {
        showProgress(true);
    }

    /**
     * Should be called after the loading is complete.
     */
    protected final void onFinishLoading() {
        hideProgress(true);
    }

    protected EditText getServerUrl() {
        return mServerUrl;
    }

    protected EditText getUsername() {
        return mUsername;
    }

    protected EditText getPassword() {
        return mPassword;
    }

    protected Button getLoginButton() {
        return mLogInButton;
    }

    protected abstract void onLogInButtonClicked(Editable serverUrl, Editable username,
                                                 Editable password);
}