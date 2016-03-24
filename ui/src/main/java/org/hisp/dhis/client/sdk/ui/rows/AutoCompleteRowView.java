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
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.dialogs.AutoCompleteDialogFragment;
import org.hisp.dhis.client.sdk.ui.models.DataEntity;
import org.hisp.dhis.client.sdk.ui.models.IDataEntity;
import org.hisp.dhis.client.sdk.ui.views.callbacks.AbsTextWatcher;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.DefaultPickable;
import org.hisp.dhis.client.sdk.ui.views.chainablepickerview.IPickable;

import java.util.ArrayList;

import static android.text.TextUtils.isEmpty;

public class AutoCompleteRowView implements IRowView {
    private ArrayList<IPickable> options;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(FragmentManager fragmentManager,
                                                      LayoutInflater inflater, ViewGroup parent,
                                                      DataEntity.Type type) {
        if (!RowViewTypeMatcher.matchToRowView(type).equals(AutoCompleteRowView.class)) {
            throw new IllegalArgumentException("Unsupported row type");
        }
        return new AutoCompleteRowViewHolder(inflater.inflate(
                R.layout.recyclerview_row_autocomplete, parent, false), type, fragmentManager);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, IDataEntity dataEntity) {
        AutoCompleteRowViewHolder autoCompleteRowViewHolder = (AutoCompleteRowViewHolder) holder;
        DataEntity entity = (DataEntity) dataEntity;
        autoCompleteRowViewHolder.textViewLabel.setText(entity.getLabel());
        autoCompleteRowViewHolder.optionText.setText(entity.getValue());
        autoCompleteRowViewHolder.onClearListener.setDataEntity(entity);
        autoCompleteRowViewHolder.onEditTextClickedListener.setOptions(options);
        autoCompleteRowViewHolder.onOptionSelectedListener.setDataEntity(entity);

        autoCompleteRowViewHolder.setOnOptionSelectedListener();
    }

    public void setOptions(ArrayList<IPickable> options) {
        this.options = options;
    }

    private static class AutoCompleteRowViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewLabel;
        public final TextInputLayout textInputLayout;
        public final EditText optionText;
        public final ImageButton clearButton;

        public final OnClearListener onClearListener;
        public final OnValueChangedListener onValueChangedListener;
        public final OnFocusChangeListener onFocusChangeListener;
        public final OnEditTextClickedListener onEditTextClickedListener;
        public OnOptionSelectedListener onOptionSelectedListener;

        public AutoCompleteRowViewHolder(View itemView, DataEntity.Type type,
                                         FragmentManager fragmentManager) {
            super(itemView);

            textViewLabel = (TextView) itemView.findViewById(R.id.autocomplete_row_label);
            textInputLayout = (TextInputLayout) itemView.findViewById(
                    R.id.autocomplete_row_text_input_layout);

            optionText = (EditText) itemView.findViewById(R.id.autocomplete_row_option_text);
            clearButton = (ImageButton) itemView.findViewById(R.id.button_clear);

            textInputLayout.setHint(itemView.getContext().getString(R.string.find_option));

            onClearListener = new OnClearListener(optionText);
            onFocusChangeListener = new OnFocusChangeListener(textInputLayout, optionText);
            onValueChangedListener = new OnValueChangedListener();
            onOptionSelectedListener = new OnOptionSelectedListener(optionText);
            onEditTextClickedListener = new OnEditTextClickedListener(fragmentManager,
                    onOptionSelectedListener);


            optionText.addTextChangedListener(onValueChangedListener);
            optionText.setOnFocusChangeListener(onFocusChangeListener);
            optionText.setOnClickListener(onEditTextClickedListener);
            clearButton.setOnClickListener(onClearListener);
        }

        public void setOnOptionSelectedListener() {
            if (onEditTextClickedListener != null && onOptionSelectedListener != null) {
                onEditTextClickedListener.setListener(onOptionSelectedListener);
            }
        }
    }

    private static class OnValueChangedListener extends AbsTextWatcher {
        private DataEntity dataEntity;

        public void setDataEntity(DataEntity dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (dataEntity != null) {
                dataEntity.updateValue(editable.toString());
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

    public static class OnOptionSelectedListener implements AutoCompleteDialogFragment
            .OnOptionSelectedListener {
        private final TextView valueTextView;
        private DataEntity dataEntity;

        public OnOptionSelectedListener(TextView valueTextView) {
            this.valueTextView = valueTextView;
        }

        public void setDataEntity(DataEntity dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void onOptionSelected(IPickable pickable) {
            valueTextView.setText(pickable.toString());
            dataEntity.updateValue(pickable.toString());
        }
    }

    private static class OnClearListener implements View.OnClickListener {
        private static final String EMPTY_FIELD = "";
        private final EditText editText;
        private DataEntity dataEntity;

        public OnClearListener(EditText editText) {
            this.editText = editText;
        }

        public void setDataEntity(DataEntity dataEntity) {
            this.dataEntity = dataEntity;
        }

        @Override
        public void onClick(View view) {
            editText.setText(EMPTY_FIELD);
            dataEntity.updateValue(EMPTY_FIELD);
        }
    }

    private static class OnEditTextClickedListener implements View.OnClickListener {
        AutoCompleteDialogFragment mOptionDialogFragment;
        private FragmentManager fragmentManager;
        private ArrayList<IPickable> options;
        private OnOptionSelectedListener onOptionSelectedListener;

        public OnEditTextClickedListener(FragmentManager fragmentManager,
                                         OnOptionSelectedListener onOptionSelectedListener) {
            this.fragmentManager = fragmentManager;
            this.onOptionSelectedListener = onOptionSelectedListener;

            if (mOptionDialogFragment != null) {
                mOptionDialogFragment.setOnOptionSelectedListener(onOptionSelectedListener);
            }
        }

        @Override
        public void onClick(View v) {
            if (options == null) {
                options = new ArrayList<>();
                options.add(new DefaultPickable("Male", "Male"));
                options.add(new DefaultPickable("Female", "Female"));
            }

            mOptionDialogFragment = AutoCompleteDialogFragment.newInstance("Find option:",
                    options, onOptionSelectedListener);
            mOptionDialogFragment.show(fragmentManager, "tag");
        }

        public void setOptions(ArrayList<IPickable> options) {
            this.options = options;
        }

        public void setListener(OnOptionSelectedListener listener) {
            AutoCompleteDialogFragment mOptionDialogFragment;
            this.onOptionSelectedListener = listener;
            if (listener != null) {
                mOptionDialogFragment = (AutoCompleteDialogFragment) fragmentManager
                        .findFragmentByTag("tag");
                if (mOptionDialogFragment != null) {
                    mOptionDialogFragment.setOnOptionSelectedListener(listener);
                }
            }
        }
    }
}
