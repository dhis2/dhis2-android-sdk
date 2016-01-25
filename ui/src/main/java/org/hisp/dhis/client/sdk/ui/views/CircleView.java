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

package org.hisp.dhis.client.sdk.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import org.hisp.dhis.client.sdk.ui.R;

public class CircleView extends View {
    private int mStrokeBackgroundColor;
    private float mStrokeWidth;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.AppTheme_Base);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ars = context.obtainStyledAttributes(attrs,
                R.styleable.CircleView, defStyleAttr, 0);
        mStrokeBackgroundColor = ars.getColor(R.styleable.CircleView_stroke_color,
                ContextCompat.getColor(getContext(), R.color.white));
        mStrokeWidth = ars.getDimensionPixelSize(R.styleable.CircleView_stroke_width,
                getResources().getDimensionPixelSize(R.dimen.default_stroke_width));

        ars.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        int w = this.getWidth();
        int h = this.getHeight();

        float ox = w / 2.0f;
        float oy = h / 2.0f;

        canvas.drawCircle(ox, oy, getCircleRadius(), getStroke());
        super.onDraw(canvas);
    }

    private Paint getStroke() {
        // Made background stroke 2px less wide than progress drawable,
        // in order to avoid un-hidden background parts
        float adjustedStrokeWidth = mStrokeWidth - 2 > 0 ? mStrokeWidth - 2 : mStrokeWidth;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(adjustedStrokeWidth);
        paint.setColor(mStrokeBackgroundColor);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private float getCircleRadius() {
        float w = this.getWidth();
        float h = this.getHeight();

        float radius = (w > h ? w / 2.0f : h / 2.0f) - (mStrokeWidth / 2.0f);
        return radius <= 0 ? 1 : radius;
    }

    public int getStrokeBackgroundColor() {
        return mStrokeBackgroundColor;
    }

    public void setStrokeBackgroundColor(@ColorInt int mStrokeBackgroundColor) {
        this.mStrokeBackgroundColor = mStrokeBackgroundColor;
    }

    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
    }
}
