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

import org.hisp.dhis.client.sdk.ui.R;

import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

public class CircularProgressBar extends fr.castorflex.android.circularprogressbar.CircularProgressBar {
    private int mStrokeBackgroundColor;

    public CircularProgressBar(Context context) {
        super(context);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ars = context.obtainStyledAttributes(attrs,
                R.styleable.CircularProgressBar, defStyle, 0);
        mStrokeBackgroundColor = ars.getColor(R.styleable.CircularProgressBar_cpb_stroke_background_color,
                ContextCompat.getColor(getContext(), R.color.cpb_default_color));
        ars.recycle();
    }


    @Override
    public void onDraw(Canvas canvas) {
        int w = this.getWidth();
        int h = this.getHeight();

        float ox = w / 2.0f;
        float oy = h / 2.0f;

        float strokeWidth = getContext().getResources()
                .getDimension(R.dimen.cpb_default_stroke_width);
        if (getIndeterminateDrawable() != null &&
                getIndeterminateDrawable() instanceof CircularProgressDrawable) {
            CircularProgressDrawable progressDrawable = (CircularProgressDrawable)
                    getIndeterminateDrawable();
            strokeWidth = progressDrawable.getCurrentPaint().getStrokeWidth();
        }
        System.out.println("StrokeWidth: " + strokeWidth);
        System.out.println("CircleRadius: " + getCircleRadius(strokeWidth));

        canvas.drawCircle(ox, oy, getCircleRadius(strokeWidth), getStroke(strokeWidth,
                mStrokeBackgroundColor));

        super.onDraw(canvas);
    }

    private Paint getStroke(float strokeWidth, int strokeColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private float getCircleRadius(float strokeWidth) {
        float w = this.getWidth();
        float h = this.getHeight();

        // Removing one pixel to make sure that background circle
        float strokeWidthHalf = (strokeWidth - 1) / 2.0f;

        float radius = (w > h ? w / 2.0f : h / 2.0f) - strokeWidthHalf;
        return radius <= 0 ? 1 : radius;
    }

    public void setStrokeBackgroundColor(@ColorInt int strokeBackgroundColor) {
        mStrokeBackgroundColor = strokeBackgroundColor;
    }

    public int getStrokeBackgroundColor() {
        return mStrokeBackgroundColor;
    }
}
