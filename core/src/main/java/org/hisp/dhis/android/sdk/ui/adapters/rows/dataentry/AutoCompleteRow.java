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

import static android.text.TextUtils.isEmpty;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.Option;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.ui.adapters.rows.events.OnDetailedInfoButtonClick;
import org.hisp.dhis.android.sdk.ui.dialogs.AutoCompleteDialogAdapter;
import org.hisp.dhis.android.sdk.ui.dialogs.AutoCompleteDialogAdapter.OptionAdapterValue;
import org.hisp.dhis.android.sdk.ui.dialogs.AutoCompleteDialogFragment;
import org.hisp.dhis.android.sdk.ui.dialogs.AutoCompleteDialogFragment.OnOptionSelectedListener;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class AutoCompleteRow extends Row {
    private static final String EMPTY_FIELD = "";

    private final Map<String, String> mCodeToNameMap;
    private final Map<String, String> mNameToCodeMap;
    private final ArrayList<String> mOptions;

    public AutoCompleteRow(String label, boolean mandatory, String warning, BaseValue value,
                           OptionSet optionSet, Event event) {
        mEvent = event;
        mLabel = label;
        mValue = value;
        mWarning = warning;
        mMandatory = mandatory;

        mCodeToNameMap = new LinkedHashMap<>();
        mNameToCodeMap = new LinkedHashMap<>();

        if (optionSet.getOptions() != null) {
            for (Option option : optionSet.getOptions()) {
                mCodeToNameMap.put(option.getCode(), option.getName());
                mNameToCodeMap.put(option.getName(), option.getCode());
            }
        }

        mOptions = new ArrayList<>(mNameToCodeMap.keySet());
        checkNeedsForDescriptionButton();

    }

    @Override
    public View getView(FragmentManager fragmentManager, final LayoutInflater inflater,
                        View convertView, ViewGroup container) {
        final View view;
        ViewHolder holder;
        checkNeedsForDescriptionButton();
        if (convertView != null && convertView.getTag() instanceof ViewHolder) {
            view = convertView;
            holder = (ViewHolder) view.getTag();
            holder.clearOnTextChangedListener();
        } else {
            view = inflater.inflate(R.layout.listview_row_autocomplete, container, false);
            detailedInfoButton =  view.findViewById(R.id.detailed_info_button_layout);
            holder = new ViewHolder(view, detailedInfoButton);
            view.setTag(holder);
        }
        holder.textView.setText(mLabel);
        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));
        holder.onTextChangedListener.setBaseValue(mValue);
        holder.onTextChangedListener.setOptions(mNameToCodeMap);
        final TextView autoCompleteTextView = holder.valueTextView;
        holder.clearButton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if (!isMandatory() || !isEventComplete()) {
                autoCompleteTextView.setText(EMPTY_FIELD);
            } else {
                Toast.makeText(inflater.getContext(), inflater.getContext().getString(
                        R.string.error_delete_mandatory_value),
                        Toast.LENGTH_SHORT).show();
            }
        }});
        holder.onDropDownButtonListener.setOptions(mOptions);
        holder.onDropDownButtonListener.setFragmentManager(fragmentManager);

        String name;
        if (mCodeToNameMap.containsKey(mValue.getValue())) {
            name = mCodeToNameMap.get(mValue.getValue());
        } else {
            name = EMPTY_FIELD;
        }

        holder.valueTextView.setText(name);

        if(!isEditable()) {
            holder.valueTextView.setEnabled(false);
            holder.valueTextView.setTextColor(Color.parseColor("#C6C6C6")); //setEnabled(false) won't set disabled text on some devices
            holder.clearButton.setEnabled(false);
        } else {
            holder.valueTextView.setEnabled(true);
            holder.valueTextView.setTextColor(Color.BLACK);
            holder.clearButton.setEnabled(true);
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

        holder.setOnTextChangedListener();

        return view;
    }

    @Override
    public int getViewType() {
        return DataEntryRowTypes.AUTO_COMPLETE.ordinal();
    }


    private static class ViewHolder {
        public final TextView textView;
        public final TextView mandatoryIndicator;
        public final TextView warningLabel;
        public final TextView errorLabel;
        public final TextView valueTextView;
        public final ImageButton clearButton;
        public final View detailedInfoButton;
        public final OnTextChangedListener onTextChangedListener;
        public final DropDownButtonListener onDropDownButtonListener;

        private ViewHolder(View view, View detailedInfoButton) {
            mandatoryIndicator = (TextView) view.findViewById(R.id.mandatory_indicator);
            textView = (TextView) view.findViewById(R.id.text_label);
            warningLabel = (TextView) view.findViewById(R.id.warning_label);
            errorLabel = (TextView) view.findViewById(R.id.error_label);
            valueTextView = (TextView) view.findViewById(R.id.choose_option);
            clearButton = (ImageButton) view.findViewById(R.id.clear_option_value);
            this.detailedInfoButton = detailedInfoButton;

            OnOptionSelectedListener onOptionListener
                    = new OnOptionItemSelectedListener(valueTextView);
            onTextChangedListener = new OnTextChangedListener();
            onDropDownButtonListener = new DropDownButtonListener();
            onDropDownButtonListener.setListener(onOptionListener);
            valueTextView.setOnClickListener(onDropDownButtonListener);
        }

        private void setOnTextChangedListener() {
            valueTextView.addTextChangedListener(onTextChangedListener);
        }

        private void clearOnTextChangedListener() {
            valueTextView.removeTextChangedListener(onTextChangedListener);
        }
    }

    private static class OnOptionItemSelectedListener implements OnOptionSelectedListener {
        private final TextView valueTextView;

        public OnOptionItemSelectedListener(TextView valueTextView) {
            this.valueTextView = valueTextView;
        }

        @Override
        public void onOptionSelected(int dialogId, int position, String id, String name) {
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
                saveValue(newValue);
            }
        }

        private void saveValue(String newValue) {
            value.setValue(newValue);
            Dhis2Application.getEventBus().post(new RowValueChangedEvent(value,DataEntryRowTypes.AUTO_COMPLETE.toString()));
        }
    }

    public static class OptionDialogFragment extends AutoCompleteDialogFragment {
        private static final String EXTRA_OPTIONS = "extra:options";

        public static OptionDialogFragment newInstance(ArrayList<String> options,
                                                       OnOptionSelectedListener listener) {
            OptionDialogFragment dialogFragment = new OptionDialogFragment();
            Bundle args = new Bundle();
            args.putStringArrayList(EXTRA_OPTIONS, options);
            dialogFragment.setArguments(args);
            dialogFragment.setOnOptionSetListener(listener);
            return dialogFragment;
        }

        private List<AutoCompleteDialogAdapter.OptionAdapterValue> getOptions() {
            List<AutoCompleteDialogAdapter.OptionAdapterValue> values = new ArrayList<>();
            List<String> options = getArguments().getStringArrayList(EXTRA_OPTIONS);
            if (options != null && !options.isEmpty()) {
                for (String option : options) {
                    values.add(new OptionAdapterValue(option, option));
                }
            }
            return values;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            setDialogLabel(R.string.find_option);
            getAdapter().swapData(getOptions());
        }
    }
}