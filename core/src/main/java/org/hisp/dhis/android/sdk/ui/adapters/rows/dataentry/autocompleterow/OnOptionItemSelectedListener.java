package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow;

import android.widget.TextView;

import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.DataEntryRowTypes;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

class OnOptionItemSelectedListener implements AutoCompleteOnOptionSelectedListener {
    private final TextView valueTextView;
    private BaseValue value;

    public OnOptionItemSelectedListener(TextView valueTextView) {
        this.valueTextView = valueTextView;
    }

    public void setValue(BaseValue value) {
        this.value = value;
    }

    @Override
    public void onOptionSelected(int dialogId, int position, String id, String name) {
        valueTextView.setText(name);
        Dhis2Application.getEventBus()
                .post(new RowValueChangedEvent(value, DataEntryRowTypes.OPTION_SET.toString()));
    }
}
