/*
 *  Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis2.android.sdk.utils.ui.rows;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramIndicator;

public class IndicatorRow implements Row {
    private static final String CLASS_TAG = "IndicatorRow";
    private final LayoutInflater inflater;
    private String value;
    private String label;
    private final ProgramIndicator programIndicator;
    private TextView valueView;
    private TextView labelView;

    public IndicatorRow(LayoutInflater inflater, String label, String value, ProgramIndicator programIndicator) {
        this.inflater = inflater;
        this.label = label;
        this.value = value;
        this.programIndicator = programIndicator;
    }

    @Override
    public View getView(View convertView) {
        View view;

        ViewGroup rowRoot = (ViewGroup) inflater.inflate(R.layout.listview_row_indicator, null);
        labelView = (TextView) rowRoot.findViewById(R.id.text_label);
        valueView = (TextView) rowRoot.findViewById(R.id.textView_number_row);

        view = rowRoot;

        if(value==null) {
            value = "";
        }

        valueView.setText(value);
        labelView.setText(label);
        return view;
    }

    @Override
    public TextView getEntryView() {
        return valueView;
    }

    @Override
    public void setEditable(boolean editable) {

    }

    public void setValue(String value) {
        this.value = value;
        valueView.setText(value);
    }

    public String getValue() {
        return this.value;
    }

    public ProgramIndicator getProgramIndicator() {
        return this.programIndicator;
    }

}
