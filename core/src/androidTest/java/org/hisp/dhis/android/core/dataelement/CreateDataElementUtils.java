package org.hisp.dhis.android.core.dataelement;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.ValueType;

public class CreateDataElementUtils {
    private static final long ID = 2L;
    private static final String UID = "test_uid";
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";
    private static final ValueType VALUE_TYPE = ValueType.TEXT;
    private static final Integer ZERO_IS_SIGNIFICANT = 0;
    private static final String AGGREGATION_OPERATOR = "test_aggregationOperator";
    private static final String FORM_NAME = "test_formName";
    private static final String NUMBER_TYPE = "test_numberType";
    private static final String DOMAIN_TYPE = "test_domainType";
    private static final String DIMENSION = "test_dimension";
    private static final String DISPLAY_FORM_NAME = "test_displayFormName";

    // timestamp
    private static final String DATE = "2014-03-20T13:37:00.007";

    public static ContentValues create(long id, String uid, String optionSetId) {
        ContentValues dataElement = new ContentValues();
        dataElement.put(DataElementModel.Columns.ID, id);
        dataElement.put(DataElementModel.Columns.UID, uid);
        dataElement.put(DataElementModel.Columns.CODE, CODE);
        dataElement.put(DataElementModel.Columns.NAME, NAME);
        dataElement.put(DataElementModel.Columns.DISPLAY_NAME, DISPLAY_NAME);
        dataElement.put(DataElementModel.Columns.CREATED, DATE);
        dataElement.put(DataElementModel.Columns.LAST_UPDATED, DATE);
        dataElement.put(DataElementModel.Columns.SHORT_NAME, SHORT_NAME);
        dataElement.put(DataElementModel.Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        dataElement.put(DataElementModel.Columns.DESCRIPTION, DESCRIPTION);
        dataElement.put(DataElementModel.Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        dataElement.put(DataElementModel.Columns.VALUE_TYPE, VALUE_TYPE.name());
        dataElement.put(DataElementModel.Columns.ZERO_IS_SIGNIFICANT, ZERO_IS_SIGNIFICANT);
        dataElement.put(DataElementModel.Columns.AGGREGATION_TYPE, AGGREGATION_OPERATOR);
        dataElement.put(DataElementModel.Columns.FORM_NAME, FORM_NAME);
        dataElement.put(DataElementModel.Columns.NUMBER_TYPE, NUMBER_TYPE);
        dataElement.put(DataElementModel.Columns.DOMAIN_TYPE, DOMAIN_TYPE);
        dataElement.put(DataElementModel.Columns.DIMENSION, DIMENSION);
        dataElement.put(DataElementModel.Columns.DISPLAY_FORM_NAME, DISPLAY_FORM_NAME);
        if (optionSetId == null) {
            dataElement.putNull(DataElementModel.Columns.OPTION_SET);
        } else {
            dataElement.put(DataElementModel.Columns.OPTION_SET, optionSetId);
        }

        return dataElement;
    }
}
