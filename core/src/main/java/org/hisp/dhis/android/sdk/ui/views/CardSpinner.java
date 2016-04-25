/*
 *  Copyright (c) 2016, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import org.hisp.dhis.android.sdk.R;


public class CardSpinner extends CardView {
    private FontSpinner spinner;
    private CharSequence hint;

    public CardSpinner(Context context) {
        super(context);
        init(context);
    }

    public CardSpinner(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(context);

        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(attributes, R.styleable.ButtonHint);
            hint = attrs.getString(R.styleable.ButtonHint_hint);
            setText(hint);
            attrs.recycle();
        }
    }

    private void init(Context context) {
        spinner = new FontSpinner(context);
        spinner.setClickable(true);
        //spinner.getUid(getUid()); causes the app to crash because of duplicate ids
        spinner.setBackgroundResource(R.drawable.spinner_background_holo_light);
        spinner.setFont(getContext().getString(R.string.regular_font_name));

        addView(spinner);
    }

    public void setText(CharSequence sequence) {
        if (spinner != null && sequence != null) {
            //spinner.setText(sequence);
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        spinner.setOnClickListener(listener);
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        spinner.setEnabled(isEnabled);
        setText(hint);
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        spinner.setOnItemSelectedListener(listener);
    }

    public void setAdapter(ArrayAdapter adapter) {
        spinner.setAdapter(adapter);
    }

    public int getSelectedItemPosition() {
        return spinner.getSelectedItemPosition();
    }

    public void setSelection(int position) {
        spinner.setSelection(position);
    }
}
