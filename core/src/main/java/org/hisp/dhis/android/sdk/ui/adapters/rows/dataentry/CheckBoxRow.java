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

package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;

import static android.text.TextUtils.isEmpty;

public class CheckBoxRow extends Row {
    private static final String TRUE = "true";
    private static final String EMPTY_FIELD = "";

    private final String mLabel;

    public CheckBoxRow(String label, boolean mandatory, String warning, BaseValue mValue) {
        mLabel = label;
        this.mValue = mValue;
        this.mWarning = warning;
        this.mMandatory = mandatory;

        checkNeedsForDescriptionButton();

    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater,
                        View convertView, ViewGroup container) {
        View view;
        CheckBoxHolder holder;

        if (convertView != null && convertView.getTag() instanceof CheckBoxHolder) {
            view = convertView;
            holder = (CheckBoxHolder) view.getTag();
        } else {
            View root = inflater.inflate(R.layout.listview_row_checkbox, container, false);
            TextView textLabel = (TextView) root.findViewById(R.id.text_label);
            TextView mandatoryIndicator = (TextView) root.findViewById(R.id.mandatory_indicator);
            TextView warningLabel = (TextView) root.findViewById(R.id.warning_label);
            TextView errorLabel = (TextView) root.findViewById(R.id.error_label);
            CheckBox checkBox = (CheckBox) root.findViewById(R.id.checkbox);
//            detailedInfoButton = root.findViewById(R.id.detailed_info_button_layout);

            CheckBoxListener listener = new CheckBoxListener();
            OnCheckBoxRowClickListener onCheckBoxRowClickListener = new OnCheckBoxRowClickListener();
            holder = new CheckBoxHolder(root, textLabel, mandatoryIndicator, warningLabel, errorLabel, checkBox ,listener, onCheckBoxRowClickListener);

            holder.checkBox.setOnCheckedChangeListener(holder.listener);
            holder.rootView.setOnClickListener(holder.onCheckBoxRowClickListener);
//            holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));


            root.setTag(holder);
            view = root;
        }

        if(!isEditable()) {
            holder.checkBox.setEnabled(false);
            holder.textLabel.setEnabled(false);
        } else {
            holder.textLabel.setEnabled(true);
            holder.checkBox.setEnabled(true);
        }
        holder.textLabel.setText(mLabel);
        holder.listener.setValue(mValue);

        holder.onCheckBoxRowClickListener.setCheckBox(holder.checkBox);
        holder.onCheckBoxRowClickListener.setEditable(holder.checkBox.isEnabled());

        String stringValue = mValue.getValue();
        if (TRUE.equalsIgnoreCase(stringValue)) {
            holder.checkBox.setChecked(true);
        } else if (isEmpty(stringValue)) {
            holder.checkBox.setChecked(false);
        }

//        if(isDetailedInfoButtonHidden()) {
//            holder.detailedInfoButton.setVisibility(View.INVISIBLE);
//        }
//        else {
//            holder.detailedInfoButton.setVisibility(View.VISIBLE);
//        }

        if(mWarning == null) {
            holder.warningLabel.setVisibility(View.GONE);
        } else {
            holder.warningLabel.setVisibility(View.VISIBLE);
            holder.warningLabel.setText(mWarning);
        }

        if(mError == null) {
            holder.errorLabel.setVisibility(View.GONE);
        } else {
            holder.errorLabel.setVisibility(View.VISIBLE);
            holder.errorLabel.setText(mError);
        }

        if(!mMandatory) {
            holder.mandatoryIndicator.setVisibility(View.GONE);
        } else {
            holder.mandatoryIndicator.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.TRUE_ONLY.ordinal();
    }


    private static class CheckBoxListener implements OnCheckedChangeListener {
        private BaseValue value;

        public void setValue(BaseValue value) {
            this.value = value;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String newValue;
            if(isChecked) {
                newValue = TRUE;
            } else {
                newValue = EMPTY_FIELD;
            }

            if(!newValue.toString().equals(value.getValue())) {
                value.setValue(newValue);
                Dhis2Application.getEventBus().post(new RowValueChangedEvent(value, DataEntryRowTypes.TRUE_ONLY.toString()));
            }

        }
    }

    private static class OnCheckBoxRowClickListener implements View.OnClickListener {
        CheckBox checkBox;
        boolean editable;

        public void setEditable(boolean editable) {
            this.editable = editable;
        }

        public void setCheckBox(CheckBox checkBox) {
            this.checkBox = checkBox;
        }

        @Override
        public void onClick(View view) {
            if(editable) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        }
    }

    private static class CheckBoxHolder {
        final View rootView;
        final TextView textLabel;
        final TextView mandatoryIndicator;
        final TextView warningLabel;
        final TextView errorLabel;
        final CheckBox checkBox;
        final CheckBoxListener listener;
        final OnCheckBoxRowClickListener onCheckBoxRowClickListener;

        public CheckBoxHolder(View rootView, TextView textLabel, TextView mandatoryIndicator, TextView warningLabel, TextView errorLabel, CheckBox checkBox,
                              CheckBoxListener listener,
                              OnCheckBoxRowClickListener onCheckBoxRowClickListener) {
            this.rootView = rootView;
            this.textLabel = textLabel;
            this.mandatoryIndicator = mandatoryIndicator;
            this.warningLabel = warningLabel;
            this.errorLabel = errorLabel;
            this.checkBox = checkBox;
            this.listener = listener;
            this.onCheckBoxRowClickListener = onCheckBoxRowClickListener;
        }
    }


}


