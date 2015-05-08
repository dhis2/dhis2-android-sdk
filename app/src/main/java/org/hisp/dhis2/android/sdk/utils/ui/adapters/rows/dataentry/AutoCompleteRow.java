/*
 * Copyright (c) 2015, dhis2
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis2.android.sdk.utils.ui.adapters.rows.dataentry;

import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hisp.dhis2.android.sdk.R;
import org.hisp.dhis2.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis2.android.sdk.utils.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis2.android.sdk.fragments.dataentry.EditTextValueChangedEvent;
import org.hisp.dhis2.android.sdk.utils.ui.adapters.rows.dataentry.OptionDialogFragment.OnOptionSelectedListener;
import org.hisp.dhis2.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis2.android.sdk.persistence.models.Option;
import org.hisp.dhis2.android.sdk.persistence.models.OptionSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static android.text.TextUtils.isEmpty;

public final class AutoCompleteRow implements DataEntryRow {
    private static final String EMPTY_FIELD = "";

    private final String mLabel;
    private final BaseValue mValue;

    private final Map<String, String> mCodeToNameMap;
    private final Map<String, String> mNameToCodeMap;
    private final ArrayList<String> mOptions;

    private boolean hidden = false;

    public AutoCompleteRow(String label, BaseValue value,
                           OptionSet optionSet) {
        mLabel = label;
        mValue = value;

        mCodeToNameMap = new LinkedHashMap<>();
        mNameToCodeMap = new LinkedHashMap<>();

        if (optionSet.getOptions() != null) {
            for (Option option : optionSet.getOptions()) {
                mCodeToNameMap.put(option.getCode(), option.getName());
                mNameToCodeMap.put(option.getName(), option.getCode());
            }
        }

        mOptions = new ArrayList<>(mNameToCodeMap.keySet());
    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater,
                        View convertView, ViewGroup container) {
        View view;
        ViewHolder holder;

        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        } else {
            view = inflater.inflate(R.layout.listview_row_autocomplete, container, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        holder.textView.setText(mLabel);

        holder.onTextChangedListener.setBaseValue(mValue);
        holder.onTextChangedListener.setOptions(mNameToCodeMap);

        holder.onDropDownButtonListener.setOptions(mOptions);
        holder.onDropDownButtonListener.setFragmentManager(fragmentManager);

        String name;
        if (mCodeToNameMap.containsKey(mValue.getValue())) {
            name = mCodeToNameMap.get(mValue.getValue());
        } else {
            name = EMPTY_FIELD;
        }

        holder.valueTextView.setText(name);
        holder.valueTextView.clearFocus();

        return view;
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.AUTO_COMPLETE.ordinal();
    }

    @Override
    public BaseValue getBaseValue() {
        return mValue;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    private static class ViewHolder {
        public final TextView textView;
        public final TextView valueTextView;
        public final ImageButton imageButton;
        public final OnTextChangedListener onTextChangedListener;
        public final DropDownButtonListener onDropDownButtonListener;

        private ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.text_label);
            valueTextView = (TextView) view.findViewById(R.id.choose_option);
            imageButton = (ImageButton) view.findViewById(R.id.show_drop_down_list);

            OnOptionSelectedListener onOptionListener
                    = new OnOptionItemSelectedListener(valueTextView);
            onTextChangedListener = new OnTextChangedListener();
            onDropDownButtonListener = new DropDownButtonListener();
            onDropDownButtonListener.setListener(onOptionListener);

            imageButton.setOnClickListener(onDropDownButtonListener);
            valueTextView.addTextChangedListener(onTextChangedListener);
        }
    }

    private static class OnOptionItemSelectedListener
            implements OnOptionSelectedListener {
        private final TextView valueTextView;

        public OnOptionItemSelectedListener(TextView valueTextView) {
            this.valueTextView = valueTextView;
        }

        @Override
        public void onOptionSelected(int position, String name) {
            valueTextView.setText(name);
        }
    }

    private static class DropDownButtonListener implements View.OnClickListener {
        private FragmentManager fragmentManager;
        private ArrayList<String> options;
        private OnOptionSelectedListener listener;

        public void setFragmentManager(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
        }

        public void setOptions(ArrayList<String> options) {
            this.options = options;
        }

        public void setListener(OnOptionSelectedListener listener) {
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            OptionDialogFragment fragment =
                    OptionDialogFragment.newInstance(options, listener);
            fragment.show(fragmentManager);
        }
    }

    private static class OnTextChangedListener extends AbsTextWatcher {
        private BaseValue value;
        private Map<String, String> nameToCodeMap;

        public void setBaseValue(BaseValue value) {
            this.value = value;
        }

        public void setOptions(Map<String, String> nameToCodeMap) {
            this.nameToCodeMap = nameToCodeMap;
        }

        @Override
        public void afterTextChanged(Editable s) {
            String name = s != null ? s.toString() : EMPTY_FIELD;
            String newValue = nameToCodeMap.get(name);
            if (isEmpty(newValue)) {
                newValue = EMPTY_FIELD;
            }

            if (!newValue.equals(value.getValue())) {
                value.setValue(newValue);
                Dhis2Application.getEventBus()
                        .post(new EditTextValueChangedEvent(value));
            }
        }
    }
}