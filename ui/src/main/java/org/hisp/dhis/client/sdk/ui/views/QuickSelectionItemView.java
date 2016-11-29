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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.Picker;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class QuickSelectionItemView extends LinearLayout {

    private final TextView nameView;
    private final ImageView checkbox;
    private GradientDrawable background;
    private boolean selected;
    private int selectedBackgroundColor;
    private int unSelectedBackgroundColor;
    private int selectedTextColor;
    private int unSelectedTextColor;
    private Picker item;

    public QuickSelectionItemView(Context context) {
        this(context, null);
    }

    public QuickSelectionItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickSelectionItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public QuickSelectionItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        nameView = new FontTextView(context);
        checkbox = new ImageView(context);
        init();
    }

    private void init() {

        ViewGroup.LayoutParams itemLayoutParams = new ViewGroup.LayoutParams(
                WRAP_CONTENT, getResources().getDimensionPixelSize(R.dimen.quick_selection_item_height));
        setLayoutParams(itemLayoutParams);

        setBackgroundResource(R.drawable.quick_selection_background);
        setGravity(CENTER_VERTICAL);
        setOrientation(HORIZONTAL);

        unSelectedBackgroundColor = ContextCompat.getColor(getContext(), R.color.color_gray_300);
        selectedBackgroundColor = ContextCompat.getColor(getContext(), R.color.color_accent_default);
        unSelectedTextColor = ContextCompat.getColor(getContext(), R.color.black_87_percent);
        selectedTextColor = ContextCompat.getColor(getContext(), android.R.color.white);
        background = (GradientDrawable) ((LayerDrawable) getBackground()).findDrawableByLayerId(R.id.gradient_layer);

        initNameView();
        initCheckbox();
    }

    private void initNameView() {
        nameView.setMaxLines(1);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        nameView.setLayoutParams(params);
        nameView.setPadding(getResources().getDimensionPixelSize(R.dimen.quick_selection_left_padding), 0, 0, 0);
        nameView.setTextColor(ContextCompat.getColor(getContext(), R.color.black_87_percent));
        nameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        addView(nameView);
    }

    private void initCheckbox() {
        checkbox.setImageResource(R.drawable.ic_quick_selection_unselected);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.quick_selection_checkbox_margin);
        lp.setMargins(margin, margin, margin, margin);
        checkbox.setLayoutParams(lp);
        addView(checkbox);
    }

    public void setItem(Picker item) {
        this.item = item;
        nameView.setText(item.getName());
    }

    public void setSelected(boolean newSelectionState) {
        selected = newSelectionState;

        if (selected) {
            checkbox.setImageResource(R.drawable.ic_quick_selection_selected);
            background.setColor(selectedBackgroundColor);
            nameView.setTextColor(selectedTextColor);
        } else {
            checkbox.setImageResource(R.drawable.ic_quick_selection_unselected);
            background.setColor(unSelectedBackgroundColor);
            nameView.setTextColor(unSelectedTextColor);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelectionState() {
        setSelected(!selected);
    }

    public Picker getItem() {
        return item;
    }
}
