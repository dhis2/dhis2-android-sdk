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
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RaisedButton extends CardView {
    private final FontTextView buttonTextView;
    private final LinearLayout linearLayout;

    public RaisedButton(Context context) {
        super(context);

        buttonTextView = new FontTextView(context);
        linearLayout = new LinearLayout(context);

        init();
    }

    public RaisedButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        buttonTextView = new FontTextView(context, attrs);
        linearLayout = new LinearLayout(context, attrs);

        init();
    }

    public RaisedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        buttonTextView = new FontTextView(context, attrs, defStyleAttr);
        linearLayout = new LinearLayout(context, attrs, defStyleAttr);

        init();
    }


    private void init() {
        setRadius(calculatePixels(2));
        setCardElevation(calculatePixels(2));
        setPreventCornerOverlap(true);
        setUseCompatPadding(true);
        setClickable(true);

        this.addView(linearLayout, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
        linearLayout.addView(buttonTextView, new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        int linearLayoutPadding = calculatePixels(8);
        linearLayout.setPadding(linearLayoutPadding, linearLayoutPadding,
                linearLayoutPadding, linearLayoutPadding);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            linearLayout.setBackgroundDrawable(getSelectableItemBackground());
        } else {
            linearLayout.setBackground(getSelectableItemBackground());
        }
    }

    private Drawable getSelectableItemBackground() {
        int[] attrs = new int[]{
                android.R.attr.selectableItemBackground
        };

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = typedArray.getDrawable(0);

        // Free resources used by TypedArray
        typedArray.recycle();

        return drawableFromTheme;
    }

    private int calculatePixels(int dps) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps,
                getResources().getDisplayMetrics());
    }

    public TextView getTextView() {
        return buttonTextView;
    }
}
