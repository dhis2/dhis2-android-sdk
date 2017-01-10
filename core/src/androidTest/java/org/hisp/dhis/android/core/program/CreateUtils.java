package org.hisp.dhis.android.core.program;

import android.content.ContentValues;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeSearchScope;

import java.text.ParseException;

import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModelIntegrationTests.getBooleanFromInteger;

public class CreateUtils {
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
     *  Properties bound to Program
     */

    private static final Integer VERSION = 1;
    private static final Boolean ONLY_ENROLL_ONCE = true;
    private static final String ENROLLMENT_DATE_LABEL = "enrollment date";
    private static final Boolean DISPLAY_INCIDENT_DATE = true;
    private static final String INCIDENT_DATE_LABEL = "incident date label";
    private static final Boolean REGISTRATION = true;
    private static final Boolean SELECT_ENROLLMENT_DATES_IN_FUTURE = true;
    private static final Boolean DATA_ENTRY_METHOD = true;
    private static final Boolean IGNORE_OVERDUE_EVENTS = false;
    private static final Boolean RELATIONSHIP_FROM_A = true;
    private static final Boolean SELECT_INCIDENT_DATES_IN_FUTURE = true;
    private static final Boolean CAPTURE_COORDINATES = true;
    private static final Boolean USE_FIRST_STAGE_DURING_REGISTRATION = true;
    private static final Boolean DISPLAY_FRONT_PAGE_LIST = true;
    private static final ProgramType PROGRAM_TYPE = ProgramType.WITH_REGISTRATION;
    private static final String RELATIONSHIP_TYPE = "relationshipUid";
    private static final String RELATIONSHIP_TEXT = "test relationship";
    private static final String RELATED_PROGRAM = "RelatedProgramUid";

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

    /**
     * Properties bound to ProgramRuleVariable
     */

    private static final String PROGRAM_STAGE = "test_programStage";
    private static final ProgramRuleVariableSourceType PROGRAM_RULE_VARIABLE_SOURCE_TYPE =
            ProgramRuleVariableSourceType.CALCULATED_VALUE;

    private static final Integer USE_CODE_FOR_OPTION_SET = 1; // true
    private static final String PROGRAM = "test_program";
    private static final String DATA_ELEMENT = "test_dataElement";
    private static final String TRACKED_ENTITY_ATTRIBUTE = "test_trackedEntityAttribute";



    public static ContentValues createTrackedEntityAttributeWithOptionSet(long id, String uid, String optionSetUid) throws ParseException {
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

    public static ContentValues createTrackedEntityAttributeWithoutOptionSet(long id, String uid) throws ParseException {
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

    public static ContentValues createProgramRuleVariable(long id, String uid) {
        ContentValues programRuleVariable = new ContentValues();
        programRuleVariable.put(ProgramRuleVariableContract.Columns.ID, id);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.UID, uid);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.CODE, CODE);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.NAME, NAME);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.DISPLAY_NAME, DISPLAY_NAME);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.CREATED, DATE);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.LAST_UPDATED, DATE);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.USE_CODE_FOR_OPTION_SET, USE_CODE_FOR_OPTION_SET);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.PROGRAM, PROGRAM);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.PROGRAM_STAGE, PROGRAM_STAGE);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.TRACKED_ENTITY_ATTRIBUTE, TRACKED_ENTITY_ATTRIBUTE);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.DATA_ELEMENT, DATA_ELEMENT);
        programRuleVariable.put(ProgramRuleVariableContract.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE, PROGRAM_RULE_VARIABLE_SOURCE_TYPE.name());
        return programRuleVariable;
    }

    /**
     * A method to createTrackedEntityAttribute ContentValues for a Program.
     * To be used by other tests.
     *
     * @param id
     * @param uid
     * @return
     */
    public static ContentValues createProgram(long id, String uid) {
        ContentValues program = new ContentValues();
        program.put(ProgramContract.Columns.ID, id);
        program.put(ProgramContract.Columns.UID, uid);
        program.put(ProgramContract.Columns.CODE, CODE);
        program.put(ProgramContract.Columns.NAME, NAME);
        program.put(ProgramContract.Columns.DISPLAY_NAME, DISPLAY_NAME);
        program.put(ProgramContract.Columns.CREATED, DATE);
        program.put(ProgramContract.Columns.LAST_UPDATED, DATE);
        program.put(ProgramContract.Columns.SHORT_NAME, SHORT_NAME);
        program.put(ProgramContract.Columns.DISPLAY_SHORT_NAME, DISPLAY_SHORT_NAME);
        program.put(ProgramContract.Columns.DESCRIPTION, DESCRIPTION);
        program.put(ProgramContract.Columns.DISPLAY_DESCRIPTION, DISPLAY_DESCRIPTION);
        program.put(ProgramContract.Columns.VERSION, VERSION);
        program.put(ProgramContract.Columns.ONLY_ENROLL_ONCE, ONLY_ENROLL_ONCE);
        program.put(ProgramContract.Columns.ENROLLMENT_DATE_LABEL, ENROLLMENT_DATE_LABEL);
        program.put(ProgramContract.Columns.DISPLAY_INCIDENT_DATE, DISPLAY_INCIDENT_DATE);
        program.put(ProgramContract.Columns.INCIDENT_DATE_LABEL, INCIDENT_DATE_LABEL);
        program.put(ProgramContract.Columns.REGISTRATION, REGISTRATION);
        program.put(ProgramContract.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE, SELECT_ENROLLMENT_DATES_IN_FUTURE);
        program.put(ProgramContract.Columns.DATA_ENTRY_METHOD, DATA_ENTRY_METHOD);
        program.put(ProgramContract.Columns.IGNORE_OVERDUE_EVENTS, IGNORE_OVERDUE_EVENTS);
        program.put(ProgramContract.Columns.RELATIONSHIP_FROM_A, RELATIONSHIP_FROM_A);
        program.put(ProgramContract.Columns.SELECT_INCIDENT_DATES_IN_FUTURE, SELECT_INCIDENT_DATES_IN_FUTURE);
        program.put(ProgramContract.Columns.CAPTURE_COORDINATES, CAPTURE_COORDINATES);
        program.put(ProgramContract.Columns.USE_FIRST_STAGE_DURING_REGISTRATION, USE_FIRST_STAGE_DURING_REGISTRATION);
        program.put(ProgramContract.Columns.DISPLAY_FRONT_PAGE_LIST, DISPLAY_FRONT_PAGE_LIST);
        program.put(ProgramContract.Columns.PROGRAM_TYPE, PROGRAM_TYPE.name());
        program.put(ProgramContract.Columns.RELATIONSHIP_TYPE, RELATIONSHIP_TYPE);
        program.put(ProgramContract.Columns.RELATIONSHIP_TEXT, RELATIONSHIP_TEXT);
        program.put(ProgramContract.Columns.RELATED_PROGRAM, RELATED_PROGRAM);

        return program;
    }
}
