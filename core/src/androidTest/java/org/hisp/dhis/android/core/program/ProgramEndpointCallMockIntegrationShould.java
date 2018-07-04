/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.category.CreateCategoryComboUtils;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.AssetsFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityTypeModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramEndpointCallMockIntegrationShould extends AbsStoreTestCase {
    private static String[] PROGRAM_PROJECTION = {
            ProgramModel.Columns.UID,
            ProgramModel.Columns.CODE,
            ProgramModel.Columns.NAME,
            ProgramModel.Columns.DISPLAY_NAME,
            ProgramModel.Columns.CREATED,
            ProgramModel.Columns.LAST_UPDATED,
            ProgramModel.Columns.SHORT_NAME,
            ProgramModel.Columns.DISPLAY_SHORT_NAME,
            ProgramModel.Columns.DESCRIPTION,
            ProgramModel.Columns.DISPLAY_DESCRIPTION,
            ProgramModel.Columns.VERSION,
            ProgramModel.Columns.ONLY_ENROLL_ONCE,
            ProgramModel.Columns.ENROLLMENT_DATE_LABEL,
            ProgramModel.Columns.DISPLAY_INCIDENT_DATE,
            ProgramModel.Columns.INCIDENT_DATE_LABEL,
            ProgramModel.Columns.REGISTRATION,
            ProgramModel.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE,
            ProgramModel.Columns.DATA_ENTRY_METHOD,
            ProgramModel.Columns.IGNORE_OVERDUE_EVENTS,
            ProgramModel.Columns.RELATIONSHIP_FROM_A,
            ProgramModel.Columns.SELECT_INCIDENT_DATES_IN_FUTURE,
            ProgramModel.Columns.CAPTURE_COORDINATES,
            ProgramModel.Columns.USE_FIRST_STAGE_DURING_REGISTRATION,
            ProgramModel.Columns.DISPLAY_FRONT_PAGE_LIST,
            ProgramModel.Columns.PROGRAM_TYPE,
            ProgramModel.Columns.RELATIONSHIP_TYPE,
            ProgramModel.Columns.RELATIONSHIP_TEXT,
            ProgramModel.Columns.RELATED_PROGRAM,
            ProgramModel.Columns.TRACKED_ENTITY_TYPE,
            ProgramModel.Columns.CATEGORY_COMBO,
            ProgramModel.Columns.ACCESS_DATA_WRITE
    };

    private Dhis2MockServer dhis2MockServer;
    private Call<List<Program>> programEndpointCall;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new AssetsFileReader());

        D2 d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        dhis2MockServer.enqueueMockResponse("programs_complete.json");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        ContentValues categoryCombo = CreateCategoryComboUtils.create(1L, "nM3u9s5a52V");
        database().insert(CategoryComboModel.TABLE, null, categoryCombo);

        ContentValues categoryCombo2 = CreateCategoryComboUtils.create(2L, "x31y45jvIQL");
        database().insert(CategoryComboModel.TABLE, null, categoryCombo2);

        // inserting tracked entity
        ContentValues trackedEntityType = CreateTrackedEntityUtils.create(1L, "nEenWmSyUEp");
        database().insert(TrackedEntityTypeModel.TABLE, null, trackedEntityType);

        programEndpointCall = ProgramEndpointCall.factory(d2.retrofit().create(ProgramService.class)).create(
                GenericCallData.create(databaseAdapter(), d2.retrofit(), new Date()));
    }

    @Test
    public void persist_program_when_call() throws Exception {
        // Fake call to api
        programEndpointCall.call();

        Cursor programCursor = database().query(ProgramModel.TABLE, PROGRAM_PROJECTION, null, null, null, null, null);

        assertThatCursor(programCursor).hasRow(
                "IpHINAT79UW",
                null,
                "Child Programme",
                "Child Programme",
                "2013-03-04T11:41:07.494",
                "2017-01-26T19:39:33.356",
                "Child Programme",
                "Child Programme",
                null,
                null,
                5,
                1, // true
                "Date of enrollment",
                1, // true
                "Date of birth",
                1, // true
                0, // false
                0, // false
                0, // false
                0, // false
                0, // false
                1, // true
                1, // true
                0, // false
                "WITH_REGISTRATION",
                null,
                null,
                null,
                "nEenWmSyUEp",
                "nM3u9s5a52V",
                0
        ).isExhausted();
    }

    @Test
    public void persist_program_rule_variables_on_call() throws Exception {
        programEndpointCall.call();
        String[] projection = {
                ProgramRuleVariableModel.Columns.UID,
                ProgramRuleVariableModel.Columns.CODE,
                ProgramRuleVariableModel.Columns.NAME,
                ProgramRuleVariableModel.Columns.DISPLAY_NAME,
                ProgramRuleVariableModel.Columns.CREATED,
                ProgramRuleVariableModel.Columns.LAST_UPDATED,
                ProgramRuleVariableModel.Columns.USE_CODE_FOR_OPTION_SET,
                ProgramRuleVariableModel.Columns.PROGRAM,
                ProgramRuleVariableModel.Columns.PROGRAM_STAGE,
                ProgramRuleVariableModel.Columns.DATA_ELEMENT,
                ProgramRuleVariableModel.Columns.TRACKED_ENTITY_ATTRIBUTE,
                ProgramRuleVariableModel.Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE
        };

        Cursor programRuleVariableCursor = database().query(ProgramRuleVariableModel.TABLE, projection,
                ProgramRuleVariableModel.Columns.UID + "=?", new String[]{"g2GooOydipB"}, null, null, null);

        assertThatCursor(programRuleVariableCursor).hasRow(
                "g2GooOydipB",
                null,
                "apgarscore",
                "apgarscore",
                "2015-08-07T18:41:55.152",
                "2015-08-07T18:41:55.153",
                null,
                "IpHINAT79UW",
                null,
                null,
                null,
                "DATAELEMENT_NEWEST_EVENT_PROGRAM"
        ).isExhausted();
    }

    @Test
    public void persist_program_tracker_entity_attributes_when_call() throws Exception {
        programEndpointCall.call();
        String[] projection = {
                ProgramTrackedEntityAttributeModel.Columns.UID,
                ProgramTrackedEntityAttributeModel.Columns.CODE,
                ProgramTrackedEntityAttributeModel.Columns.NAME,
                ProgramTrackedEntityAttributeModel.Columns.DISPLAY_NAME,
                ProgramTrackedEntityAttributeModel.Columns.CREATED,
                ProgramTrackedEntityAttributeModel.Columns.LAST_UPDATED,
                ProgramTrackedEntityAttributeModel.Columns.SHORT_NAME,
                ProgramTrackedEntityAttributeModel.Columns.DISPLAY_SHORT_NAME,
                ProgramTrackedEntityAttributeModel.Columns.DESCRIPTION,
                ProgramTrackedEntityAttributeModel.Columns.DISPLAY_DESCRIPTION,
                ProgramTrackedEntityAttributeModel.Columns.MANDATORY,
                ProgramTrackedEntityAttributeModel.Columns.TRACKED_ENTITY_ATTRIBUTE,
                ProgramTrackedEntityAttributeModel.Columns.ALLOW_FUTURE_DATES,
                ProgramTrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST,
                ProgramTrackedEntityAttributeModel.Columns.PROGRAM,
                ProgramTrackedEntityAttributeModel.Columns.SORT_ORDER
        };

        Cursor programTrackedEntityAttributeCursor = database().query(ProgramTrackedEntityAttributeModel.TABLE,
                projection,
                ProgramTrackedEntityAttributeModel.Columns.UID + "=?",
                new String[]{"l2T72XzBCLd"},
                null, null, null);

        assertThatCursor(programTrackedEntityAttributeCursor).hasRow(
                "l2T72XzBCLd",
                null,
                "Child Programme First name",
                "Child Programme First name",
                "2017-01-26T19:39:33.347",
                "2017-01-26T19:39:33.347",
                "Child Programme First name",
                "Child Programme First name",
                null,
                null,
                0, // false
                "w75KJ2mc4zz",
                0, // false
                1, // true
                "IpHINAT79UW",
                99
        ).isExhausted();
    }

    @Test
    public void persist_tracked_entity_attribute_when_call() throws Exception {
        programEndpointCall.call();
        String[] projection = {
                TrackedEntityAttributeModel.Columns.UID,
                TrackedEntityAttributeModel.Columns.CODE,
                TrackedEntityAttributeModel.Columns.NAME,
                TrackedEntityAttributeModel.Columns.DISPLAY_NAME,
                TrackedEntityAttributeModel.Columns.CREATED,
                TrackedEntityAttributeModel.Columns.LAST_UPDATED,
                TrackedEntityAttributeModel.Columns.SHORT_NAME,
                TrackedEntityAttributeModel.Columns.DISPLAY_SHORT_NAME,
                TrackedEntityAttributeModel.Columns.DESCRIPTION,
                TrackedEntityAttributeModel.Columns.DISPLAY_DESCRIPTION,
                TrackedEntityAttributeModel.Columns.PATTERN,
                TrackedEntityAttributeModel.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM,
                TrackedEntityAttributeModel.Columns.OPTION_SET,
                TrackedEntityAttributeModel.Columns.VALUE_TYPE,
                TrackedEntityAttributeModel.Columns.EXPRESSION,
                TrackedEntityAttributeModel.Columns.SEARCH_SCOPE,
                TrackedEntityAttributeModel.Columns.PROGRAM_SCOPE,
                TrackedEntityAttributeModel.Columns.DISPLAY_IN_LIST_NO_PROGRAM,
                TrackedEntityAttributeModel.Columns.GENERATED,
                TrackedEntityAttributeModel.Columns.DISPLAY_ON_VISIT_SCHEDULE,
                TrackedEntityAttributeModel.Columns.ORG_UNIT_SCOPE,
                TrackedEntityAttributeModel.Columns.UNIQUE,
                TrackedEntityAttributeModel.Columns.INHERIT
        };

        Cursor trackedEntityAttributeCursor = database().query(TrackedEntityAttributeModel.TABLE, projection,
                TrackedEntityAttributeModel.Columns.UID + "=?", new String[]{"w75KJ2mc4zz"}, null, null, null);

        assertThatCursor(trackedEntityAttributeCursor).hasRow(
                "w75KJ2mc4zz",
                "MMD_PER_NAM",
                "First name",
                "First name",
                "2014-06-06T20:41:25.233",
                "2015-10-20T13:57:00.939",
                "First name",
                "First name",
                "First name",
                "First name",
                "",
                1,
                null,
                "TEXT",
                null,
                "SEARCH_ORG_UNITS",
                0, // false
                1, // true
                0, // false
                0, // false
                0, // false
                0, // false
                0 // false
        ).isExhausted();
    }

    @Test
    public void persist_program_indicators_when_call() throws Exception {
        programEndpointCall.call();

        String[] projection = {
                ProgramIndicatorModel.Columns.UID,
                ProgramIndicatorModel.Columns.CODE,
                ProgramIndicatorModel.Columns.NAME,
                ProgramIndicatorModel.Columns.DISPLAY_NAME,
                ProgramIndicatorModel.Columns.CREATED,
                ProgramIndicatorModel.Columns.LAST_UPDATED,
                ProgramIndicatorModel.Columns.SHORT_NAME,
                ProgramIndicatorModel.Columns.DISPLAY_SHORT_NAME,
                ProgramIndicatorModel.Columns.DESCRIPTION,
                ProgramIndicatorModel.Columns.DISPLAY_DESCRIPTION,
                ProgramIndicatorModel.Columns.DISPLAY_IN_FORM,
                ProgramIndicatorModel.Columns.EXPRESSION,
                ProgramIndicatorModel.Columns.DIMENSION_ITEM,
                ProgramIndicatorModel.Columns.FILTER,
                ProgramIndicatorModel.Columns.DECIMALS,
                ProgramIndicatorModel.Columns.PROGRAM
        };

        Cursor programIndicatorCursor = database().query(ProgramIndicatorModel.TABLE, projection,
                ProgramIndicatorModel.Columns.UID + "=?", new String[]{"rXoaHGAXWy9"}, null, null, null);
        assertThatCursor(programIndicatorCursor).hasRow(
                "rXoaHGAXWy9",
                null,
                "Health immunization score",
                "Health immunization score",
                "2015-10-20T11:26:19.631",
                "2015-10-20T11:26:19.631",
                "Health immunization score",
                "Health immunization score",
                "Sum of BCG doses, measles doses and yellow fever doses." +
                        " If Apgar score over or equal to 2, multiply by 2.",
                "Sum of BCG doses, measles doses and yellow fever doses." +
                        " If Apgar score over or equal to 2, multiply by 2.",
                0, // false
                "(#{A03MvHHogjR.bx6fsa0t90x} +  #{A03MvHHogjR.FqlgKAG8HOu} + #{A03MvHHogjR.rxBfISxXS2U}) " +
                        "* d2:condition('#{A03MvHHogjR.a3kGcGDCuk6} >= 2',1,2)",
                "rXoaHGAXWy9",
                null,
                2,
                "IpHINAT79UW"
        ).isExhausted();
    }

    @Test
    public void persist_program_rules_when_call() throws Exception {
        programEndpointCall.call();
        String[] projection = {
                ProgramRuleModel.Columns.UID,
                ProgramRuleModel.Columns.CODE,
                ProgramRuleModel.Columns.NAME,
                ProgramRuleModel.Columns.DISPLAY_NAME,
                ProgramRuleModel.Columns.CREATED,
                ProgramRuleModel.Columns.LAST_UPDATED,
                ProgramRuleModel.Columns.PRIORITY,
                ProgramRuleModel.Columns.CONDITION,
                ProgramRuleModel.Columns.PROGRAM,
                ProgramRuleModel.Columns.PROGRAM_STAGE
        };

        Cursor programRuleCursor = database().query(ProgramRuleModel.TABLE, projection,
                ProgramRuleModel.Columns.UID + "=?", new String[]{"NAgjOfWMXg6"}, null, null, null);

        assertThatCursor(programRuleCursor).hasRow(
                "NAgjOfWMXg6",
                null,
                "Ask for comment for low apgar",
                "Ask for comment for low apgar",
                "2015-09-14T21:17:40.841",
                "2015-09-14T22:22:15.383",
                null,
                "#{apgarscore} >= 0 && #{apgarscore} < 4 && #{apgarcomment} == ''",
                "IpHINAT79UW",
                null
        ).isExhausted();
    }

    @Test
    public void persist_program_rule_actions_when_call() throws Exception {
        programEndpointCall.call();

        String[] projection = {
                ProgramRuleActionModel.Columns.UID,
                ProgramRuleActionModel.Columns.CODE,
                ProgramRuleActionModel.Columns.NAME,
                ProgramRuleActionModel.Columns.DISPLAY_NAME,
                ProgramRuleActionModel.Columns.CREATED,
                ProgramRuleActionModel.Columns.LAST_UPDATED,
                ProgramRuleActionModel.Columns.DATA,
                ProgramRuleActionModel.Columns.CONTENT,
                ProgramRuleActionModel.Columns.LOCATION,
                ProgramRuleActionModel.Columns.TRACKED_ENTITY_ATTRIBUTE,
                ProgramRuleActionModel.Columns.PROGRAM_INDICATOR,
                ProgramRuleActionModel.Columns.PROGRAM_STAGE_SECTION,
                ProgramRuleActionModel.Columns.PROGRAM_RULE_ACTION_TYPE,
                ProgramRuleActionModel.Columns.PROGRAM_STAGE,
                ProgramRuleActionModel.Columns.DATA_ELEMENT,
                ProgramRuleActionModel.Columns.PROGRAM_RULE
        };

        Cursor programRuleActionCursor = database().query(ProgramRuleActionModel.TABLE, projection,
                ProgramRuleActionModel.Columns.UID + "=?", new String[]{"v434s5YPDcP"}, null, null, null);

        assertThatCursor(programRuleActionCursor).hasRow(
                "v434s5YPDcP",
                null,
                null,
                null,
                "2015-09-14T21:17:41.033",
                "2015-09-14T22:22:15.458",
                null,
                "It is suggested that an explanation is provided when the Apgar score is below 4",
                null,
                null,
                null,
                null,
                "SHOWWARNING",
                null,
                null,
                "NAgjOfWMXg6"
        ).isExhausted();
    }

    /**
     * Relationship type doesn't exist for the program in the payload. Therefore we'll need to check that it doesn't
     * exist in the database
     *
     * @throws Exception
     */
    @Test
    public void not_persist_relationship_type_when_call() throws Exception {
        programEndpointCall.call();

        String[] projection = {
                RelationshipTypeModel.Columns.UID,
                RelationshipTypeModel.Columns.CODE,
                RelationshipTypeModel.Columns.NAME,
                RelationshipTypeModel.Columns.DISPLAY_NAME,
                RelationshipTypeModel.Columns.CREATED,
                RelationshipTypeModel.Columns.LAST_UPDATED,
                RelationshipTypeModel.Columns.A_IS_TO_B,
                RelationshipTypeModel.Columns.B_IS_TO_A
        };

        Cursor relationshipTypeCursor = database().query(RelationshipTypeModel.TABLE, projection,
                null, null, null, null, null);

        assertThatCursor(relationshipTypeCursor).isExhausted();
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }
}
