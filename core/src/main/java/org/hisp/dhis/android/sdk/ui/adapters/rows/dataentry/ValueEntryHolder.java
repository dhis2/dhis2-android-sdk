package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;


import static org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.AbsDatePickerRow.EMPTY_FIELD;

import android.text.Editable;
import android.widget.EditText;
import android.widget.TextView;

import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.ui.adapters.rows.AbsTextWatcher;
import org.hisp.dhis.android.sdk.ui.fragments.dataentry.RowValueChangedEvent;

public class ValueEntryHolder {
    final TextView textLabel;
    final TextView mandatoryIndicator;
    final TextView warningLabel;
    final TextView errorLabel;
    final EditText editText;
    //        final View detailedInfoButton;
    final OnTextChangeListener listener;

    public ValueEntryHolder(TextView textLabel,
            TextView mandatoryIndicator, TextView warningLabel,
            TextView errorLabel, EditText editText,
            OnTextChangeListener listener) {
        this.textLabel = textLabel;
        this.mandatoryIndicator = mandatoryIndicator;
        this.warningLabel = warningLabel;
        this.errorLabel = errorLabel;
        this.editText = editText;
//            this.detailedInfoButton = detailedInfoButton;
        this.listener = listener;
    }

}


class OnTextChangeListener extends AbsTextWatcher {
    private BaseValue value;
    RunProgramRulesDelayedDispatcher runProgramRulesDelayedDispatcher = new RunProgramRulesDelayedDispatcher();
    Row row;
    String rowType;
    public void setRowType(String type){
        rowType = type;
    }
    public void setRow(Row row) {
        this.row = row;
    }

    public void setBaseValue(BaseValue value) {
        this.value = value;
    }

    public void onRowReused() {
        if (runProgramRulesDelayedDispatcher != null) {
            runProgramRulesDelayedDispatcher.dispatchNow();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        String newValue = s != null ? s.toString() : EMPTY_FIELD;
        if (!newValue.equals(value.getValue())) {
            value.setValue(newValue);
            RowValueChangedEvent rowValueChangeEvent = new RowValueChangedEvent(value, rowType);
            rowValueChangeEvent.setRow(row);
            Dhis2Application.getEventBus().post(rowValueChangeEvent);
            runProgramRulesDelayedDispatcher.dispatchDelayed(new RunProgramRulesEvent(value));
        }
    }


}
