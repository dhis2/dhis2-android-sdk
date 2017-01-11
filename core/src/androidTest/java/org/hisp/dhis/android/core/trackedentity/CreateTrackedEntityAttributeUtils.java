package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;

import java.text.ParseException;

import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModelIntegrationTests.getBooleanFromInteger;

public class CreateTrackedEntityAttributeUtils {

    /**
     * BaseIdentifiable propertites
     */
    private static final String CODE = "test_code";
    private static final String NAME = "test_name";
    private static final String DISPLAY_NAME = "test_display_name";
    private static final String DATE = "2011-12-24T12:24:25.203";

    /**
     * BaseNameableProperties
     */
    private static final String SHORT_NAME = "test_short_name";
    private static final String DISPLAY_SHORT_NAME = "test_display_short_name";
    private static final String DESCRIPTION = "test_description";
    private static final String DISPLAY_DESCRIPTION = "test_display_description";

    /**
     * Properties bound to TrackedEntityAttribute
     */
    private static final String PATTERN = "test_pattern";
    private static final Integer SORT_ORDER_IN_LIST_NO_PROGRAM = 1;
    private static final ValueType VALUE_TYPE = ValueType.BOOLEAN;
    private static final String EXPRESSION = "test_expression";
    private static final TrackedEntityAttributeSearchScope SEARCH_SCOPE = TrackedEntityAttributeSearchScope.SEARCH_ORG_UNITS;
    private static final Integer PROGRAM_SCOPE = 0; // false
    private static final Integer DISPLAY_IN_LIST_NO_PROGRAM = 1; // true
    private static final Integer GENERATED = 0; // false
    private static final Integer DISPLAY_ON_VISIT_SCHEDULE = 1; // true
    private static final Integer ORG_UNIT_SCOPE = 0; // false
    private static final Integer UNIQUE = 1; // true
    private static final Integer INHERIT = 0; // false


    public static ContentValues createWithOptionSet(long id, String uid, String optionSetUid) throws ParseException {
        TrackedEntityAttributeModel trackedEntityAttributeModel = TrackedEntityAttributeModel.builder()
                .id(id)
                .uid(uid)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(BaseIdentifiableObject.DATE_FORMAT.parse(DATE))
                .lastUpdated(BaseIdentifiableObject.DATE_FORMAT.parse(DATE))
                .shortName(SHORT_NAME)
                .displayShortName(DISPLAY_SHORT_NAME)
                .description(DESCRIPTION)
                .displayDescription(DISPLAY_DESCRIPTION)
                .pattern(PATTERN)
                .sortOrderInListNoProgram(SORT_ORDER_IN_LIST_NO_PROGRAM)
                .optionSet(optionSetUid)
                .valueType(VALUE_TYPE)
                .expression(EXPRESSION)
                .searchScope(SEARCH_SCOPE)
                .programScope(getBooleanFromInteger(PROGRAM_SCOPE))
                .displayInListNoProgram(getBooleanFromInteger(DISPLAY_IN_LIST_NO_PROGRAM))
                .generated(getBooleanFromInteger(GENERATED))
                .displayOnVisitSchedule(getBooleanFromInteger(DISPLAY_ON_VISIT_SCHEDULE))
                .orgUnitScope(getBooleanFromInteger(ORG_UNIT_SCOPE))
                .unique(getBooleanFromInteger(UNIQUE))
                .inherit(getBooleanFromInteger(INHERIT))
                .build();
        return trackedEntityAttributeModel.toContentValues();
    }

    public static ContentValues createWithoutOptionSet(long id, String uid) throws ParseException {
        TrackedEntityAttributeModel trackedEntityAttributeModel = TrackedEntityAttributeModel.builder()
                .id(id)
                .uid(uid)
                .code(CODE)
                .name(NAME)
                .displayName(DISPLAY_NAME)
                .created(BaseIdentifiableObject.DATE_FORMAT.parse(DATE))
                .lastUpdated(BaseIdentifiableObject.DATE_FORMAT.parse(DATE))
                .shortName(SHORT_NAME)
                .displayShortName(DISPLAY_SHORT_NAME)
                .description(DESCRIPTION)
                .displayDescription(DISPLAY_DESCRIPTION)
                .pattern(PATTERN)
                .sortOrderInListNoProgram(SORT_ORDER_IN_LIST_NO_PROGRAM)
                .optionSet(null)
                .valueType(VALUE_TYPE)
                .expression(EXPRESSION)
                .searchScope(SEARCH_SCOPE)
                .programScope(getBooleanFromInteger(PROGRAM_SCOPE))
                .displayInListNoProgram(getBooleanFromInteger(DISPLAY_IN_LIST_NO_PROGRAM))
                .generated(getBooleanFromInteger(GENERATED))
                .displayOnVisitSchedule(getBooleanFromInteger(DISPLAY_ON_VISIT_SCHEDULE))
                .orgUnitScope(getBooleanFromInteger(ORG_UNIT_SCOPE))
                .unique(getBooleanFromInteger(UNIQUE))
                .inherit(getBooleanFromInteger(INHERIT))
                .build();
        return trackedEntityAttributeModel.toContentValues();
    }
}
