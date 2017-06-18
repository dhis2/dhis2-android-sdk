package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow;

import android.text.Editable;

import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.Option;
import org.hisp.dhis.android.sdk.persistence.models.Option$Table;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRowTypes;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

import static android.text.TextUtils.isEmpty;

public class AutoCompleteOnTextChangedListener extends AbsTextWatcher {
    private BaseValue value;
    private OptionNameCacher optionNameCacher;
    private String optionSetId;

    public void setBaseValue(BaseValue value) {
        this.value = value;
    }

    public void setOptionSetId(String optionSetId) {
        this.optionSetId = optionSetId;
    }

    public void setCachedOptionNameClearer(OptionNameCacher optionNameCacher) {
        this.optionNameCacher = optionNameCacher;
    }

    @Override
    public void afterTextChanged(Editable s) {
        String name = s != null ? s.toString() : AutoCompleteRow.EMPTY_FIELD;
        String newValue;
        String previousValue = value.getValue();
        if(AutoCompleteRow.EMPTY_FIELD.equals(name)) {
            newValue = AutoCompleteRow.EMPTY_FIELD;
        } else {
            Option option = new Select().from(Option.class).
                    where(Condition.column(Option$Table.NAME).is(name)).
                    and(Condition.column(Option$Table.OPTIONSET).is(optionSetId)).querySingle();
            if(option == null) {
                newValue = AutoCompleteRow.EMPTY_FIELD;
            } else {
                newValue = option.getCode();
            }
        }
        if (isEmpty(newValue)) {
            newValue = AutoCompleteRow.EMPTY_FIELD;
        }

        if (!newValue.equals(previousValue)) {
            value.setValue(newValue);
            optionNameCacher.cacheOptionName();

            Dhis2Application.getEventBus()
                    .post(new RowValueChangedEvent(value, DataEntryRowTypes.OPTION_SET.toString()));
        }
    }
}
