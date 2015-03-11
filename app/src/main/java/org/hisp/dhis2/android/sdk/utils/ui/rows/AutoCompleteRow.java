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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.Option;
import org.hisp.dhis2.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStageDataElement;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteRow implements Row {	
	private ArrayAdapter<String> adapter;
    private LayoutInflater inflater;
    private ProgramStageDataElement programStageDataElement;
	private OptionSet optionset;
    private ArrayList<String> optionNames;
    private List<Option> options;
    private DataValue dataValue;
    private AutoCompleteRowHolder holder;
    
    public AutoCompleteRow(LayoutInflater inflater, ProgramStageDataElement programStageDataElement,
                           OptionSet optionset, DataValue dataValue, Context context) {
        this.inflater = inflater;
        this.programStageDataElement = programStageDataElement;
        this.optionset = optionset;
        this.dataValue = dataValue;


        if (optionset != null) {
            options = optionset.getOptions();
            optionNames = new ArrayList<String>();
            for(Option option: options) {
                optionNames.add(option.getName());
            }
        }
        adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, optionNames);
    }

    @Override
    public View getView(View convertView) {
        View view;
        
        if (convertView == null) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.listview_row_autocomplete, null);
            
            TextView textLabel = (TextView) rootView.findViewById(R.id.text_label);
            AutoCompleteTextView autoComplete = (AutoCompleteTextView) rootView.findViewById(R.id.chooseOption);
            OnFocusListener onFocusChangeListener = new OnFocusListener(autoComplete, optionNames);
            EditTextWatcher textWatcher = new EditTextWatcher(dataValue);
            
            autoComplete.setOnFocusChangeListener(onFocusChangeListener);
            autoComplete.addTextChangedListener(textWatcher);
            
            ImageView showOptions = (ImageView) rootView.findViewById(R.id.showDropDownList);
            DropDownButtonListener listener = new DropDownButtonListener(autoComplete);
            showOptions.setOnClickListener(listener);
            
            holder = new AutoCompleteRowHolder(textLabel, autoComplete, showOptions,
                    listener, onFocusChangeListener, textWatcher);
            
            rootView.setTag(holder);
            view = rootView;
        } else {
            view = convertView;
            holder = (AutoCompleteRowHolder) view.getTag();
        }

        holder.textLabel.setText(MetaDataController.getDataElement(programStageDataElement.dataElement).getName());
        
        holder.autoComplete.setAdapter(adapter);
        holder.onFocusListener.setValues(holder.autoComplete, optionNames);
        holder.autoComplete.setOnFocusChangeListener(holder.onFocusListener);
        holder.textWatcher.setDataValue(dataValue);
        holder.autoComplete.addTextChangedListener(holder.textWatcher);
        holder.autoComplete.setText(dataValue.value);
        
        holder.listener.setAutoComplete(holder.autoComplete);
        holder.button.setOnClickListener(holder.listener);
        holder.autoComplete.clearFocus();
        
        return view;
    }

    @Override
    public TextView getEntryView() {
        return holder.autoComplete;
    }
    
    private class AutoCompleteRowHolder {
        final TextView textLabel;
        final AutoCompleteTextView autoComplete;
        final ImageView button;
        final DropDownButtonListener listener;
        final OnFocusListener onFocusListener;
        final EditTextWatcher textWatcher;
        
        AutoCompleteRowHolder(TextView textLabel, AutoCompleteTextView autoComplete,
                ImageView button, DropDownButtonListener listener,
                OnFocusListener onFocusListener, EditTextWatcher textWatcher) {
            
            this.textLabel = textLabel;
            this.autoComplete = autoComplete;
            this.button = button;
            this.listener = listener;
            this.onFocusListener = onFocusListener;
            this.textWatcher = textWatcher;
        }
    }
    
    private class DropDownButtonListener implements OnClickListener {
        private AutoCompleteTextView autoComplete;
        
        DropDownButtonListener(AutoCompleteTextView autoComplete) {
            this.autoComplete = autoComplete;
        }
        
        void setAutoComplete(AutoCompleteTextView autoComplete) {
            this.autoComplete = autoComplete;
        }

        @Override
        public void onClick(View v) {
            autoComplete.showDropDown();
        }
        
    }
    
    private class OnFocusListener implements OnFocusChangeListener {
        private AutoCompleteTextView autoComplete;
        private List<String> options;
        
        public OnFocusListener(AutoCompleteTextView autoComplete, List<String> options) {
            this.autoComplete = autoComplete;
            this.options = options;
        }
        
        public void setValues(AutoCompleteTextView autoComplete, List<String> options) {
            this.autoComplete = autoComplete;
            this.options = options;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (!hasFocus) {            
                String choice = autoComplete.getText().toString();
                if (!options.contains(choice)) {
                    autoComplete.setText("");
                }
            }
        }     
    }
}