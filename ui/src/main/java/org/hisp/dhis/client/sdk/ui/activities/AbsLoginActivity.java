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
import android.animation.LayoutTransition.TransitionListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

import static android.text.TextUtils.isEmpty;
import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;


// TODO show snackbars for errors (not dialogs)
// TODO when serverUrl, username or password are
// TODO invalid highlight corresponding fields
public abstract class AbsLoginActivity extends AppCompatActivity {
    private static final String ARG_LOGIN_ACTIVITY_LAUNCH_MODE = "arg:launchMode";
    private static final String ARG_LAUNCH_MODE_LOGIN_USER = "mode:loginUser";
    private static final String ARG_LAUNCH_MODE_CONFIRM_USER = "mode:confirmUser";

    private static final String ARG_SERVER_URL = "arg:serverUrl";
    private static final String ARG_USERNAME = "arg:username";
    private static final String IS_LOADING = "state:isLoading";

    //--------------------------------------------------------------------------------------
    // Views
    //--------------------------------------------------------------------------------------

    // ProgressBar.
    private CircularProgressBar progressBar;

    // Fields and corresponding container.
    private ViewGroup loginViewsContainer;
    private EditText serverUrl;
    private EditText username;
    private EditText password;

    private Button loginButton;
    private Button logoutButton;


    //--------------------------------------------------------------------------------------
    // Animations
    //--------------------------------------------------------------------------------------

    // LayoutTransition (for JellyBean+ devices only)
    private LayoutTransition layoutTransition;

    // Animations for pre-JellyBean devices
    private Animation layoutTransitionSlideIn;
    private Animation layoutTransitionSlideOut;

    // Action which should be executed after animation is finished
    private OnPostAnimationRunnable onPostAnimationAction;

    // Callback which will be triggered when animations are finished
    private OnPostAnimationListener onPostAnimationListener;


    //--------------------------------------------------------------------------------------
    // Factory methods
    //--------------------------------------------------------------------------------------

    /**
     * Creates intent for AbsLoginActivity to be launched in "User confirmation" mode.
     *
     * @param currentActivity Activity from which we want to fire AbsLoginActivity
     * @param target          Implementation of AbsLoginActivity
     * @param serverUrl       ServerUrl which will be set to serverUrl address and locked
     */
    public static void navigateTo(Activity currentActivity, Class<? extends Activity> target,
                                  String serverUrl, String username) {
        isNull(currentActivity, "Activity must not be null");
        isNull(target, "Target activity class must not be null");
        isNull(serverUrl, "ServerUrl must not be null");
        isNull(username, "Username must not be null");

        Intent intent = new Intent(currentActivity, target);
        intent.putExtra(ARG_LOGIN_ACTIVITY_LAUNCH_MODE, ARG_LAUNCH_MODE_CONFIRM_USER);
        intent.putExtra(ARG_SERVER_URL, serverUrl);
        intent.putExtra(ARG_USERNAME, username);

        ActivityCompat.startActivity(currentActivity, intent, null);
    }


    //--------------------------------------------------------------------------------------
    // Activity life-cycle callbacks
    //--------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Configuring progress bar (setting width of 6dp)
        float progressBarStrokeWidth = getResources()
                .getDimensionPixelSize(R.dimen.progressbar_stroke_width);
        progressBar = (CircularProgressBar) findViewById(R.id.progress_bar_circular);
        progressBar.setIndeterminateDrawable(new CircularProgressDrawable.Builder(this)
                .color(ContextCompat.getColor(this, R.color.color_primary_default))
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .strokeWidth(progressBarStrokeWidth)
                .rotationSpeed(1f)
                .sweepSpeed(1f)
                .build());

        loginViewsContainer = (CardView) findViewById(R.id.layout_login_views);
        loginButton = (Button) findViewById(R.id.button_log_in);
        logoutButton = (Button) findViewById(R.id.button_log_out);

        serverUrl = (EditText) findViewById(R.id.edittext_server_url);
        username = (EditText) findViewById(R.id.edittext_username);
        password = (EditText) findViewById(R.id.edittext_password);

        FieldTextWatcher watcher = new FieldTextWatcher();
        serverUrl.addTextChangedListener(watcher);
        username.addTextChangedListener(watcher);
        password.addTextChangedListener(watcher);
        logoutButton.setVisibility(View.GONE);

        if (getIntent().getExtras() != null) {
            String launchMode = getIntent().getExtras().getString(
                    ARG_LOGIN_ACTIVITY_LAUNCH_MODE, ARG_LAUNCH_MODE_LOGIN_USER);

            if (ARG_LAUNCH_MODE_CONFIRM_USER.equals(launchMode)) {
                String predefinedServerUrl = getIntent().getExtras().getString(ARG_SERVER_URL);
                String predefinedUsername = getIntent().getExtras().getString(ARG_USERNAME);

                serverUrl.setText(predefinedServerUrl);
                serverUrl.setEnabled(false);

                username.setText(predefinedUsername);
                username.setEnabled(false);

                loginButton.setText(R.string.confirm_user);
                logoutButton.setVisibility(View.VISIBLE);
                logoutButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        onLogoutButtonClicked();
                    }
                });
            }
        }

        onPostAnimationListener = new OnPostAnimationListener();

        /* adding transition animations to root layout */
        if (isGreaterThanOrJellyBean()) {
            layoutTransition = new LayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            layoutTransition.addTransitionListener(onPostAnimationListener);

            RelativeLayout loginLayoutContent = (RelativeLayout) findViewById(R.id.layout_content);
            loginLayoutContent.setLayoutTransition(layoutTransition);
        } else {
            layoutTransitionSlideIn = AnimationUtils.loadAnimation(this, R.anim.in_up);
            layoutTransitionSlideOut = AnimationUtils.loadAnimation(this, R.anim.out_down);

            layoutTransitionSlideIn.setAnimationListener(onPostAnimationListener);
            layoutTransitionSlideOut.setAnimationListener(onPostAnimationListener);
        }

        hideProgress();
        onTextChanged();

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onLoginButtonClicked(serverUrl.getText(), username.getText(),
                        password.getText());
            }
        });
    }

    @Override
    protected void onPause() {
        if (onPostAnimationAction != null) {
            onPostAnimationAction.run();
            onPostAnimationAction = null;
        }

        super.onPause();
    }

    @Override
    protected final void onSaveInstanceState(Bundle outState) {
        if (onPostAnimationAction != null) {
            outState.putBoolean(IS_LOADING,
                    onPostAnimationAction.isProgressBarWillBeShown());
        } else {
            outState.putBoolean(IS_LOADING, progressBar.isShown());
        }

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

    public void navigateTo(final Class<? extends Activity> activityClass) {
        isNull(activityClass, "Target activity must not be null");

        Intent intent = new Intent(this, activityClass);
        ActivityCompat.startActivity(this, intent, null);
        overridePendingTransition(
                R.anim.activity_open_enter,
                R.anim.activity_open_exit);
        finish();
    }

    private void showProgress() {
        if (layoutTransitionSlideOut != null) {
            loginViewsContainer.startAnimation(layoutTransitionSlideOut);
        }

        loginViewsContainer.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        if (layoutTransitionSlideIn != null) {
            loginViewsContainer.startAnimation(layoutTransitionSlideIn);
        }

        loginViewsContainer.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void onTextChanged() {
        loginButton.setEnabled(
                !isEmpty(serverUrl.getText()) &&
                        !isEmpty(username.getText()) &&
                        !isEmpty(password.getText()));
    }

    private boolean isAnimationInProgress() {
        boolean layoutTransitionAnimationsInProgress =
                layoutTransition != null && layoutTransition.isRunning();
        boolean layoutTransitionAnimationSlideUpInProgress = layoutTransitionSlideIn != null &&
                layoutTransitionSlideIn.hasStarted() && !layoutTransitionSlideIn.hasEnded();
        boolean layoutTransitionAnimationSlideOutInProgress = layoutTransitionSlideOut != null &&
                layoutTransitionSlideOut.hasStarted() && !layoutTransitionSlideOut.hasEnded();

        return layoutTransitionAnimationsInProgress ||
                layoutTransitionAnimationSlideUpInProgress ||
                layoutTransitionAnimationSlideOutInProgress;
    }

    private static boolean isGreaterThanOrJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * Should be called in order to show progressbar.
     */
    protected final void onStartLoading() {
        if (isAnimationInProgress()) {
            onPostAnimationAction = new OnPostAnimationRunnable(null, this, true);
        } else {
            showProgress();
        }
    }

    protected final void onFinishLoading() {
        onFinishLoading(null);
    }

    /**
     * Should be called after the loading is complete.
     */
    protected final void onFinishLoading(OnAnimationFinishListener listener) {
        if (isAnimationInProgress()) {
            onPostAnimationAction = new OnPostAnimationRunnable(listener, this, false);
            return;
        }

        hideProgress();
        if (listener != null) {
            listener.onFinish();
        }
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
        return loginButton;
    }

    protected Button getLogoutButton() {
        return logoutButton;
    }

    protected abstract void onLoginButtonClicked(
            Editable serverUrl, Editable username, Editable password);

    protected void onLogoutButtonClicked() {
        // empty implementation for subclasses to override
    }

    protected interface OnAnimationFinishListener {
        void onFinish();
    }

    private class FieldTextWatcher extends AbsTextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            AbsLoginActivity.this.onTextChanged();
        }
    }

    private class OnPostAnimationListener implements TransitionListener, AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            // stub implementation
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // stub implementation
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            onPostAnimation();
        }

        @Override
        public void startTransition(
                LayoutTransition transition, ViewGroup container, View view, int type) {
            // stub implementation
        }

        @Override
        public void endTransition(
                LayoutTransition transition, ViewGroup container, View view, int type) {
            if (LayoutTransition.CHANGE_APPEARING == type ||
                    LayoutTransition.CHANGE_DISAPPEARING == type) {
                onPostAnimation();
            }
        }

        private void onPostAnimation() {
            if (onPostAnimationAction != null) {
                onPostAnimationAction.run();
                onPostAnimationAction = null;
            }
        }
    }

    /* since this runnable is intended to be executed on UI (not main) thread, we should
    be careful and not keep any implicit references to activities */
    private static class OnPostAnimationRunnable implements Runnable {
        private final OnAnimationFinishListener listener;
        private final AbsLoginActivity loginActivity;
        private final boolean showProgress;

        public OnPostAnimationRunnable(OnAnimationFinishListener listener,
                                       AbsLoginActivity loginActivity, boolean showProgress) {
            this.listener = listener;
            this.loginActivity = loginActivity;
            this.showProgress = showProgress;
        }

        @Override
        public void run() {
            if (loginActivity != null) {
                if (showProgress) {
                    loginActivity.showProgress();
                } else {
                    loginActivity.hideProgress();
                }
            }

            if (listener != null) {
                listener.onFinish();
            }
        }

        public boolean isProgressBarWillBeShown() {
            return showProgress;
        }
    }
}