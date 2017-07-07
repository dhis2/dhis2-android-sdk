package org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry;

import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.persistence.models.BaseValue;
import org.hisp.dhis.android.sdk.persistence.models.Option;
import org.hisp.dhis.android.sdk.persistence.models.OptionSet;
import org.hisp.dhis.android.sdk.ui.adapters.rows.dataentry.autocompleterow.AutoCompleteRow;
import org.hisp.dhis.android.sdk.utils.api.ValueType;

import java.util.List;

/**
 * Created by katana on 21/10/16.
 */

public class DataEntryRowFactory {
    public static Row createDataEntryView(boolean mandatory, boolean allowFutureDate,
                                          String optionSetId, String rowName, BaseValue baseValue,
                                          ValueType valueType, boolean editable,
                                          boolean shouldNeverBeEdited, boolean dataEntryMethod ) {
        Row row;
        String trackedEntityAttributeName = rowName;
        if (optionSetId != null) {
            OptionSet optionSet = MetaDataController.getOptionSet(optionSetId);
            if (optionSet == null) {
                row = new ShortTextEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.TEXT);
            } else {
                List<Option> options = MetaDataController.getOptions(optionSetId);

                if (isDataEntryRadioButtons(dataEntryMethod, options)) {
                    row = new RadioButtonsOptionSetRow(trackedEntityAttributeName, mandatory, null,
                            baseValue, options);
                }
                else
                    row = new AutoCompleteRow(trackedEntityAttributeName, mandatory, null, baseValue, optionSet);
            }
        } else if (valueType.equals(ValueType.TEXT)) {
            row = new ShortTextEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.TEXT);
        } else if (valueType.equals(ValueType.LONG_TEXT)) {
            row = new LongEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.LONG_TEXT);
        } else if (valueType.equals(ValueType.NUMBER)) {
            row = new NumberEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.NUMBER);
        } else if (valueType.equals(ValueType.INTEGER)) {
            row = new IntegerEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.INTEGER);
        } else if (valueType.equals(ValueType.INTEGER_ZERO_OR_POSITIVE)) {
            row = new IntegerZeroOrPositiveEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.INTEGER_ZERO_OR_POSITIVE);
        } else if (valueType.equals(ValueType.PERCENTAGE)) {
            row = new PercentageEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.PERCENTAGE);
        } else if (valueType.equals(ValueType.INTEGER_POSITIVE)) {
            row = new IntegerPositiveEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.INTEGER_POSITIVE);
        } else if (valueType.equals(ValueType.INTEGER_NEGATIVE)) {
            row = new IntegerNegativeEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.INTEGER_NEGATIVE);
        } else if (valueType.equals(ValueType.BOOLEAN)) {
            row = new RadioButtonsRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.BOOLEAN);
        } else if (valueType.equals(ValueType.TRUE_ONLY)) {
            row = new CheckBoxRow(trackedEntityAttributeName, mandatory, null, baseValue);
        } else if (valueType.equals(ValueType.DATE) || valueType.equals(ValueType.AGE)) {
            row = new DatePickerRow(trackedEntityAttributeName, mandatory, null, baseValue, allowFutureDate);
        } else if(valueType.equals(ValueType.COORDINATE)) {
            row = new QuestionCoordinatesRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.QUESTION_COORDINATES);
        } else  if(valueType.equals(ValueType.PHONE_NUMBER)) {
            row = new PhoneEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.PHONE_NUMBER);
        }  else  if(valueType.equals(ValueType.EMAIL)) {
            row = new EmailAddressEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue, DataEntryRowTypes.EMAIL);
        } else {
            row = new InvalidEditTextRow(trackedEntityAttributeName, mandatory, null, baseValue,
                    DataEntryRowTypes.INVALID_DATA_ENTRY);
        }
        row.setEditable(editable);
        row.setShouldNeverBeEdited(shouldNeverBeEdited);
        return row;
    }

    private static boolean isDataEntryRadioButtons(boolean dataEntryMethod, List<Option> options) {
        return dataEntryMethod && options.size() < 8;
    }

}
