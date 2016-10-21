package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis.android.sdk.persistence.models.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttribute;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow.AutoCompleteRow;
import org.hisp.dhis.android.sdk.utils.api.ValueType;

/**
 * Created by katana on 21/10/16.
 */

public class DataEntryRowFactory {
    public static Row createDataEntryView(boolean mandatory, boolean allowFutureDate,
                                          String optionSetId, String rowName, BaseValue baseValue,
                                          ValueType valueType, boolean editable, boolean shouldNeverBeEdited) {
        Row row;
        String trackedEntityAttributeName = rowName;
        if (optionSetId != null) {
            OptionSet optionSet = MetaDataController.getOptionSet(optionSetId);
            if (optionSet == null) {
                row = new EditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.TEXT);
            } else {
                row = new AutoCompleteRow(trackedEntityAttributeName, mandatory, null, baseValue, optionSet);
            }
        } else if (valueType.equals(ValueType.TEXT)) {
            row = new EditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.TEXT);
        } else if (valueType.equals(ValueType.LONG_TEXT)) {
            row = new EditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.LONG_TEXT);
        } else if (valueType.equals(ValueType.NUMBER)) {
            row = new EditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.NUMBER);
        } else if (valueType.equals(ValueType.INTEGER)) {
            row = new EditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.INTEGER);
        } else if (valueType.equals(ValueType.INTEGER_ZERO_OR_POSITIVE)) {
            row = new EditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.INTEGER_ZERO_OR_POSITIVE);
        } else if (valueType.equals(ValueType.INTEGER_POSITIVE)) {
            row = new EditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.INTEGER_POSITIVE);
        } else if (valueType.equals(ValueType.INTEGER_NEGATIVE)) {
            row = new EditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.INTEGER_NEGATIVE);
        } else if (valueType.equals(ValueType.BOOLEAN)) {
            row = new RadioButtonsRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.BOOLEAN);
        } else if (valueType.equals(ValueType.TRUE_ONLY)) {
            row = new CheckBoxRow(trackedEntityAttributeName, mandatory, null, baseValue);
        } else if (valueType.equals(ValueType.DATE)) {
            row = new DatePickerRow(trackedEntityAttributeName, mandatory, null, baseValue, allowFutureDate);
        } else if(valueType.equals(ValueType.COORDINATE)) {
            row = new DataValueCoordinatesRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.DATAVALUECOORDINATES);
        } else {
            row = new EditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.LONG_TEXT);
        }
        row.setEditable(editable);
        row.setShouldNeverBeEdited(shouldNeverBeEdited);
        return row;
    }
}
