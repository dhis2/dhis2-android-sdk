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
import android.util.AttributeSet;
import android.view.View;

import org.hisp.dhis.client.sdk.ui.R;


public class CircleView extends View {
    private int strokeWidth;
    private int circleRadius;
    private int circleGap;
    private int strokeColor;
    private int fillColor;

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
        circleRadius = ars.getDimensionPixelSize(R.styleable.CircleView_circle_radius, -1);
        strokeWidth = ars.getDimensionPixelSize(R.styleable.CircleView_stroke_width, -1);
        circleGap = ars.getDimensionPixelSize(R.styleable.CircleView_circle_gap, -1);

        fillColor = ars.getColor(R.styleable.CircleView_fill_color, 0);
        strokeColor = ars.getColor(R.styleable.CircleView_stroke_color, 0);

        setMinimumHeight(circleRadius * 2 + strokeWidth * 2);
        setMinimumWidth(circleRadius * 2 + strokeWidth * 2);
        setSaveEnabled(true);

        ars.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int ox = getWidth() / 2;
        int oy = getHeight() / 2;

        if (strokeWidth > 0 && strokeColor != 0) {
            canvas.drawCircle(ox, oy, circleRadius, getStroke());
        }

        if (circleRadius > 0 && fillColor != 0) {
            canvas.drawCircle(ox, oy, circleRadius - circleGap, getFill());
        }
    }

    private Paint getStroke() {
        // Made background stroke 2px less wide than progress drawable,
        // in order to avoid un-hidden background parts
        float adjustedStrokeWidth = strokeWidth - 2 > 0 ? strokeWidth - 2 : strokeWidth;

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(adjustedStrokeWidth);
        paint.setColor(strokeColor);
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private Paint getFill() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(fillColor);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(@ColorInt int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(@ColorInt int fillColor) {
        this.fillColor = fillColor;
    }

    public int getCircleGap() {
        return circleGap;
    }

    public void setCircleGap(int circleGap) {
        this.circleGap = circleGap;
    }
}