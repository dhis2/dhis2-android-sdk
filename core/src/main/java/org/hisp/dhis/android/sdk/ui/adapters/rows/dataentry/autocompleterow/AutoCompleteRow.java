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

package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow;

import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.R;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.Option;
import org.hisp.dhis.android.sdk.persistence.models.Option$Table;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRowTypes;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.Row;

public final class AutoCompleteRow extends Row implements OptionNameCacher {
    static final String EMPTY_FIELD = "";

    private final String mOptionSetId;

    /**
     * Caches option name to display to avoid heavy lookups in cases of high number of
     * option sets
     */
    private String mSelectedOptionName;

    public AutoCompleteRow(String label, boolean mandatory, String warning, BaseValue value,
                           OptionSet optionSet) {
        mLabel = label;
        mValue = value;
        mWarning = warning;
        mMandatory = mandatory;
        mOptionSetId = optionSet.getUid();
        cacheOptionName();
//        checkNeedsForDescriptionButton();
    }

    @Override
    public View getView(FragmentManager fragmentManager, LayoutInflater inflater,
                        View convertView, ViewGroup container) {
        View view;
        AutoCompleteRowViewHolder holder;
//        checkNeedsForDescriptionButton();
        if (convertView != null && convertView.getTag() instanceof AutoCompleteRowViewHolder) {
            view = convertView;
            holder = (AutoCompleteRowViewHolder) view.getTag();
            holder.clearOnTextChangedListener();
        } else {
            view = inflater.inflate(R.layout.listview_row_autocomplete, container, false);
//            detailedInfoButton =  view.findViewById(R.id.detailed_info_button_layout);
            holder = new AutoCompleteRowViewHolder(view);
            view.setTag(holder);
        }
        holder.textView.setText(mLabel);
//        holder.detailedInfoButton.setOnClickListener(new OnDetailedInfoButtonClick(this));
        holder.onTextChangedListener.setBaseValue(mValue);
        holder.onTextChangedListener.setOptionSetId(mOptionSetId);
        holder.onTextChangedListener.setCachedOptionNameClearer(this);

        holder.onDropDownButtonListener.setOptionSetId(mOptionSetId);
        holder.onDropDownButtonListener.setFragmentManager(fragmentManager);
        holder.onDropDownButtonListener.getListener().setValue(mValue);

        holder.onClearButtonListener.setValue(mValue);
        holder.onClearButtonListener.setOptionNameCacher(this);

        holder.valueTextView.setText(mSelectedOptionName);

        holder.setOnTextChangedListener();

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
//            holder.detailedInfoButton.setVisibility(View.INVISIBLE);
        }
        else {
//            holder.detailedInfoButton.setVisibility(View.VISIBLE);
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
        return DataEntryRowTypes.OPTION_SET.ordinal();
    }

    public void cacheOptionName() {
        String optionSetCode = mValue.getValue();
        if(optionSetCode == null || EMPTY_FIELD.equals(optionSetCode)) {
            mSelectedOptionName = EMPTY_FIELD;
        } else {
            Option option = new Select().from(Option.class).
                    where(Condition.column(Option$Table.CODE).is(optionSetCode)).
                    and(Condition.column(Option$Table.OPTIONSET).is(mOptionSetId)).querySingle();
            if(option != null) {
                mSelectedOptionName = option.getName();
            }
            if(mSelectedOptionName == null) {
                mSelectedOptionName = EMPTY_FIELD;
            }
        }
    }

    @Override
    public void clearCachedOptionName() {
        mSelectedOptionName = EMPTY_FIELD;
    }
}