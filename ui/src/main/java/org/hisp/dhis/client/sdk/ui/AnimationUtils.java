package org.hisp.dhis.client.sdk.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.OvershootInterpolator;

public class AnimationUtils {
    private AnimationUtils() {
        // no instances
    }

    public static void playFabShrinkPopAnimation(FloatingActionButton floatingActionButton) {
        if (!floatingActionButton.isShown()) {
            floatingActionButton.setVisibility(View.VISIBLE);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(floatingActionButton, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(floatingActionButton, "scaleY", 0, 1);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleX, scaleY);
            animSetXY.setInterpolator(new OvershootInterpolator());
            animSetXY.setDuration(256);
            animSetXY.start();
        } else {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(floatingActionButton, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(floatingActionButton, "scaleY", 0, 1);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleY, scaleX);
            animSetXY.setInterpolator(new OvershootInterpolator());
            animSetXY.setDuration(256);
            animSetXY.start();
        }
    }
}
