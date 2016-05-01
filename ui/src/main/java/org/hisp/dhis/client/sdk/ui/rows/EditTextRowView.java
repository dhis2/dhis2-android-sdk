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

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.models.DataEntityEditText;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;

import static android.text.TextUtils.isEmpty;

public final class EditTextRowView implements RowView {

    public EditTextRowView() {
        // explicit empty constructor
    }

    @Override
    public ViewHolder onCreateViewHolder(LayoutInflater inflater, ViewGroup parent) {
        return new EditTextRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_edittext, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, DataEntity dataEntity) {
        DataEntityEditText entity = (DataEntityEditText) dataEntity;
        ((EditTextRowViewHolder) viewHolder).update(entity);
    }

    private static class EditTextRowViewHolder extends RecyclerView.ViewHolder {
        private static final int LONG_TEXT_LINE_COUNT = 3;

        public final TextView textViewLabel;
        public final TextInputLayout textInputLayout;
        public final EditText editText;

        public final OnFocusChangeListener focusChangeListener;
        public final OnValueChangedListener onValueChangedListener;

        public EditTextRowViewHolder(View itemView) {
            super(itemView);

            textViewLabel = (TextView) itemView
                    .findViewById(R.id.textview_row_label);
            textInputLayout = (TextInputLayout) itemView
                    .findViewById(R.id.edittext_row_textinputlayout);
            editText = (EditText) itemView
                    .findViewById(R.id.edittext_row_edittext);

            focusChangeListener = new OnFocusChangeListener(textInputLayout, editText);
            onValueChangedListener = new OnValueChangedListener();

            editText.setOnFocusChangeListener(focusChangeListener);
            editText.addTextChangedListener(onValueChangedListener);
        }

        public void update(DataEntityEditText entity) {
            // configure edittext according to entity
            configureViews(entity);

            // update callbacks with current entities
            onValueChangedListener.setDataEntity(entity);
            textViewLabel.setText(entity.getLabel());
            editText.setText(entity.getValue());

            // edittext value
            CharSequence hint = !isEmpty(entity.getValue()) ? null : entity.getHint();
            textInputLayout.setHint(hint);
        }

        private boolean configureViews(DataEntityEditText dataEntityText) {
            switch (dataEntityText.getInputType()) {
                case TEXT:
                    return configure(dataEntityText, R.string.enter_text,
                            InputType.TYPE_CLASS_TEXT, true);
                case LONG_TEXT:
                    return configure(dataEntityText, R.string.enter_long_text,
                            InputType.TYPE_CLASS_TEXT, false);
                case NUMBER:
                    return configure(dataEntityText, R.string.enter_number,
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL |
                                    InputType.TYPE_NUMBER_FLAG_SIGNED, true);
                case INTEGER:
                    return configure(dataEntityText, R.string.enter_integer,
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED, true);
                case INTEGER_NEGATIVE:
                    return configure(dataEntityText, R.string.enter_negative_integer,
                            InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED, true);
                case INTEGER_ZERO_OR_POSITIVE:
                    return configure(dataEntityText, R.string.enter_positive_integer_or_zero,
                            InputType.TYPE_CLASS_NUMBER, true);
                case INTEGER_POSITIVE:
                    return configure(dataEntityText, R.string.enter_positive_integer,
                            InputType.TYPE_CLASS_NUMBER, true);
                default:
                    return false;
            }
        }

        private boolean configure(DataEntityEditText entity, int hint, int inputType, boolean line) {
            textInputLayout.setHint(editText.getContext().getString(hint));
            editText.setInputType(inputType);
            editText.setSingleLine(line);
            editText.setFilters(new InputFilter[]{
                    new ValueFilter(entity)
            });

            if (!line) {
                editText.setLines(LONG_TEXT_LINE_COUNT);
            }

            return true;
        }
    }

    private static class OnValueChangedListener extends AbsTextWatcher {
        private DataEntityEditText dataEntity;

        public void setDataEntity(DataEntityEditText dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (dataEntity != null) {
                dataEntity.setValue(editable.toString());
            }
        }
    }

    private static class OnFocusChangeListener implements View.OnFocusChangeListener {
        private final TextInputLayout textInputLayout;
        private final EditText editText;
        private final CharSequence hint;

        public OnFocusChangeListener(TextInputLayout inputLayout, EditText editText) {
            this.textInputLayout = inputLayout;
            this.editText = editText;
            this.hint = textInputLayout.getHint();
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                textInputLayout.setHint(hint);
            } else {
                if (!isEmpty(editText.getText().toString())) {
                    textInputLayout.setHint(null);
                }
            }
        }

        public CharSequence getHint() {
            return hint;
        }
    }

    private static class ValueFilter implements InputFilter {
        private final DataEntityEditText dataEntityText;

        public ValueFilter(DataEntityEditText dataEntityText) {
            this.dataEntityText = dataEntityText;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            return source;
        }
    }
}
