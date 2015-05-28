/*
 * Copyright (c) 2014, Araz Abishov
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.utils.ui.views;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;

import org.hisp.dhis.android.sdk.R;

public class FloatingActionButton extends ImageButton {
    private final static OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator();
    private final static AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_MINI = 1;

    private static final int SHADOW_LAYER_INSET_INDEX = 1;

    private int mType;
    private int mColorNormal;
    private int mColorPressed;
    private int mShadowSize;
    private boolean mShadow;
    private boolean mHidden;

    public FloatingActionButton(Context context) {
        super(context);
        init(null);
    }

    public FloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attributeSet) {
        setClickable(true);

        mType = TYPE_NORMAL;

        mColorNormal = getColor(R.color.navy_blue);
        mColorPressed = getColor(R.color.dark_navy_blue);

        mShadow = true;
        mShadowSize = getDimension(R.dimen.floating_action_button_shadow_size);

        if (attributeSet != null) {
            TypedArray attrs = getContext().obtainStyledAttributes(attributeSet, R.styleable.FloatingActionButton);
            if (attrs != null) {
                try {
                    mColorNormal = attrs.getColor(R.styleable.FloatingActionButton_colorNormal, mColorNormal);
                    mColorPressed = attrs.getColor(R.styleable.FloatingActionButton_colorPressed, mColorPressed);
                    mShadow = attrs.getBoolean(R.styleable.FloatingActionButton_shadow, mShadow);
                    mType = attrs.getInt(R.styleable.FloatingActionButton_type, TYPE_NORMAL);
                } finally {
                    attrs.recycle();
                }
            }
        }

        updateBackground();
    }

    private Drawable createDrawable(int color) {
        OvalShape ovalShape = new OvalShape();
        ShapeDrawable shapeDrawable = new ShapeDrawable(ovalShape);
        shapeDrawable.getPaint().setColor(color);

        if (mShadow) {
            Drawable shadowDrawable;
            if (mType == TYPE_NORMAL) {
                shadowDrawable = getResources().getDrawable(R.drawable.shadow);
            } else {
                shadowDrawable = getResources().getDrawable(R.drawable.shadow_mini);
            }

            Drawable[] layerDrawableArray = new Drawable[]{
                    shadowDrawable, shapeDrawable
            };

            LayerDrawable layerDrawable = new LayerDrawable(layerDrawableArray);
            layerDrawable.setLayerInset(
                    SHADOW_LAYER_INSET_INDEX, mShadowSize, mShadowSize, mShadowSize, mShadowSize
            );

            return layerDrawable;
        } else {
            return shapeDrawable;
        }
    }

    private void updateBackground() {
        StateListDrawable stateList = new StateListDrawable();
        // Empty array goes for every other state of button
        int[] unpressedState = new int[]{};
        int[] pressedState = new int[]{android.R.attr.state_pressed};

        stateList.addState(pressedState, createDrawable(mColorPressed));
        stateList.addState(unpressedState, createDrawable(mColorNormal));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(stateList);
        } else {
            setBackgroundDrawable(stateList);
        }
    }

    public void setColorNormalResId(int colorResId) {
        setColorNormal(getColor(colorResId));
    }

    public int getColorNormal() {
        return mColorNormal;
    }

    public void setColorNormal(int color) {
        if (color != mColorNormal) {
            mColorNormal = color;
            updateBackground();
        }
    }

    public void setColorPressedResId(int colorResId) {
        setColorPressed(getColor(colorResId));
    }

    public int getColorPressed() {
        return mColorPressed;
    }

    public void setColorPressed(int color) {
        if (color != mColorPressed) {
            mColorPressed = color;
            updateBackground();
        }
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        if (type != mType) {
            mType = type;
            updateBackground();
        }
    }

    public void setShadow(boolean shadow) {
        if (shadow != mShadow) {
            mShadow = shadow;
            updateBackground();
        }
    }

    public boolean hasShadow() {
        return mShadow;
    }

    private int getColor(int id) {
        return getResources().getColor(id);
    }

    private int getDimension(int id) {
        return getResources().getDimensionPixelSize(id);
    }

    public void hide() {
        if (!mHidden) {
            setVisibility(View.GONE);
            mHidden = true;
        }
    }

    public void show() {
        if (mHidden) {
            setVisibility(View.VISIBLE);
            mHidden = false;

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0, 1);
            AnimatorSet animSetXY = new AnimatorSet();
            animSetXY.playTogether(scaleX, scaleY);
            animSetXY.setInterpolator(OVERSHOOT_INTERPOLATOR);
            animSetXY.setDuration(200);
            animSetXY.start();
        }
    }
}
