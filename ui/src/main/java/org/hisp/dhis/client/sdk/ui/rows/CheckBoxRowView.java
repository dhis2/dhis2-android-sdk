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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.models.FormEntityCheckBox;

public class CheckBoxRowView implements RowView {
    private static final String TRUE = "true";
    private static final String EMPTY_FIELD = "";

    public CheckBoxRowView() {
        // explicit empty constructor
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new CheckBoxRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_checkbox, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, FormEntity formEntity) {
        FormEntityCheckBox entity = (FormEntityCheckBox) formEntity;
        ((CheckBoxRowViewHolder) viewHolder).update(entity);
    }

    private static class CheckBoxRowViewHolder extends RecyclerView.ViewHolder {
        public final CheckBox checkBox;
        public final TextView textViewLabel;
        public final OnCheckBoxListener onCheckBoxListener;

        public CheckBoxRowViewHolder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_row_checkbox);
            textViewLabel = (TextView) itemView.findViewById(R.id.textview_row_label);

            onCheckBoxListener = new OnCheckBoxListener();
            checkBox.setOnCheckedChangeListener(onCheckBoxListener);

            OnRowClickListener rowClickListener = new OnRowClickListener(checkBox);
            itemView.setOnClickListener(rowClickListener);
        }

        public void update(FormEntityCheckBox dataEntity) {
            textViewLabel.setText(dataEntity.getLabel());
            onCheckBoxListener.setDataEntity(dataEntity);

            if (EMPTY_FIELD.equals(dataEntity.getValue())) {
                checkBox.setChecked(false);
            } else if (TRUE.equals(dataEntity.getValue())) {
                checkBox.setChecked(true);
            }
        }
    }

    private static class OnRowClickListener implements View.OnClickListener {
        private final CheckBox checkBox;

        public OnRowClickListener(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View v) {
            checkBox.setChecked(!checkBox.isChecked());
        }
    }

    private static class OnCheckBoxListener implements CompoundButton.OnCheckedChangeListener {
        private FormEntityCheckBox dataEntity;

        public void setDataEntity(FormEntityCheckBox dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String newValue = isChecked ? TRUE : EMPTY_FIELD;
            dataEntity.setValue(newValue, true);
        }
    }
}
