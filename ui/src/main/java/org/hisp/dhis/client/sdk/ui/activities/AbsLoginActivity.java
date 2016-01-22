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
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.CircularProgressBar;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;

import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

import static android.text.TextUtils.isEmpty;

public abstract class AbsLoginActivity extends AppCompatActivity {
    private static final String IS_LOADING = "state:isLoading";

    private View mViewsContainer;
    private CircularProgressBar mProgressBar;
    private EditText mServerUrl;
    private EditText mUsername;
    private EditText mPassword;
    private Button mLogInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configuring progress bar (setting width of 6dp)
        float progressBarWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                getResources().getDisplayMetrics());
        mProgressBar = (CircularProgressBar) findViewById(R.id.progress_bar_circular);
        mProgressBar.setStrokeBackgroundColor(ContextCompat.getColor(this, R.color.white));
        mProgressBar.setIndeterminateDrawable(new CircularProgressDrawable.Builder(this)
                .color(ContextCompat.getColor(this, R.color.color_primary_default))
                .sweepSpeed(1f)
                .strokeWidth(progressBarWidth)
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .build());

        mViewsContainer = findViewById(R.id.layout_login_views);
        mLogInButton = (Button) findViewById(R.id.button_log_in);

        mServerUrl = (EditText) findViewById(R.id.edittext_server_url);
        mUsername = (EditText) findViewById(R.id.edittext_username);
        mPassword = (EditText) findViewById(R.id.edittext_password);

        setDebugLoginCredentials();

        FieldTextWatcher watcher = new FieldTextWatcher();
        mServerUrl.addTextChangedListener(watcher);
        mUsername.addTextChangedListener(watcher);
        mPassword.addTextChangedListener(watcher);

        // hideProgressBar(false);
        onTextChanged();

        if (savedInstanceState == null) {
            startIntroAnimation();
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mLogInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CircularProgressDrawable) mProgressBar.getIndeterminateDrawable()).isRunning()) {
                    ((CircularProgressDrawable) mProgressBar.getIndeterminateDrawable()).progressiveStop();
//                    mProgressBar.progressiveStop(new CircularProgressDrawable.OnEndListener() {
//                        @Override
//                        public void onEnd(CircularProgressDrawable circularProgressDrawable) {
//                            // mProgressBar.setVisibility(View.GONE);
//                        }
//                    });
                } else {
                    ((CircularProgressDrawable) mProgressBar.getIndeterminateDrawable()).start();
                    // mProgressBar.ge
                    // mProgressBar.setVisibility(View.VISIBLE);
                }
                onLogInButtonClicked(mServerUrl.getText(), mUsername.getText(),
                        mPassword.getText());
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
            showProgressBar(false);
        } else {
            hideProgressBar(false);
        }
        super.onRestoreInstanceState(savedInstanceState);
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

    private void setDebugLoginCredentials() {
        mServerUrl.setText("https://play.dhis2.org/demo");
        mUsername.setText("android");
        mPassword.setText("Android123");
    }

    private void showProgressBar(boolean withAnimation) {
        if (withAnimation) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.out_up);
            mViewsContainer.startAnimation(anim);
        }

        mViewsContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(boolean withAnimation) {
        if (withAnimation) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.in_down);
            mViewsContainer.startAnimation(anim);
        }

        mViewsContainer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
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
        showProgressBar(true);
    }

    /**
     * Should be called after the loading is complete.
     */
    protected final void onFinishLoading() {
        hideProgressBar(true);
    }

    protected abstract void onLogInButtonClicked(Editable serverUrl, Editable username,
                                                 Editable password);
}