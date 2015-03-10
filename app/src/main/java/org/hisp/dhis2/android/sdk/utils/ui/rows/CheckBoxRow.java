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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStageDataElement;

public class CheckBoxRow implements Row {
    private LayoutInflater inflater;
    private ProgramStageDataElement programStageDataElement;
    private DataValue dataValue;
    
    public CheckBoxRow(LayoutInflater inflater, ProgramStageDataElement programStageDataElement, DataValue dataValue) {
        this.inflater = inflater;
        this.programStageDataElement = programStageDataElement;
        this.dataValue = dataValue;
    }

    @Override
    public View getView(View convertView) {
        View view;
        CheckBoxHolder holder;
        
        if (convertView == null) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listview_row_checkbox, null);
            TextView textLabel = (TextView) rootView.findViewById(R.id.text_label);
            CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.checkbox);
            CheckBoxListener listener = new CheckBoxListener(dataValue);
            
            checkBox.setOnCheckedChangeListener(listener);
            holder = new CheckBoxHolder(textLabel, checkBox, listener);
            
            rootView.setTag(holder);
            view = rootView;
        } else {
            view = convertView;
            holder = (CheckBoxHolder) view.getTag();
        }
        
        holder.textLabel.setText(MetaDataController.getDataElement(programStageDataElement.dataElement).getName());
        holder.listener.setField(dataValue);
        
        if (dataValue.value.equals(DataValue.TRUE)) holder.checkBox.setChecked(true);
        else if (dataValue.value.equals(DataValue.EMPTY_VALUE)) holder.checkBox.setChecked(false);
        
        return view;
    }
    
    private class CheckBoxListener implements OnCheckedChangeListener {
        private DataValue dataValue;
        
        CheckBoxListener(DataValue dataValue) {
            this.dataValue = dataValue;
        }
        
        void setField(DataValue dataValue) {
            this.dataValue = dataValue;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                dataValue.value = DataValue.TRUE ;
            } else {
                dataValue.value = DataValue.EMPTY_VALUE;
            }
        }
        
    }

    private class CheckBoxHolder {
        final TextView textLabel;
        final CheckBox checkBox;
        final CheckBoxListener listener;
        
        CheckBoxHolder(TextView textLabel, CheckBox checkBox, CheckBoxListener listener) {
            this.textLabel = textLabel;
            this.checkBox = checkBox;
            this.listener = listener;
        }
    }   
}


