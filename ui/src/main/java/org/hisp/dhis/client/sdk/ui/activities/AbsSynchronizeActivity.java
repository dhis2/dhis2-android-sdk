package org.hisp.dhis.client.sdk.ui.activities;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.views.FontTextView;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

/*
 * Optional class to implement if you want to synchronize your data before navigating to home screen
 */
public abstract class AbsSynchronizeActivity extends Activity {
    private static final String IS_LOADING = "state:isLoading";
    private static final String PROGRESS_MESSAGE = "arg:progressMessage";

    //--------------------------------------------------------------------------------------
    // Views
    //--------------------------------------------------------------------------------------

    // ProgressBar
    private CircularProgressBar progressBar;

    private FontTextView progressMessageTextView;

    // LayoutTransition (for JellyBean+ devices only)
    private LayoutTransition layoutTransition;

    // Animations for pre-JellyBean devices
    private Animation layoutTransitionSlideIn;
    private Animation layoutTransitionSlideOut;
    private ViewGroup viewContainer;

    // Callback which will be triggered when animations are finished
    private OnPostAnimationListener onPostAnimationListener;

    // Action which should be executed after animation is finished
    private OnPostAnimationRunnable onPostAnimationAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_synchronize);

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

        viewContainer = (RelativeLayout) findViewById(R.id.layout_content);
        viewContainer.setVisibility(View.VISIBLE);

        progressMessageTextView = (FontTextView) findViewById(R.id.font_text_view_progress_message);

        /* adding transition animations to root layout */
        if (isGreaterThanOrJellyBean()) {
            layoutTransition = new LayoutTransition();
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            layoutTransition.addTransitionListener(onPostAnimationListener);

            viewContainer.setLayoutTransition(layoutTransition);
        } else {
            layoutTransitionSlideIn = AnimationUtils.loadAnimation(this, R.anim.in_up);
            layoutTransitionSlideOut = AnimationUtils.loadAnimation(this, R.anim.out_down);

            layoutTransitionSlideIn.setAnimationListener(onPostAnimationListener);
            layoutTransitionSlideOut.setAnimationListener(onPostAnimationListener);
        }

    }

    private static boolean isGreaterThanOrJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    @Override
    protected final void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null &&
                savedInstanceState.getBoolean(IS_LOADING, false)) {
            String message = savedInstanceState.getString(PROGRESS_MESSAGE) != null ? savedInstanceState.getString(PROGRESS_MESSAGE) : "";
            showProgress(message);
        } else {
            hideProgress();
        }

        super.onRestoreInstanceState(savedInstanceState);
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

    protected void showProgress(String message) {
        if (layoutTransitionSlideOut != null) {
            viewContainer.startAnimation(layoutTransitionSlideOut);
        }


        progressBar.setVisibility(View.VISIBLE);
        progressMessageTextView.setText(message);
    }

    protected void hideProgress() {
        if (layoutTransitionSlideIn != null) {
            viewContainer.startAnimation(layoutTransitionSlideIn);
        }

        progressBar.setVisibility(View.GONE);
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

    private class OnPostAnimationListener implements LayoutTransition.TransitionListener, Animation.AnimationListener {

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
        private final AbsLoginActivity.OnAnimationFinishListener listener;
        private final AbsSynchronizeActivity synchronizeActivity;
        private final boolean showProgress;

        public OnPostAnimationRunnable(AbsLoginActivity.OnAnimationFinishListener listener,
                                       AbsSynchronizeActivity synchronizeActivity, boolean showProgress) {
            this.listener = listener;
            this.synchronizeActivity = synchronizeActivity;
            this.showProgress = showProgress;
        }

        @Override
        public void run() {
            if (synchronizeActivity != null) {
                if (showProgress) {
                    Bundle extras = synchronizeActivity.getIntent().getExtras();
                    String message = extras.getString(PROGRESS_MESSAGE) != null ? extras.getString(PROGRESS_MESSAGE) : "";
                    synchronizeActivity.showProgress(message);
                } else {
                    synchronizeActivity.hideProgress();
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
