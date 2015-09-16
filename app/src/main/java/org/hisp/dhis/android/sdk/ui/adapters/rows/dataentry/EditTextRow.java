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

package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;

public class EditTextRow extends Row {
    private static final String EMPTY_FIELD = "";
    private static int LONG_TEXT_LINE_COUNT = 3;




    public EditTextRow(String label, BaseValue baseValue, DataEntryRowTypes rowType) {
        mLabel = label;
        mValue = baseValue;
        mRowType = rowType;


        if (!DataEntryRowTypes.TEXT.equals(rowType) &&
                !DataEntryRowTypes.LONG_TEXT.equals(rowType) &&
                !DataEntryRowTypes.NUMBER.equals(rowType) &&
                !DataEntryRowTypes.INTEGER.equals(rowType) &&
                !DataEntryRowTypes.INTEGER_NEGATIVE.equals(rowType) &&
                !DataEntryRowTypes.INTEGER_ZERO_OR_POSITIVE.equals(rowType) &&
                !DataEntryRowTypes.INTEGER_POSITIVE.equals(rowType)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
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
            EditText editText = (EditText) root.findViewById(R.id.edit_text_row);
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

            OnTextChangeListener listener = new OnTextChangeListener();
            holder = new ValueEntryHolder(label, editText, detailedInfoButton, listener );
            holder.editText.addTextChangedListener(listener);

            if(!isEditable())
                holder.editText.setEnabled(false);
            else
            {
                holder.editText.setEnabled(true);
            }

            root.setTag(holder);
            view = root;
        }

        holder.textLabel.setText(mLabel);
        holder.listener.setBaseValue(mValue);
        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));

        holder.editText.setText(mValue.getValue());
        holder.editText.clearFocus();



        return view;
    }

    @Override
    public int getViewType() {
        return mRowType.ordinal();
    }



    private static class ValueEntryHolder {
        final TextView textLabel;
        final EditText editText;
        final View detailedInfoButton;
        final OnTextChangeListener listener;


        public ValueEntryHolder(TextView textLabel,
                                EditText editText,
                                View detailedInfoButton,
                                OnTextChangeListener listener) {
            this.textLabel = textLabel;
            this.editText = editText;
            this.detailedInfoButton = detailedInfoButton;
            this.listener = listener;
        }
    }

    private static class OnTextChangeListener extends AbsTextWatcher {
        private BaseValue value;

        public void setBaseValue(BaseValue value) {
            this.value = value;
        }

        @Override
        public void afterTextChanged(Editable s) {
            String newValue = s != null ? s.toString() : EMPTY_FIELD;
            if (!newValue.equals(value.getValue())) {
                value.setValue(newValue);
                Dhis2Application.getEventBus()
                        .post(new RowValueChangedEvent(value));
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
}