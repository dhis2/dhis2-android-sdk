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

package org.hisp.dhis.android.sdk.utils.ui.rows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;

public class BooleanRow implements Row {
    private final LayoutInflater inflater;
    private final BaseValue dataValue;
    private BooleanRowHolder holder;
    private boolean editable = true;
    private String label;
    
    public BooleanRow(LayoutInflater inflater, String label, BaseValue dataValue) {
        this.inflater = inflater;
        this.label = label;
        this.dataValue = dataValue;
    }

    @Override
    public View getView(View convertView) {
        View view;
        
        if (convertView == null) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listview_row_boolean, null);
            TextView label = (TextView) rootView.findViewById(R.id.text_label);
            
            TrueButtonListener tListener = new TrueButtonListener(dataValue);
            FalseButtonListener fListener = new FalseButtonListener(dataValue);
            NoneButtonListener nListener = new NoneButtonListener(dataValue);
            
            CompoundButton tButton = (CompoundButton) rootView.findViewById(R.id.true_button);
            CompoundButton fButton = (CompoundButton) rootView.findViewById(R.id.false_button);
            CompoundButton nButton = (CompoundButton) rootView.findViewById(R.id.none_button);
            
            tButton.setOnCheckedChangeListener(tListener);
            fButton.setOnCheckedChangeListener(fListener);
            nButton.setOnCheckedChangeListener(nListener);
            
            holder = new BooleanRowHolder(label, tListener, fListener, 
                    nListener, tButton, fButton, nButton);
            
            rootView.setTag(holder);
            view = rootView;
        } else {
            view = convertView;
            holder = (BooleanRowHolder) convertView.getTag();
        }
        
        holder.textLabel.setText(label);
        
        holder.trueButtonListener.setField(dataValue);
        holder.falseButtonListener.setField(dataValue);
        holder.noneButtonListener.setField(dataValue);
        
        holder.trueButton.setOnCheckedChangeListener(holder.trueButtonListener);
        holder.falseButton.setOnCheckedChangeListener(holder.falseButtonListener);
        holder.noneButton.setOnCheckedChangeListener(holder.noneButtonListener);
        
        if (dataValue.getValue().equals(BaseValue.FALSE)) holder.falseButton.setChecked(true);
        else if (dataValue.getValue().equals(BaseValue.TRUE)) holder.trueButton.setChecked(true);
        else if (dataValue.getValue().equals(BaseValue.EMPTY_VALUE)) holder.noneButton.setChecked(true);
        setEditable(editable);
        
        return view;
    }

    @Override
    public TextView getEntryView() {
        return null;
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        if(holder!=null) {
            if(editable) {
                holder.trueButton.setEnabled(true);
                holder.falseButton.setEnabled(true);
                holder.noneButton.setEnabled(true);
            } else {
                holder.trueButton.setEnabled(false);
                holder.falseButton.setEnabled(false);
                holder.noneButton.setEnabled(false);
            }
        }
    }

    private class BooleanRowHolder {
        final TextView textLabel;
        
        final CompoundButton trueButton;
        final CompoundButton falseButton;
        final CompoundButton noneButton;
        
        final TrueButtonListener  trueButtonListener;
        final FalseButtonListener falseButtonListener;
        final NoneButtonListener  noneButtonListener;
        
        BooleanRowHolder(TextView tLabel, TrueButtonListener tListener,
                FalseButtonListener fListener, NoneButtonListener nListener,
                CompoundButton tButton, CompoundButton fButton, CompoundButton nButton) {
            
            textLabel = tLabel;
            
            trueButtonListener = tListener;
            falseButtonListener = fListener;
            noneButtonListener = nListener;
            
            trueButton = tButton;
            falseButton = fButton;
            noneButton = nButton;
        }
    }
    
    private class TrueButtonListener implements OnCheckedChangeListener {
        private BaseValue dataValue;
        
        TrueButtonListener(BaseValue dataValue) {
            this.dataValue = dataValue;
        }
        
        public void setField(BaseValue dataValue) {
            this.dataValue = dataValue;
        }

        @Override
        public void onCheckedChanged(CompoundButton button, boolean isChecked) {
            if (isChecked) dataValue.setValue(BaseValue.TRUE);
        } 
    }
    
    private class FalseButtonListener implements OnCheckedChangeListener {
        private BaseValue dataValue;
        
        FalseButtonListener(BaseValue dataValue) {
            this.dataValue = dataValue;
        }
        
        public void setField(BaseValue dataValue) {
            this.dataValue = dataValue;
        }

        @Override
        public void onCheckedChanged(CompoundButton button, boolean isChecked) {
            if (isChecked)  dataValue.setValue(BaseValue.FALSE);
        } 
    }

    private class NoneButtonListener implements OnCheckedChangeListener {
        private BaseValue dataValue;
        
        NoneButtonListener(BaseValue dataValue) {
            this.dataValue = dataValue;
        }
        
        public void setField(BaseValue dataValue) {
            this.dataValue = dataValue;
        }

        @Override
        public void onCheckedChanged(CompoundButton button, boolean isChecked) {
            if (isChecked)  dataValue.setValue(BaseValue.EMPTY_VALUE);
        } 
    }
    
}





