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

package org.hisp.dhis.client.sdk.ui.rows;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.models.FormEntityRadioButtons;

public class RadioButtonRowView implements RowView {
    private static final String EMPTY_FIELD = "";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new RadioButtonRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_radiobutton, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, FormEntity formEntity) {
        FormEntityRadioButtons entity = (FormEntityRadioButtons) formEntity;
        ((RadioButtonRowViewHolder) viewHolder).update(entity);
    }

    private static class RadioButtonRowViewHolder extends RecyclerView.ViewHolder {
        public final TextView labelTextView;
        public final RadioGroup radioGroup;
        public final RadioButton firstRadioButton;
        public final RadioButton secondRadioButton;
        public final OnCheckedChangedListener onCheckedChangedListener;

        public RadioButtonRowViewHolder(View itemView) {
            super(itemView);

            labelTextView = (TextView) itemView
                    .findViewById(R.id.textview_row_label);
            radioGroup = (RadioGroup) itemView
                    .findViewById(R.id.radiogroup_radiobutton_row);
            firstRadioButton = (RadioButton) itemView
                    .findViewById(R.id.radiobutton_row_radiobutton_first);
            secondRadioButton = (RadioButton) itemView
                    .findViewById(R.id.radiobutton_row_radiobutton_second);

            firstRadioButton.setText(itemView.getContext().getString(R.string.yes));
            secondRadioButton.setText(itemView.getContext().getString(R.string.no));

            onCheckedChangedListener = new OnCheckedChangedListener();
            radioGroup.setOnCheckedChangeListener(onCheckedChangedListener);
        }

        public void update(FormEntityRadioButtons dataEntity) {
            onCheckedChangedListener.setDataEntity(dataEntity);
            labelTextView.setText(dataEntity.getLabel());

            if (TRUE.equals(dataEntity.getValue())) {
                firstRadioButton.setChecked(true);
            } else if (FALSE.equals(dataEntity.getValue())) {
                secondRadioButton.setChecked(true);
            } else {
                radioGroup.clearCheck();
            }
        }
    }

    private static class OnCheckedChangedListener implements RadioGroup.OnCheckedChangeListener {
        private FormEntityRadioButtons dataEntity;

        public void setDataEntity(FormEntityRadioButtons dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            String newValue;

            if (checkedId == R.id.radiobutton_row_radiobutton_first) {
                newValue = TRUE;
            } else if (checkedId == R.id.radiobutton_row_radiobutton_second) {
                newValue = FALSE;
            } else {
                newValue = EMPTY_FIELD;
            }

            dataEntity.setValue(newValue, true);
        }
    }
}
