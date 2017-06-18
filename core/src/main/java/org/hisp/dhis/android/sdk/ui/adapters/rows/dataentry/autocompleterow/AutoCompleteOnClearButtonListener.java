package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow;

import android.view.View;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRowTypes;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

class AutoCompleteOnClearButtonListener implements View.OnClickListener {
    private final TextView textView;
    private BaseValue value;
    private OptionNameCacher optionNameCacher;

    public AutoCompleteOnClearButtonListener(TextView textView) {
        this.textView = textView;
    }

    public void setValue(BaseValue value) {
        this.value = value;
    }

    public void setOptionNameCacher(OptionNameCacher optionNameCacher) {
        this.optionNameCacher = optionNameCacher;
    }

    @Override
    public void onClick(View v) {
        value.setValue(AutoCompleteRow.EMPTY_FIELD);
        optionNameCacher.clearCachedOptionName();
        textView.setText(AutoCompleteRow.EMPTY_FIELD);
        Dhis2Application.getEventBus()
                .post(new RowValueChangedEvent(value, DataEntryRowTypes.OPTION_SET.toString()));
    }
}
