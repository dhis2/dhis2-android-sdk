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

import android.animation.LayoutTransition;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

import static android.text.TextUtils.isEmpty;

public abstract class AbsLoginActivity extends AppCompatActivity {
    private static final String IS_LOADING = "state:isLoading";

    // ProgressBar.
    private CircularProgressBar progressBar;

    // Fields and corresponding container.
    private View loginViewsContainer;
    private EditText serverUrl;
    private EditText username;
    private EditText password;
    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configuring progress bar (setting width of 6dp)
        float progressBarStrokeWidth = getResources()
                .getDimensionPixelSize(R.dimen.default_stroke_width);
        progressBar = (CircularProgressBar) findViewById(R.id.progress_bar_circular);
        progressBar.setIndeterminateDrawable(new CircularProgressDrawable.Builder(this)
                .color(ContextCompat.getColor(this, R.color.color_primary_default))
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .strokeWidth(progressBarStrokeWidth)
                .rotationSpeed(1f)
                .sweepSpeed(1f)
                .build());

        /* adding transition animations to root layout */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            RelativeLayout layoutContent = (RelativeLayout) findViewById(R.id.layout_content);
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            layoutContent.setLayoutTransition(layoutTransition);
        }

        loginViewsContainer = findViewById(R.id.layout_login_views);
        logInButton = (Button) findViewById(R.id.button_log_in);

        serverUrl = (EditText) findViewById(R.id.edittext_server_url);
        username = (EditText) findViewById(R.id.edittext_username);
        password = (EditText) findViewById(R.id.edittext_password);

        FieldTextWatcher watcher = new FieldTextWatcher();
        serverUrl.addTextChangedListener(watcher);
        username.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);

        hideProgress();
        onTextChanged();

        logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onLogInButtonClicked(serverUrl.getText(), username.getText(),
                        password.getText());
            }
        });
    }

    @Override
    protected final void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_LOADING, progressBar.isShown());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected final void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.getBoolean(IS_LOADING, false)) {
            showProgress();
        } else {
            hideProgress();
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void showProgress() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Animation loginViewsAnimation = AnimationUtils.loadAnimation(this, R.anim.out_down);
            loginViewsContainer.startAnimation(loginViewsAnimation);
        }

        loginViewsContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.in_up);
            loginViewsContainer.startAnimation(anim);
        }

        loginViewsContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void onTextChanged() {
        logInButton.setEnabled(
                !isEmpty(serverUrl.getText()) &&
                        !isEmpty(username.getText()) &&
                        !isEmpty(password.getText())
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
        showProgress();
    }

    /**
     * Should be called after the loading is complete.
     */
    protected final void onFinishLoading() {
        hideProgress();
    }

    protected EditText getServerUrl() {
        return serverUrl;
    }

    protected EditText getUsername() {
        return username;
    }

    protected EditText getPassword() {
        return password;
    }

    protected Button getLoginButton() {
        return logInButton;
    }

    protected abstract void onLogInButtonClicked(Editable serverUrl, Editable username,
                                                 Editable password);
}