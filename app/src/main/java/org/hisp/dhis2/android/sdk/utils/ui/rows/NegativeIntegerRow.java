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

import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramStageDataElement;

public class NegativeIntegerRow implements Row {
    private final LayoutInflater inflater;
    private final ProgramStageDataElement programStageDataElement;
    private final DataValue dataValue;
    private EditTextHolder holder;
    
    public NegativeIntegerRow(LayoutInflater inflater, ProgramStageDataElement programStageDataElement, DataValue dataValue) {
        this.inflater = inflater;
        this.programStageDataElement = programStageDataElement;
        this.dataValue = dataValue;
    }

    @Override
    public View getView(View convertView) {
        View view;
        
        if (convertView == null) {
            ViewGroup rowRoot = (ViewGroup) inflater.inflate(R.layout.listview_row_integer_negative, null);
            TextView label = (TextView) rowRoot.findViewById(R.id.text_label);
            EditText editText = (EditText) rowRoot.findViewById(R.id.edit_integer_neg_row);
           
            EditTextWatcher watcher = new EditTextWatcher(dataValue);
            editText.addTextChangedListener(watcher);
            editText.setFilters(new InputFilter[]{new InpFilter()});
            
            holder = new EditTextHolder(label, editText, watcher);
            rowRoot.setTag(holder);
            view = rowRoot;
        } else {
            view = convertView;
            holder = (EditTextHolder) view.getTag();
        }
        
        holder.textLabel.setText(MetaDataController.getDataElement(programStageDataElement.dataElement).getName());
        
        holder.textWatcher.setDataValue(dataValue);
        holder.editText.addTextChangedListener(holder.textWatcher);
        holder.editText.setText(dataValue.value);
        holder.editText.clearFocus();

        return view;
    }

    @Override
    public TextView getEntryView() {
        return holder.editText;
    }
    
    private class InpFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                Spanned spn, int spnStart, int spnEnd) {
            
            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) != '-')) {
                return "";
            }
            
            return str;
        }   
    }
}
