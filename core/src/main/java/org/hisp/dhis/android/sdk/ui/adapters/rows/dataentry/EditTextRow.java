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

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

public class EditTextRow extends Row {
    private static final String EMPTY_FIELD = "";
    private static int LONG_TEXT_LINE_COUNT = 3;
    private static String rowTypeTemp;
    private TextView.OnEditorActionListener mOnEditorActionListener;


    public EditTextRow(String label, boolean mandatory, String warning, BaseValue baseValue, DataEntryRowTypes rowType, Event event) {
        mLabel = label;
        mMandatory = mandatory;
        mWarning = warning;
        mValue = baseValue;
        mRowType = rowType;
        mEvent = event;

        if (!DataEntryRowTypes.TEXT.equals(rowType) &&
                !DataEntryRowTypes.LONG_TEXT.equals(rowType) &&
                !DataEntryRowTypes.NUMBER.equals(rowType) &&
                !DataEntryRowTypes.INTEGER.equals(rowType) &&
                !DataEntryRowTypes.PERCENTAGE.equals(rowType) &&
                !DataEntryRowTypes.INTEGER_NEGATIVE.equals(rowType) &&
                !DataEntryRowTypes.INTEGER_ZERO_OR_POSITIVE.equals(rowType) &&
                !DataEntryRowTypes.INTEGER_POSITIVE.equals(rowType) &&
                !DataEntryRowTypes.NOT_SUPPORTED.equals(rowType)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
        checkNeedsForDescriptionButton();

    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater,
                        View convertView, ViewGroup container) {
        View view;
        final ValueEntryHolder holder;

        if (convertView != null && convertView.getTag() instanceof ValueEntryHolder) {
            view = convertView;
            holder = (ValueEntryHolder) view.getTag();
        } else {
            View root = inflater.inflate(R.layout.listview_row_edit_text, container, false);
            TextView label = (TextView) root.findViewById(R.id.text_label);
            TextView mandatoryIndicator = (TextView) root.findViewById(R.id.mandatory_indicator);
            TextView warningLabel = (TextView) root.findViewById(R.id.warning_label);
            TextView errorLabel = (TextView) root.findViewById(R.id.error_label);
            final EditText editText = (EditText) root.findViewById(R.id.edit_text_row);
            detailedInfoButton = root.findViewById(R.id.detailed_info_button_layout);

            if (DataEntryRowTypes.TEXT.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setHint(R.string.enter_text);
                editText.setSingleLine(true);
            } else if (DataEntryRowTypes.LONG_TEXT.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setHint(R.string.enter_long_text);
                editText.setLines(LONG_TEXT_LINE_COUNT);
            } else if (DataEntryRowTypes.NUMBER.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setHint(R.string.enter_number);
                editText.setSingleLine(true);
            } else if (DataEntryRowTypes.INTEGER.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setHint(R.string.enter_integer);
                editText.setSingleLine(true);
            } else if (DataEntryRowTypes.PERCENTAGE.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setHint(R.string.enter_percentage);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3), new MinMaxInputFilter(0, 100)});
                editText.setSingleLine(true);
            } else if (DataEntryRowTypes.INTEGER_NEGATIVE.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_SIGNED);
                editText.setHint(R.string.enter_negative_integer);
                editText.setFilters(new InputFilter[]{new NegInpFilter()});
                editText.setSingleLine(true);
            } else if (DataEntryRowTypes.INTEGER_ZERO_OR_POSITIVE.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setHint(R.string.enter_positive_integer_or_zero);
                editText.setFilters(new InputFilter[]{new PosOrZeroFilter()});
                editText.setSingleLine(true);
            } else if (DataEntryRowTypes.INTEGER_POSITIVE.equals(mRowType)) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setHint(R.string.enter_positive_integer);
                editText.setFilters(new InputFilter[]{new PosFilter()});
                editText.setSingleLine(true);
            }
            
            OnTextChangeListener listener = new OnTextChangeListener(inflater.getContext(), editText);
            listener.setRow(this);
            listener.setRowType(rowTypeTemp);
            holder = new ValueEntryHolder(label, mandatoryIndicator, warningLabel, errorLabel, editText, detailedInfoButton, listener );
            listener.setBaseValue(mValue);
            holder.editText.addTextChangedListener(listener);

            rowTypeTemp = mRowType.toString();
            root.setTag(holder);
            view = root;
        }

        if(!isEditable()) {
            holder.editText.setEnabled(false);
        } else {
            holder.editText.setEnabled(true);
        }

        holder.textLabel.setText(mLabel);
        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));
        holder.listener.setBaseValue(mValue);
        holder.listener.isMandatory = isMandatory();
        holder.listener.isEventComplete = isEventComplete();

        holder.editText.setText(mValue.getValue());
        holder.editText.setSelection(holder.editText.getText().length());

        if(mRowType.equals(DataEntryRowTypes.NOT_SUPPORTED)){
            holder.editText.setHint(R.string.unsupported_value_type);
            holder.editText.setEnabled(false);
        } else{
            holder.editText.setEnabled(true);
        }

        if(isDetailedInfoButtonHidden()) {
            holder.detailedInfoButton.setVisibility(View.INVISIBLE);
        }
        else {
            holder.detailedInfoButton.setVisibility(View.VISIBLE);
        }

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
        return mRowType.ordinal();
    }

    private static class ValueEntryHolder {
        final TextView textLabel;
        final TextView mandatoryIndicator;
        final TextView warningLabel;
        final TextView errorLabel;
        final EditText editText;
        final View detailedInfoButton;
        final OnTextChangeListener listener;

        public ValueEntryHolder(TextView textLabel,
                                TextView mandatoryIndicator, TextView warningLabel,
                                TextView errorLabel, EditText editText,
                                View detailedInfoButton,
                                OnTextChangeListener listener) {
            this.textLabel = textLabel;
            this.mandatoryIndicator = mandatoryIndicator;
            this.warningLabel = warningLabel;
            this.errorLabel = errorLabel;
            this.editText = editText;
            this.detailedInfoButton = detailedInfoButton;
            this.listener = listener;
        }
    }

    private static class OnTextChangeListener extends AbsTextWatcher {


        protected BaseValue value;
        Row row;
        String rowType;
        boolean isMandatory;
        boolean isEventComplete;
        EditText mEditText;
        Context context;

        public OnTextChangeListener(Context context, EditText editText){
            this.context = context;
            this.mEditText = editText;
        }

        public void setRowType(String type){
            rowType = type;
        }

        public void setRow(Row row) {
            this.row = row;
        }

        public void setBaseValue(BaseValue value) {
            this.value = value;
        }

        @Override
        public void afterTextChanged(Editable s) {
            String newValue = s != null ? s.toString() : EMPTY_FIELD;

            if(!isMandatory || (!isEventComplete || (!newValue.equals("")))) {
                if (!newValue.equals(value.getValue())) {
                    value.setValue(newValue);
                    RowValueChangedEvent rowValueChangeEvent = new RowValueChangedEvent(value,
                            rowType);
                    rowValueChangeEvent.setRow(row);
                    Dhis2Application.getEventBus().post(rowValueChangeEvent);
                }
            }else{
                Toast.makeText(context, context.getString(R.string.error_delete_mandatory_value), Toast.LENGTH_SHORT).show();
                mEditText.setText(value.getValue());
            }
        }
    }

    private static class NegInpFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spnStart, int spnEnd) {

            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) != '-')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    private static class PosOrZeroFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spStart, int spEnd) {

            if ((str.length() > 0) && (spn.length() > 0) && (spn.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            if ((spn.length() > 0) && (spStart == 0)
                    && (str.length() > 0) && (str.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    private static class PosFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence str, int start, int end,
                                   Spanned spn, int spnStart, int spnEnd) {

            if ((str.length() > 0) && (spnStart == 0) && (str.charAt(0) == '0')) {
                return EMPTY_FIELD;
            }

            return str;
        }
    }

    public class MinMaxInputFilter implements InputFilter {
        /**
         * Minimum allowed value for the input.
         * Null means there is no minimum limit.
         */
        private Integer minAllowed;

        /**
         * Maximum allowed value for the input.
         * Null means there is no maximum limit.
         */
        private Integer maxAllowed;



        public MinMaxInputFilter(Integer min){
            this.minAllowed=min;
        }

        public MinMaxInputFilter(Integer min, Integer max){
            this(min);
            this.maxAllowed=max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                // Remove the string out of destination that is to be replaced
                String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
                // Add the new string in
                newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
                if(newVal.length()>1 && newVal.startsWith("0")) {
                    return "";
                }
                int input = Integer.parseInt(newVal);
                if (inRange(input)) {
                    return null;
                }
            }catch (NumberFormatException nfe) {
            }
            return "";
        }

        /**
         * Checks if the value is between the specified range.
         *
         * @param value
         * @return
         */
        public boolean inRange(Integer value){
            boolean isMinOk=true;
            boolean isMaxOk=true;
            //No bounds -> ok
            if(minAllowed==null && maxAllowed==null){
                return true;
            }
            //Check minimum
            if(minAllowed!=null){
                if(value==null){
                    isMinOk=false;
                }else{
                    isMinOk=minAllowed<=value;
                }
            }
            //Check maximum
            if(maxAllowed!=null){
                if(value==null){
                    isMaxOk=false;
                }else{
                    isMaxOk=value<=maxAllowed;
                }
            }
            return isMinOk && isMaxOk;
        }


    }


    public interface ValueCallback {
        void saveValue(String newValue, BaseValue baseValue);
    }

    public void setOnEditorActionListener(
            TextView.OnEditorActionListener onEditorActionListener) {
        mOnEditorActionListener = onEditorActionListener;
    }
}

