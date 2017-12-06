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

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.FieldsConverterFactory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.dataelement.DataElementHandler;
import org.hisp.dhis.android.core.dataelement.DataElementModel;
import org.hisp.dhis.android.core.dataelement.DataElementStore;
import org.hisp.dhis.android.core.dataelement.DataElementStoreImpl;
import org.hisp.dhis.android.core.option.OptionHandler;
import org.hisp.dhis.android.core.option.OptionSetHandler;
import org.hisp.dhis.android.core.option.OptionSetModel;
import org.hisp.dhis.android.core.option.OptionSetStore;
import org.hisp.dhis.android.core.option.OptionSetStoreImpl;
import org.hisp.dhis.android.core.option.OptionStore;
import org.hisp.dhis.android.core.option.OptionStoreImpl;
import org.hisp.dhis.android.core.relationship.RelationshipTypeHandler;
import org.hisp.dhis.android.core.relationship.RelationshipTypeModel;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStoreImpl;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.CreateTrackedEntityUtils;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeModel;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModel;
import org.hisp.dhis.android.core.utils.HeaderUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.hisp.dhis.android.core.data.database.CursorAssert.assertThatCursor;

@RunWith(AndroidJUnit4.class)
public class ProgramCallMockIntegrationShould extends AbsStoreTestCase {
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
            ProgramModel.Columns.TRACKED_ENTITY
    };

    private MockWebServer mockWebServer;
    private Call<Response<Payload<Program>>> programCall;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        MockResponse mockResponse = new MockResponse();
        mockResponse.setHeader(HeaderUtils.DATE, Calendar.getInstance().getTime());
        mockResponse.setBody(RESPONSE_BODY);

        mockWebServer.enqueue(mockResponse);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(BaseIdentifiableObject.DATE_FORMAT.raw());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .addConverterFactory(FieldsConverterFactory.create())
                .build();
        TrackedEntityAttributeStore trackedEntityAttributeStore =
                new TrackedEntityAttributeStoreImpl(databaseAdapter());
        TrackedEntityAttributeHandler trackedEntityAttributeHandler =
                new TrackedEntityAttributeHandler(trackedEntityAttributeStore);

        ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore =
                new ProgramTrackedEntityAttributeStoreImpl(databaseAdapter());

        ProgramTrackedEntityAttributeHandler programTrackedEntityAttributeHandler =
                new ProgramTrackedEntityAttributeHandler(
                        programTrackedEntityAttributeStore,
                        trackedEntityAttributeHandler
                );

        ProgramRuleVariableStore programRuleVariableStore =
                new ProgramRuleVariableStoreImpl(databaseAdapter());
        ProgramRuleVariableHandler programRuleVariableHandler =
                new ProgramRuleVariableHandler(programRuleVariableStore);

        ProgramIndicatorStore programIndicatorStore = new ProgramIndicatorStoreImpl(databaseAdapter());
        ProgramStageSectionProgramIndicatorLinkStore programStageSectionProgramIndicatorLinkStore =
                new ProgramStageSectionProgramIndicatorLinkStoreImpl(databaseAdapter());
        ProgramIndicatorHandler programIndicatorHandler = new ProgramIndicatorHandler(
                programIndicatorStore,
                programStageSectionProgramIndicatorLinkStore
        );

        ProgramRuleActionStore programRuleActionStore = new ProgramRuleActionStoreImpl(databaseAdapter());
        ProgramRuleActionHandler programRuleActionHandler = new ProgramRuleActionHandler(programRuleActionStore);
        ProgramRuleStore programRuleStore = new ProgramRuleStoreImpl(databaseAdapter());
        ProgramRuleHandler programRuleHandler = new ProgramRuleHandler(programRuleStore, programRuleActionHandler);

        OptionStore optionStore = new OptionStoreImpl(databaseAdapter());
        OptionHandler optionHandler = new OptionHandler(optionStore);

        OptionSetStore optionSetStore = new OptionSetStoreImpl(databaseAdapter());
        OptionSetHandler optionSetHandler = new OptionSetHandler(optionSetStore, optionHandler);


        DataElementStore dataElementStore = new DataElementStoreImpl(databaseAdapter());
        DataElementHandler dataElementHandler = new DataElementHandler(dataElementStore, optionSetHandler);
        ProgramStageDataElementStore programStageDataElementStore =
                new ProgramStageDataElementStoreImpl(databaseAdapter());

        ProgramStageDataElementHandler programStageDataElementHandler = new ProgramStageDataElementHandler(
                programStageDataElementStore, dataElementHandler
        );

        ProgramStageSectionStore programStageSectionStore = new ProgramStageSectionStoreImpl(databaseAdapter());
        ProgramStageSectionHandler programStageSectionHandler = new ProgramStageSectionHandler(
                programStageSectionStore,
                programStageDataElementHandler,
                programIndicatorHandler
        );

        ProgramStageStore programStageStore = new ProgramStageStoreImpl(databaseAdapter());
        ProgramStageHandler programStageHandler = new ProgramStageHandler(
                programStageStore,
                programStageSectionHandler,
                programStageDataElementHandler
        );

        RelationshipTypeStore relationshipStore = new RelationshipTypeStoreImpl(databaseAdapter());
        RelationshipTypeHandler relationshipTypeHandler = new RelationshipTypeHandler(relationshipStore);
        ProgramService programService = retrofit.create(ProgramService.class);
        ProgramStore programStore = new ProgramStoreImpl(databaseAdapter());


        ProgramHandler programHandler = new ProgramHandler(
                programStore,
                programRuleVariableHandler,
                programStageHandler,
                programIndicatorHandler,
                programRuleHandler,
                programTrackedEntityAttributeHandler,
                relationshipTypeHandler);

        ResourceStore resourceStore = new ResourceStoreImpl(databaseAdapter());
        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

        Set<String> uids = new HashSet<>();
        uids.add("uid1");
        uids.add("uids2");

        // inserting tracked entity
        ContentValues trackedEntity = CreateTrackedEntityUtils.create(1L, "nEenWmSyUEp");
        database().insert(TrackedEntityModel.TABLE, null, trackedEntity);

        programCall = new ProgramCall(
                programService, databaseAdapter(), resourceStore, uids, programStore, new Date(),
                trackedEntityAttributeStore, programTrackedEntityAttributeStore, programRuleVariableStore,
                programIndicatorStore, programStageSectionProgramIndicatorLinkStore, programRuleActionStore,
                programRuleStore, optionStore, optionSetStore, dataElementStore, programStageDataElementStore,
                programStageSectionStore, programStageStore, relationshipStore
        );
    }

    @Test
    public void persist_program_when_call() throws Exception {
        // Fake call to api
        programCall.call();

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
                "nEenWmSyUEp"
        ).isExhausted();
    }

    @Test
    public void persist_program_stage_when_call() throws Exception {
        programCall.call();
        String[] projection = {
                ProgramStageModel.Columns.UID,
                ProgramStageModel.Columns.CODE,
                ProgramStageModel.Columns.NAME,
                ProgramStageModel.Columns.DISPLAY_NAME,
                ProgramStageModel.Columns.CREATED,
                ProgramStageModel.Columns.LAST_UPDATED,
                ProgramStageModel.Columns.EXECUTION_DATE_LABEL,
                ProgramStageModel.Columns.ALLOW_GENERATE_NEXT_VISIT,
                ProgramStageModel.Columns.VALID_COMPLETE_ONLY,
                ProgramStageModel.Columns.REPORT_DATE_TO_USE,
                ProgramStageModel.Columns.OPEN_AFTER_ENROLLMENT,
                ProgramStageModel.Columns.REPEATABLE,
                ProgramStageModel.Columns.CAPTURE_COORDINATES,
                ProgramStageModel.Columns.FORM_TYPE,
                ProgramStageModel.Columns.DISPLAY_GENERATE_EVENT_BOX,
                ProgramStageModel.Columns.GENERATED_BY_ENROLMENT_DATE,
                ProgramStageModel.Columns.AUTO_GENERATE_EVENT,
                ProgramStageModel.Columns.SORT_ORDER,
                ProgramStageModel.Columns.HIDE_DUE_DATE,
                ProgramStageModel.Columns.BLOCK_ENTRY_FORM,
                ProgramStageModel.Columns.MIN_DAYS_FROM_START,
                ProgramStageModel.Columns.STANDARD_INTERVAL,
                ProgramStageModel.Columns.PROGRAM
        };
        Cursor programStageCursor = database().query(ProgramStageModel.TABLE, projection,
                ProgramStageModel.Columns.UID + "=?", new String[]{"A03MvHHogjR"}, null, null, null);

        assertThatCursor(programStageCursor).hasRow(
                "A03MvHHogjR",
                null,
                "Birth",
                "Birth",
                "2013-03-04T11:41:07.541",
                "2016-10-11T10:32:53.527",
                "Report date",
                0, // false
                0, // false
                null,
                0, // false
                0, // false
                0, // false
                "DEFAULT",
                0, // false
                0, // false
                0, // false
                1,
                0, // false
                0, // false
                0,
                null,
                "IpHINAT79UW"
        ).isExhausted();
    }

    /**
     * There is no ProgramStageSections in the payload in setUpMethod. Therefore we need to check that
     * no program stage sections exists in database
     *
     * @throws Exception
     */
    @Test
    public void not_persist_program_stage_sections_when_call() throws Exception {
        programCall.call();
        String[] projection = {
                ProgramStageSectionModel.Columns.UID,
                ProgramStageSectionModel.Columns.CODE,
                ProgramStageSectionModel.Columns.NAME,
                ProgramStageSectionModel.Columns.DISPLAY_NAME,
                ProgramStageSectionModel.Columns.CREATED,
                ProgramStageSectionModel.Columns.LAST_UPDATED,
                ProgramStageSectionModel.Columns.SORT_ORDER,
                ProgramStageSectionModel.Columns.PROGRAM_STAGE
        };
        Cursor programStageSectionCursor = database().query(ProgramStageSectionModel.TABLE, projection,
                null, null, null, null, null);

        assertThatCursor(programStageSectionCursor).isExhausted();
    }

    @Test
    public void persist_program_stage_data_element_when_call() throws Exception {
        programCall.call();

        String[] projection = {
                ProgramStageDataElementModel.Columns.UID,
                ProgramStageDataElementModel.Columns.CODE,
                ProgramStageDataElementModel.Columns.NAME,
                ProgramStageDataElementModel.Columns.DISPLAY_NAME,
                ProgramStageDataElementModel.Columns.CREATED,
                ProgramStageDataElementModel.Columns.LAST_UPDATED,
                ProgramStageDataElementModel.Columns.DISPLAY_IN_REPORTS,
                ProgramStageDataElementModel.Columns.COMPULSORY,
                ProgramStageDataElementModel.Columns.ALLOW_PROVIDED_ELSEWHERE,
                ProgramStageDataElementModel.Columns.SORT_ORDER,
                ProgramStageDataElementModel.Columns.ALLOW_FUTURE_DATE,
                ProgramStageDataElementModel.Columns.DATA_ELEMENT,
                ProgramStageDataElementModel.Columns.PROGRAM_STAGE,
                ProgramStageDataElementModel.Columns.PROGRAM_STAGE_SECTION
        };

        Cursor programStageDataElementCursor = database().query(ProgramStageDataElementModel.TABLE, projection,
                ProgramStageDataElementModel.Columns.PROGRAM_STAGE + "=?" +
                        " AND " +
                        ProgramStageDataElementModel.Columns.DATA_ELEMENT + "=?",
                new String[]{"ZzYYXq4fJie", "GQY2lXrypjO"}, null, null, null);

        assertThatCursor(programStageDataElementCursor).hasRow(
                "ztoQtbuXzsI",
                null,
                null,
                null,
                "2015-03-27T16:27:19.000",
                "2015-08-06T20:16:48.340",
                0, // false
                0, // false
                1, // true
                0,
                0, // false
                "GQY2lXrypjO",
                "ZzYYXq4fJie",
                null
        ).isExhausted();
    }

    @Test
    public void persist_data_element_when_call() throws Exception {
        programCall.call();

        String[] projection = {
                DataElementModel.Columns.UID,
                DataElementModel.Columns.CODE,
                DataElementModel.Columns.NAME,
                DataElementModel.Columns.DISPLAY_NAME,
                DataElementModel.Columns.CREATED,
                DataElementModel.Columns.LAST_UPDATED,
                DataElementModel.Columns.SHORT_NAME,
                DataElementModel.Columns.DISPLAY_SHORT_NAME,
                DataElementModel.Columns.DESCRIPTION,
                DataElementModel.Columns.DISPLAY_DESCRIPTION,
                DataElementModel.Columns.VALUE_TYPE,
                DataElementModel.Columns.ZERO_IS_SIGNIFICANT,
                DataElementModel.Columns.AGGREGATION_TYPE,
                DataElementModel.Columns.FORM_NAME,
                DataElementModel.Columns.NUMBER_TYPE,
                DataElementModel.Columns.DOMAIN_TYPE,
                DataElementModel.Columns.DIMENSION,
                DataElementModel.Columns.DISPLAY_FORM_NAME,
                DataElementModel.Columns.OPTION_SET
        };

        Cursor dataElementCursor = database().query(DataElementModel.TABLE, projection,
                DataElementModel.Columns.UID + "=?", new String[]{"GQY2lXrypjO"}, null, null, null);

        assertThatCursor(dataElementCursor).hasRow(
                "GQY2lXrypjO",
                "DE_2006099",
                "MCH Infant Weight  (g)",
                "MCH Infant Weight  (g)",
                "2012-09-20T08:44:53.428",
                "2014-11-11T21:56:05.550",
                "Infant Weight (g)",
                "Infant Weight (g)",
                "Infant weight in grams",
                "Infant weight in grams",
                "NUMBER",
                0, // false
                "AVERAGE",
                "Infant Weight (g)",
                null,
                "TRACKER",
                null,
                "Infant Weight (g)",
                null
        ).isExhausted();
    }

    @Test
    public void persist_option_set_when_call() throws Exception {
        programCall.call();

        String[] projection = {
                OptionSetModel.Columns.UID,
                OptionSetModel.Columns.VERSION
        };

        Cursor optionSetCursor = database().query(OptionSetModel.TABLE, projection,
                OptionSetModel.Columns.UID + "=?", new String[]{"x31y45jvIQL"}, null, null, null);

        assertThatCursor(optionSetCursor).hasRow(
                "x31y45jvIQL",
                1
        ).isExhausted();
    }

    @Test
    public void persist_program_rule_variables_on_call() throws Exception {
        programCall.call();
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
                "a3kGcGDCuk6",
                null,
                "DATAELEMENT_NEWEST_EVENT_PROGRAM"
        ).isExhausted();
    }

    @Test
    public void persist_program_tracker_entity_attributes_when_call() throws Exception {
        programCall.call();
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
        programCall.call();
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
        programCall.call();

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
        programCall.call();
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
        programCall.call();

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
                "H6uSAMO5WLD",
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
        programCall.call();

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

        mockWebServer.shutdown();
    }

    //TODO: add a comment with the url for this json:
    public static final String RESPONSE_BODY = "{\n" + "\n" +
            "    \"pager\": {\n" +
            "        \"page\": 1,\n" +
            "        \"pageCount\": 1,\n" +
            "        \"total\": 1,\n" +
            "        \"pageSize\": 50\n" +
            "    },\n" +
            "    \"programs\": [\n" +
            "        {\n" +
            "            \"lastUpdated\": \"2017-01-26T19:39:33.356\",\n" +
            "            \"id\": \"IpHINAT79UW\",\n" +
            "            \"created\": \"2013-03-04T11:41:07.494\",\n" +
            "            \"name\": \"Child Programme\",\n" +
            "            \"shortName\": \"Child Programme\",\n" +
            "            \"displayName\": \"Child Programme\",\n" +
            "            \"ignoreOverdueEvents\": false,\n" +
            "            \"dataEntryMethod\": false,\n" +
            "            \"displayShortName\": \"Child Programme\",\n" +
            "            \"captureCoordinates\": true,\n" +
            "            \"displayFrontPageList\": false,\n" +
            "            \"enrollmentDateLabel\": \"Date of enrollment\",\n" +
            "            \"onlyEnrollOnce\": true,\n" +
            "            \"programType\": \"WITH_REGISTRATION\",\n" +
            "            \"relationshipFromA\": false,\n" +
            "            \"version\": 5,\n" +
            "            \"selectIncidentDatesInFuture\": false,\n" +
            "            \"incidentDateLabel\": \"Date of birth\",\n" +
            "            \"displayIncidentDate\": true,\n" +
            "            \"selectEnrollmentDatesInFuture\": false,\n" +
            "            \"registration\": true,\n" +
            "            \"useFirstStageDuringRegistration\": true,\n" +
            "            \"trackedEntity\": {\n" +
            "                \"id\": \"nEenWmSyUEp\"\n" +
            "            },\n" +
            "            \"programRuleVariables\": [\n" +
            "                {\n" +
            "                    \"created\": \"2015-08-07T18:41:55.152\",\n" +
            "                    \"lastUpdated\": \"2015-08-07T18:41:55.153\",\n" +
            "                    \"name\": \"apgarscore\",\n" +
            "                    \"id\": \"g2GooOydipB\",\n" +
            "                    \"displayName\": \"apgarscore\",\n" +
            "                    \"programRuleVariableSourceType\": \"DATAELEMENT_NEWEST_EVENT_PROGRAM\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    },\n" +
            "                    \"dataElement\": {\n" +
            "                        \"id\": \"a3kGcGDCuk6\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"created\": \"2015-09-14T21:17:40.956\",\n" +
            "                    \"lastUpdated\": \"2015-09-14T21:17:40.958\",\n" +
            "                    \"name\": \"apgarcomment\",\n" +
            "                    \"id\": \"aKpfPKSRQnv\",\n" +
            "                    \"displayName\": \"apgarcomment\",\n" +
            "                    \"programRuleVariableSourceType\": \"DATAELEMENT_NEWEST_EVENT_PROGRAM\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    },\n" +
            "                    \"dataElement\": {\n" +
            "                        \"id\": \"H6uSAMO5WLD\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"programTrackedEntityAttributes\": [\n" +
            "                {\n" +
            "                    \"created\": \"2017-01-26T19:39:33.347\",\n" +
            "                    \"lastUpdated\": \"2017-01-26T19:39:33.347\",\n" +
            "                    \"name\": \"Child Programme First name\",\n" +
            "                    \"id\": \"l2T72XzBCLd\",\n" +
            "                    \"shortName\": \"Child Programme First name\",\n" +
            "                    \"displayName\": \"Child Programme First name\",\n" +
            "                    \"mandatory\": false,\n" +
            "                    \"displayShortName\": \"Child Programme First name\",\n" +
            "                    \"displayInList\": true,\n" +
            "                    \"valueType\": \"TEXT\",\n" +
            "                    \"sortOrder\": 99,\n" +
            "                    \"allowFutureDate\": false,\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    },\n" +
            "                    \"trackedEntityAttribute\": {\n" +
            "                        \"code\": \"MMD_PER_NAM\",\n" +
            "                        \"created\": \"2014-06-06T20:41:25.233\",\n" +
            "                        \"lastUpdated\": \"2015-10-20T13:57:00.939\",\n" +
            "                        \"name\": \"First name\",\n" +
            "                        \"id\": \"w75KJ2mc4zz\",\n" +
            "                        \"shortName\": \"First name\",\n" +
            "                        \"displayDescription\": \"First name\",\n" +
            "                        \"programScope\": false,\n" +
            "                        \"displayInListNoProgram\": true,\n" +
            "                        \"displayName\": \"First name\",\n" +
            "                        \"searchScope\": \"SEARCH_ORG_UNITS\",\n" +
            "                        \"pattern\": \"\",\n" +
            "                        \"description\": \"First name\",\n" +
            "                        \"displayShortName\": \"First name\",\n" +
            "                        \"sortOrderInListNoProgram\": 1,\n" +
            "                        \"displayOnVisitSchedule\": false,\n" +
            "                        \"generated\": false,\n" +
            "                        \"inherit\": false,\n" +
            "                        \"unique\": false,\n" +
            "                        \"valueType\": \"TEXT\",\n" +
            "                        \"orgunitScope\": false\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"created\": \"2017-01-26T19:39:33.350\",\n" +
            "                    \"lastUpdated\": \"2017-01-26T19:39:33.350\",\n" +
            "                    \"name\": \"Child Programme Last name\",\n" +
            "                    \"id\": \"XmRd2ZDjWhF\",\n" +
            "                    \"shortName\": \"Child Programme Last name\",\n" +
            "                    \"displayName\": \"Child Programme Last name\",\n" +
            "                    \"mandatory\": false,\n" +
            "                    \"displayShortName\": \"Child Programme Last name\",\n" +
            "                    \"displayInList\": true,\n" +
            "                    \"valueType\": \"TEXT\",\n" +
            "                    \"sortOrder\": 99,\n" +
            "                    \"allowFutureDate\": false,\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    },\n" +
            "                    \"trackedEntityAttribute\": {\n" +
            "                        \"created\": \"2015-04-14T10:21:48.043\",\n" +
            "                        \"lastUpdated\": \"2015-10-20T13:57:00.944\",\n" +
            "                        \"name\": \"Last name\",\n" +
            "                        \"id\": \"zDhUuAYrxNC\",\n" +
            "                        \"shortName\": \"Last name\",\n" +
            "                        \"displayDescription\": \"Last name\",\n" +
            "                        \"programScope\": false,\n" +
            "                        \"displayInListNoProgram\": true,\n" +
            "                        \"displayName\": \"Last name\",\n" +
            "                        \"searchScope\": \"SEARCH_ORG_UNITS\",\n" +
            "                        \"pattern\": \"\",\n" +
            "                        \"description\": \"Last name\",\n" +
            "                        \"displayShortName\": \"Last name\",\n" +
            "                        \"sortOrderInListNoProgram\": 2,\n" +
            "                        \"displayOnVisitSchedule\": false,\n" +
            "                        \"generated\": false,\n" +
            "                        \"inherit\": false,\n" +
            "                        \"unique\": false,\n" +
            "                        \"valueType\": \"TEXT\",\n" +
            "                        \"orgunitScope\": false\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"created\": \"2017-01-26T19:39:33.352\",\n" +
            "                    \"lastUpdated\": \"2017-01-26T19:39:33.352\",\n" +
            "                    \"name\": \"Child Programme Gender\",\n" +
            "                    \"id\": \"pWEEfUJUjej\",\n" +
            "                    \"shortName\": \"Child Programme Gender\",\n" +
            "                    \"displayName\": \"Child Programme Gender\",\n" +
            "                    \"mandatory\": false,\n" +
            "                    \"displayShortName\": \"Child Programme Gender\",\n" +
            "                    \"displayInList\": false,\n" +
            "                    \"valueType\": \"TEXT\",\n" +
            "                    \"sortOrder\": 99,\n" +
            "                    \"allowFutureDate\": false,\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    },\n" +
            "                    \"trackedEntityAttribute\": {\n" +
            "                        \"created\": \"2014-01-09T19:12:46.526\",\n" +
            "                        \"lastUpdated\": \"2015-10-20T13:57:01.028\",\n" +
            "                        \"name\": \"Gender\",\n" +
            "                        \"id\": \"cejWyOfXge6\",\n" +
            "                        \"shortName\": \"Gender\",\n" +
            "                        \"displayDescription\": \"Gender\",\n" +
            "                        \"programScope\": false,\n" +
            "                        \"displayInListNoProgram\": false,\n" +
            "                        \"displayName\": \"Gender\",\n" +
            "                        \"searchScope\": \"SEARCH_ORG_UNITS\",\n" +
            "                        \"pattern\": \"\",\n" +
            "                        \"description\": \"Gender\",\n" +
            "                        \"displayShortName\": \"Gender\",\n" +
            "                        \"sortOrderInListNoProgram\": 0,\n" +
            "                        \"displayOnVisitSchedule\": false,\n" +
            "                        \"generated\": false,\n" +
            "                        \"inherit\": false,\n" +
            "                        \"unique\": false,\n" +
            "                        \"valueType\": \"TEXT\",\n" +
            "                        \"orgunitScope\": false\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"programIndicators\": [\n" +
            "                {\n" +
            "                    \"created\": \"2015-10-20T11:26:19.631\",\n" +
            "                    \"lastUpdated\": \"2015-10-20T11:26:19.631\",\n" +
            "                    \"name\": \"Health immunization score\",\n" +
            "                    \"id\": \"rXoaHGAXWy9\",\n" +
            "                    \"shortName\": \"Health immunization score\",\n" +
            "                    \"displayDescription\": \"Sum of BCG doses, measles doses and yellow fever doses. If Apgar score over or equal to 2, multiply by 2.\",\n" +
            "                    \"expression\": \"(#{A03MvHHogjR.bx6fsa0t90x} +  #{A03MvHHogjR.FqlgKAG8HOu} + #{A03MvHHogjR.rxBfISxXS2U}) * d2:condition('#{A03MvHHogjR.a3kGcGDCuk6} >= 2',1,2)\",\n" +
            "                    \"dimensionItem\": \"rXoaHGAXWy9\",\n" +
            "                    \"displayName\": \"Health immunization score\",\n" +
            "                    \"displayInForm\": false,\n" +
            "                    \"description\": \"Sum of BCG doses, measles doses and yellow fever doses. If Apgar score over or equal to 2, multiply by 2.\",\n" +
            "                    \"displayShortName\": \"Health immunization score\",\n" +
            "                    \"decimals\": 2,\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"created\": \"2015-09-14T20:25:55.543\",\n" +
            "                    \"lastUpdated\": \"2015-12-13T12:50:25.642\",\n" +
            "                    \"name\": \"Average weight\",\n" +
            "                    \"id\": \"GxdhnY5wmHq\",\n" +
            "                    \"shortName\": \"Average weight\",\n" +
            "                    \"expression\": \"(#{A03MvHHogjR.UXz7xuGCEhU}+#{ZzYYXq4fJie.GQY2lXrypjO})/V{value_count}\",\n" +
            "                    \"dimensionItem\": \"GxdhnY5wmHq\",\n" +
            "                    \"displayName\": \"Average weight\",\n" +
            "                    \"displayInForm\": true,\n" +
            "                    \"displayShortName\": \"Average weight\",\n" +
            "                    \"filter\": \"V{value_count}>0\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"created\": \"2015-08-06T22:48:42.650\",\n" +
            "                    \"lastUpdated\": \"2015-08-09T21:42:06.928\",\n" +
            "                    \"name\": \"Measles + Yellow fever doses\",\n" +
            "                    \"id\": \"fM7RZGVndZE\",\n" +
            "                    \"shortName\": \"Measles + Yellow fever doses\",\n" +
            "                    \"expression\": \"#{ZzYYXq4fJie.FqlgKAG8HOu} + #{ZzYYXq4fJie.rxBfISxXS2U}\",\n" +
            "                    \"dimensionItem\": \"fM7RZGVndZE\",\n" +
            "                    \"displayName\": \"Measles + Yellow fever doses\",\n" +
            "                    \"displayInForm\": true,\n" +
            "                    \"displayShortName\": \"Measles + Yellow fever doses\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"created\": \"2015-08-07T19:04:29.117\",\n" +
            "                    \"lastUpdated\": \"2015-08-07T19:04:29.117\",\n" +
            "                    \"name\": \"Measles + Yellow fever doses female\",\n" +
            "                    \"id\": \"eo73fim1b2i\",\n" +
            "                    \"shortName\": \"Measles + Yellow fever doses female\",\n" +
            "                    \"expression\": \"#{ZzYYXq4fJie.FqlgKAG8HOu} + #{ZzYYXq4fJie.rxBfISxXS2U}\",\n" +
            "                    \"dimensionItem\": \"eo73fim1b2i\",\n" +
            "                    \"displayName\": \"Measles + Yellow fever doses female\",\n" +
            "                    \"displayInForm\": false,\n" +
            "                    \"displayShortName\": \"Measles + Yellow fever doses female\",\n" +
            "                    \"filter\": \"A{cejWyOfXge6} == 'Female'\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"created\": \"2015-08-06T22:49:20.128\",\n" +
            "                    \"lastUpdated\": \"2015-08-06T22:51:19.787\",\n" +
            "                    \"name\": \"Measles + Yellow fever doses low infant weight\",\n" +
            "                    \"id\": \"tt54DiKuQ9c\",\n" +
            "                    \"shortName\": \"Measles + Yellow fever doses low infant weight\",\n" +
            "                    \"expression\": \"#{ZzYYXq4fJie.FqlgKAG8HOu} + #{ZzYYXq4fJie.rxBfISxXS2U}\",\n" +
            "                    \"dimensionItem\": \"tt54DiKuQ9c\",\n" +
            "                    \"displayName\": \"Measles + Yellow fever doses low infant weight\",\n" +
            "                    \"displayInForm\": false,\n" +
            "                    \"displayShortName\": \"Measles + Yellow fever doses low infant weight\",\n" +
            "                    \"filter\": \"#{ZzYYXq4fJie.GQY2lXrypjO} < 2700\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"created\": \"2017-01-20T10:32:26.388\",\n" +
            "                    \"lastUpdated\": \"2017-01-20T10:32:26.388\",\n" +
            "                    \"name\": \"Weight gain(in g) between birth and last postnatal\",\n" +
            "                    \"id\": \"qhTkqwAJLMv\",\n" +
            "                    \"shortName\": \"Weight gain(g)\",\n" +
            "                    \"displayDescription\": \"The average number of grams the baby has gained through the postnatal period. Only counted among the babies that has completed the postnatal period.\",\n" +
            "                    \"expression\": \"#{ZzYYXq4fJie.GQY2lXrypjO} - #{A03MvHHogjR.UXz7xuGCEhU}\",\n" +
            "                    \"dimensionItem\": \"qhTkqwAJLMv\",\n" +
            "                    \"displayName\": \"Weight gain(in g) between birth and last postnatal\",\n" +
            "                    \"displayInForm\": false,\n" +
            "                    \"description\": \"The average number of grams the baby has gained through the postnatal period. Only counted among the babies that has completed the postnatal period.\",\n" +
            "                    \"displayShortName\": \"Weight gain(g)\",\n" +
            "                    \"filter\": \"#{ZzYYXq4fJie.GQY2lXrypjO} != 0 && #{A03MvHHogjR.UXz7xuGCEhU} != 0 && V{enrollment_status} == 'COMPLETED'\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"created\": \"2015-08-06T22:35:40.391\",\n" +
            "                    \"lastUpdated\": \"2015-08-06T22:35:40.391\",\n" +
            "                    \"name\": \"BCG doses low birth weight\",\n" +
            "                    \"id\": \"hCYU0G5Ti2T\",\n" +
            "                    \"shortName\": \"BCG doses low birth weight\",\n" +
            "                    \"expression\": \"#{A03MvHHogjR.bx6fsa0t90x}\",\n" +
            "                    \"dimensionItem\": \"hCYU0G5Ti2T\",\n" +
            "                    \"displayName\": \"BCG doses low birth weight\",\n" +
            "                    \"displayInForm\": false,\n" +
            "                    \"displayShortName\": \"BCG doses low birth weight\",\n" +
            "                    \"filter\": \"#{A03MvHHogjR.UXz7xuGCEhU} < 2700\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"created\": \"2015-08-06T22:25:14.645\",\n" +
            "                    \"lastUpdated\": \"2015-08-06T22:25:14.645\",\n" +
            "                    \"name\": \"BCG doses\",\n" +
            "                    \"id\": \"p2Zxg0wcPQ3\",\n" +
            "                    \"shortName\": \"BCG doses\",\n" +
            "                    \"expression\": \"#{A03MvHHogjR.bx6fsa0t90x}\",\n" +
            "                    \"dimensionItem\": \"p2Zxg0wcPQ3\",\n" +
            "                    \"displayName\": \"BCG doses\",\n" +
            "                    \"displayInForm\": false,\n" +
            "                    \"displayShortName\": \"BCG doses\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ],\n" +
            "            \"programStages\": [\n" +
            "                {\n" +
            "                    \"lastUpdated\": \"2016-10-11T10:32:53.527\",\n" +
            "                    \"id\": \"A03MvHHogjR\",\n" +
            "                    \"created\": \"2013-03-04T11:41:07.541\",\n" +
            "                    \"name\": \"Birth\",\n" +
            "                    \"allowGenerateNextVisit\": false,\n" +
            "                    \"executionDateLabel\": \"Report date\",\n" +
            "                    \"validCompleteOnly\": false,\n" +
            "                    \"displayName\": \"Birth\",\n" +
            "                    \"openAfterEnrollment\": false,\n" +
            "                    \"repeatable\": false,\n" +
            "                    \"captureCoordinates\": false,\n" +
            "                    \"formType\": \"DEFAULT\",\n" +
            "                    \"displayGenerateEventBox\": false,\n" +
            "                    \"generatedByEnrollmentDate\": false,\n" +
            "                    \"autoGenerateEvent\": false,\n" +
            "                    \"sortOrder\": 1,\n" +
            "                    \"blockEntryForm\": false,\n" +
            "                    \"hideDueDate\": false,\n" +
            "                    \"minDaysFromStart\": 0,\n" +
            "                    \"programStageDataElements\": [\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2016-01-15T18:13:49.600\",\n" +
            "                            \"created\": \"2015-03-31T09:53:05.015\",\n" +
            "                            \"id\": \"LBNxoXdMnkv\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 0,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"A03MvHHogjR\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006098\",\n" +
            "                                \"created\": \"2012-09-20T08:37:02.003\",\n" +
            "                                \"lastUpdated\": \"2016-04-20T17:32:10.262\",\n" +
            "                                \"name\": \"MCH Apgar Score\",\n" +
            "                                \"id\": \"a3kGcGDCuk6\",\n" +
            "                                \"shortName\": \"Apgar Score\",\n" +
            "                                \"displayDescription\": \"Apgar is a quick test performed on a baby at 1 and 5 minutes after birth. The 1-minute score determines how well the baby tolerated the birthing process. The 5-minute score tells the doctor how well the baby is doing outside the mother's womb.\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Apgar Score\",\n" +
            "                                \"description\": \"Apgar is a quick test performed on a baby at 1 and 5 minutes after birth. The 1-minute score determines how well the baby tolerated the birthing process. The 5-minute score tells the doctor how well the baby is doing outside the mother's womb.\",\n" +
            "                                \"displayFormName\": \"Apgar Score\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Apgar Score\",\n" +
            "                                \"formName\": \"Apgar Score\",\n" +
            "                                \"valueType\": \"NUMBER\"\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2016-01-15T18:13:49.612\",\n" +
            "                            \"created\": \"2015-08-07T14:46:19.073\",\n" +
            "                            \"id\": \"yYMGxXpfl0Z\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 1,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"A03MvHHogjR\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"created\": \"2015-08-07T14:44:59.734\",\n" +
            "                                \"lastUpdated\": \"2015-08-07T14:45:56.684\",\n" +
            "                                \"name\": \"MCH Apgar comment\",\n" +
            "                                \"id\": \"H6uSAMO5WLD\",\n" +
            "                                \"shortName\": \"Apgar comment\",\n" +
            "                                \"aggregationType\": \"SUM\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Apgar comment\",\n" +
            "                                \"displayFormName\": \"Apgar comment\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Apgar comment\",\n" +
            "                                \"formName\": \"Apgar comment\",\n" +
            "                                \"valueType\": \"LONG_TEXT\"\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2016-01-15T18:13:49.621\",\n" +
            "                            \"created\": \"2015-03-31T09:53:05.049\",\n" +
            "                            \"id\": \"u2FvnCDCBcD\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 2,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"A03MvHHogjR\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2005736\",\n" +
            "                                \"created\": \"2012-09-20T17:37:45.474\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.418\",\n" +
            "                                \"name\": \"MCH Weight (g)\",\n" +
            "                                \"id\": \"UXz7xuGCEhU\",\n" +
            "                                \"shortName\": \"Weight (g)\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Weight (g)\",\n" +
            "                                \"displayFormName\": \"Weight (g)\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Weight (g)\",\n" +
            "                                \"formName\": \"Weight (g)\",\n" +
            "                                \"valueType\": \"NUMBER\"\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2016-01-15T18:13:49.625\",\n" +
            "                            \"created\": \"2015-03-31T09:53:05.084\",\n" +
            "                            \"id\": \"XLFc6tTftb5\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 3,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"A03MvHHogjR\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2008294\",\n" +
            "                                \"created\": \"2012-09-21T13:12:38.012\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.404\",\n" +
            "                                \"name\": \"MCH ARV at birth\",\n" +
            "                                \"id\": \"wQLfBvPrXqq\",\n" +
            "                                \"shortName\": \"ARV at birth\",\n" +
            "                                \"displayDescription\": \"Onlu used for birth details.\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH ARV at birth\",\n" +
            "                                \"description\": \"Onlu used for birth details.\",\n" +
            "                                \"displayFormName\": \"ARV at birth\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"ARV at birth\",\n" +
            "                                \"formName\": \"ARV at birth\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"f38bstJioPs\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2016-01-15T18:13:49.630\",\n" +
            "                            \"created\": \"2015-03-31T09:53:05.109\",\n" +
            "                            \"id\": \"p8eX3rSkKN0\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 4,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"A03MvHHogjR\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006101\",\n" +
            "                                \"created\": \"2012-09-20T08:40:33.607\",\n" +
            "                                \"lastUpdated\": \"2015-08-06T22:17:09.056\",\n" +
            "                                \"name\": \"MCH BCG dose\",\n" +
            "                                \"id\": \"bx6fsa0t90x\",\n" +
            "                                \"shortName\": \"BCG dose\",\n" +
            "                                \"aggregationType\": \"SUM\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH BCG dose\",\n" +
            "                                \"displayFormName\": \"BCG dose\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"BCG dose\",\n" +
            "                                \"formName\": \"BCG dose\",\n" +
            "                                \"valueType\": \"BOOLEAN\"\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2016-01-15T18:13:49.635\",\n" +
            "                            \"created\": \"2015-03-31T09:53:05.136\",\n" +
            "                            \"id\": \"O4dwFWakvGO\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 5,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"A03MvHHogjR\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006104\",\n" +
            "                                \"created\": \"2012-09-20T18:03:44.110\",\n" +
            "                                \"lastUpdated\": \"2015-08-06T16:44:30.622\",\n" +
            "                                \"name\": \"MCH OPV dose\",\n" +
            "                                \"id\": \"ebaJjqltK5N\",\n" +
            "                                \"shortName\": \"OPV dose\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH OPV dose\",\n" +
            "                                \"displayFormName\": \"OPV dose\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"OPV dose\",\n" +
            "                                \"formName\": \"OPV dose\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"kzgQRhOCadd\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2016-01-15T18:13:49.638\",\n" +
            "                            \"created\": \"2015-03-31T09:53:05.159\",\n" +
            "                            \"id\": \"xtjAxBGQNNV\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 6,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"A03MvHHogjR\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006103\",\n" +
            "                                \"created\": \"2012-09-20T08:43:58.131\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.414\",\n" +
            "                                \"name\": \"MCH Infant Feeding\",\n" +
            "                                \"id\": \"X8zyunlgUfM\",\n" +
            "                                \"shortName\": \"Infant Feeding\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Infant Feeding\",\n" +
            "                                \"displayFormName\": \"Infant Feeding\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Infant Feeding\",\n" +
            "                                \"formName\": \"Infant Feeding\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"x31y45jvIQL\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2016-01-15T18:13:49.641\",\n" +
            "                            \"created\": \"2016-01-15T18:13:49.641\",\n" +
            "                            \"id\": \"JYyXbTmBBls\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 7,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"A03MvHHogjR\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"created\": \"2016-01-15T18:13:32.416\",\n" +
            "                                \"lastUpdated\": \"2016-01-15T22:44:11.475\",\n" +
            "                                \"name\": \"Birth certificate\",\n" +
            "                                \"id\": \"uf3svrmp8Oj\",\n" +
            "                                \"shortName\": \"Birth certificate\",\n" +
            "                                \"aggregationType\": \"SUM\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"Birth certificate\",\n" +
            "                                \"displayFormName\": \"Birth certificate\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Birth certificate\",\n" +
            "                                \"valueType\": \"FILE_RESOURCE\"\n" +
            "                            }\n" +
            "                        }\n" +
            "                    ],\n" +
            "                    \"programStageSections\": [ ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"lastUpdated\": \"2015-08-06T20:16:48.321\",\n" +
            "                    \"id\": \"ZzYYXq4fJie\",\n" +
            "                    \"created\": \"2013-03-04T11:41:07.541\",\n" +
            "                    \"name\": \"Baby Postnatal\",\n" +
            "                    \"allowGenerateNextVisit\": false,\n" +
            "                    \"validCompleteOnly\": false,\n" +
            "                    \"displayName\": \"Baby Postnatal\",\n" +
            "                    \"openAfterEnrollment\": false,\n" +
            "                    \"repeatable\": false,\n" +
            "                    \"captureCoordinates\": false,\n" +
            "                    \"formType\": \"DEFAULT\",\n" +
            "                    \"displayGenerateEventBox\": false,\n" +
            "                    \"generatedByEnrollmentDate\": false,\n" +
            "                    \"autoGenerateEvent\": true,\n" +
            "                    \"sortOrder\": 2,\n" +
            "                    \"blockEntryForm\": false,\n" +
            "                    \"hideDueDate\": false,\n" +
            "                    \"minDaysFromStart\": 6,\n" +
            "                    \"programStageDataElements\": [\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.340\",\n" +
            "                            \"created\": \"2015-03-27T16:27:19.000\",\n" +
            "                            \"id\": \"ztoQtbuXzsI\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": true,\n" +
            "                            \"sortOrder\": 0,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006099\",\n" +
            "                                \"created\": \"2012-09-20T08:44:53.428\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.550\",\n" +
            "                                \"name\": \"MCH Infant Weight  (g)\",\n" +
            "                                \"id\": \"GQY2lXrypjO\",\n" +
            "                                \"shortName\": \"Infant Weight (g)\",\n" +
            "                                \"displayDescription\": \"Infant weight in grams\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Infant Weight  (g)\",\n" +
            "                                \"description\": \"Infant weight in grams\",\n" +
            "                                \"displayFormName\": \"Infant Weight (g)\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Infant Weight (g)\",\n" +
            "                                \"formName\": \"Infant Weight (g)\",\n" +
            "                                \"valueType\": \"NUMBER\"\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.354\",\n" +
            "                            \"created\": \"2015-03-27T16:27:19.000\",\n" +
            "                            \"id\": \"vdc1saaN2ma\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": true,\n" +
            "                            \"sortOrder\": 1,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006103\",\n" +
            "                                \"created\": \"2012-09-20T08:43:58.131\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.414\",\n" +
            "                                \"name\": \"MCH Infant Feeding\",\n" +
            "                                \"id\": \"X8zyunlgUfM\",\n" +
            "                                \"shortName\": \"Infant Feeding\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Infant Feeding\",\n" +
            "                                \"displayFormName\": \"Infant Feeding\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Infant Feeding\",\n" +
            "                                \"formName\": \"Infant Feeding\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"x31y45jvIQL\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.366\",\n" +
            "                            \"created\": \"2015-08-06T13:42:22.491\",\n" +
            "                            \"id\": \"Vpx18GqyLcK\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": true,\n" +
            "                            \"sortOrder\": 2,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006125\",\n" +
            "                                \"created\": \"2015-08-06T13:36:22.541\",\n" +
            "                                \"lastUpdated\": \"2015-08-06T22:17:50.754\",\n" +
            "                                \"name\": \"MCH Measles dose\",\n" +
            "                                \"id\": \"FqlgKAG8HOu\",\n" +
            "                                \"shortName\": \"Measles dose\",\n" +
            "                                \"aggregationType\": \"SUM\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Measles dose\",\n" +
            "                                \"displayFormName\": \"Measles dose\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Measles dose\",\n" +
            "                                \"formName\": \"Measles dose\",\n" +
            "                                \"valueType\": \"BOOLEAN\"\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.376\",\n" +
            "                            \"created\": \"2015-08-06T13:42:22.499\",\n" +
            "                            \"id\": \"WlYechRHVo3\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": true,\n" +
            "                            \"sortOrder\": 3,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"created\": \"2015-08-06T13:39:31.690\",\n" +
            "                                \"lastUpdated\": \"2015-08-06T16:43:12.445\",\n" +
            "                                \"name\": \"MCH Penta dose\",\n" +
            "                                \"id\": \"vTUhAUZFoys\",\n" +
            "                                \"shortName\": \"Penta dose\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Penta dose\",\n" +
            "                                \"displayFormName\": \"Penta dose\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Penta dose\",\n" +
            "                                \"formName\": \"Penta dose\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"kzgQRhOCadd\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.385\",\n" +
            "                            \"created\": \"2015-08-06T13:42:22.504\",\n" +
            "                            \"id\": \"WucAVPYvcEO\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": true,\n" +
            "                            \"sortOrder\": 4,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006126\",\n" +
            "                                \"created\": \"2015-08-06T13:41:15.499\",\n" +
            "                                \"lastUpdated\": \"2015-08-06T22:18:27.907\",\n" +
            "                                \"name\": \"MCH Yellow fever dose\",\n" +
            "                                \"id\": \"rxBfISxXS2U\",\n" +
            "                                \"shortName\": \"Yellow fever dose\",\n" +
            "                                \"aggregationType\": \"SUM\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Yellow fever dose\",\n" +
            "                                \"displayFormName\": \"Yellow fever dose\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Yellow fever dose\",\n" +
            "                                \"formName\": \"Yellow fever dose\",\n" +
            "                                \"valueType\": \"BOOLEAN\"\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.394\",\n" +
            "                            \"created\": \"2015-08-06T14:04:45.215\",\n" +
            "                            \"id\": \"EL5dr5x0WbZ\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 5,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2005744\",\n" +
            "                                \"created\": \"2012-09-20T08:45:02.015\",\n" +
            "                                \"lastUpdated\": \"2015-08-06T13:44:32.035\",\n" +
            "                                \"name\": \"MCH IPT dose\",\n" +
            "                                \"id\": \"lNNb3truQoi\",\n" +
            "                                \"shortName\": \"IPT dose\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH IPT dose\",\n" +
            "                                \"displayFormName\": \"IPT dose\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"IPT dose\",\n" +
            "                                \"formName\": \"IPT dose\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"nH8Y04zS7UV\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.402\",\n" +
            "                            \"created\": \"2015-03-27T16:27:19.000\",\n" +
            "                            \"id\": \"IpPWDRlHJSe\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": true,\n" +
            "                            \"sortOrder\": 6,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006105\",\n" +
            "                                \"created\": \"2012-09-20T18:01:19.313\",\n" +
            "                                \"lastUpdated\": \"2015-08-06T16:43:25.692\",\n" +
            "                                \"name\": \"MCH DPT dose\",\n" +
            "                                \"id\": \"pOe0ogW4OWd\",\n" +
            "                                \"shortName\": \"DPT dose\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH DPT dose\",\n" +
            "                                \"displayFormName\": \"DPT dose\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"DPT dose\",\n" +
            "                                \"formName\": \"DPT dose\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"udkr3ihaeD3\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.408\",\n" +
            "                            \"created\": \"2015-03-27T16:27:19.000\",\n" +
            "                            \"id\": \"xSTVGEIbarb\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": true,\n" +
            "                            \"sortOrder\": 7,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006106\",\n" +
            "                                \"created\": \"2012-09-20T08:46:58.580\",\n" +
            "                                \"lastUpdated\": \"2015-08-06T22:21:28.678\",\n" +
            "                                \"name\": \"MCH Vit A\",\n" +
            "                                \"id\": \"HLmTEmupdX0\",\n" +
            "                                \"shortName\": \"Vit A\",\n" +
            "                                \"aggregationType\": \"SUM\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Vit A\",\n" +
            "                                \"displayFormName\": \"Vit A\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Vit A\",\n" +
            "                                \"formName\": \"Vit A\",\n" +
            "                                \"valueType\": \"BOOLEAN\"\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.416\",\n" +
            "                            \"created\": \"2015-03-27T16:27:19.000\",\n" +
            "                            \"id\": \"YCO2FVT0wXL\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": true,\n" +
            "                            \"sortOrder\": 8,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006107\",\n" +
            "                                \"created\": \"2012-09-20T08:44:35.860\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.548\",\n" +
            "                                \"name\": \"MCH Infant HIV Test Result\",\n" +
            "                                \"id\": \"cYGaxwK615G\",\n" +
            "                                \"shortName\": \"Infant HIV Test Result\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Infant HIV Test Result\",\n" +
            "                                \"displayFormName\": \"Infant HIV Test Result\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Infant HIV Test Result\",\n" +
            "                                \"formName\": \"Infant HIV Test Result\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"oXR37f2wOb1\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.423\",\n" +
            "                            \"created\": \"2015-03-27T16:27:19.000\",\n" +
            "                            \"id\": \"VlOvjLKnoyw\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": true,\n" +
            "                            \"sortOrder\": 9,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006108\",\n" +
            "                                \"created\": \"2012-09-20T08:43:45.821\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.416\",\n" +
            "                                \"name\": \"MCH HIV Test Type\",\n" +
            "                                \"id\": \"hDZbpskhqDd\",\n" +
            "                                \"shortName\": \"HIV Test Type\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH HIV Test Type\",\n" +
            "                                \"displayFormName\": \"HIV Test Type\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"HIV Test Type\",\n" +
            "                                \"formName\": \"HIV Test Type\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"OGmE3wUMEzu\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.433\",\n" +
            "                            \"created\": \"2015-03-27T16:27:19.000\",\n" +
            "                            \"id\": \"rqmcdr07fxQ\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 10,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006100\",\n" +
            "                                \"created\": \"2012-09-21T13:02:22.744\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.599\",\n" +
            "                                \"name\": \"MCH Child ARVs\",\n" +
            "                                \"id\": \"sj3j9Hwc7so\",\n" +
            "                                \"shortName\": \"Child ARVs\",\n" +
            "                                \"displayDescription\": \"ARVs for child postnatal visits.\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Child ARVs\",\n" +
            "                                \"description\": \"ARVs for child postnatal visits.\",\n" +
            "                                \"displayFormName\": \"Child ARVs\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Child ARVs\",\n" +
            "                                \"formName\": \"Child ARVs\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"dgsftM0rXu2\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.444\",\n" +
            "                            \"created\": \"2015-03-27T16:27:19.000\",\n" +
            "                            \"id\": \"LfgZNmadu4W\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 11,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006109\",\n" +
            "                                \"created\": \"2012-09-20T08:46:17.717\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.417\",\n" +
            "                                \"name\": \"MCH Septrin Given\",\n" +
            "                                \"id\": \"aei1xRjSU2l\",\n" +
            "                                \"shortName\": \"Septrin Given\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Septrin Given\",\n" +
            "                                \"displayFormName\": \"Septrin Given\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Septrin Given\",\n" +
            "                                \"formName\": \"Septrin Given\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"XdI8KRJiRoZ\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.452\",\n" +
            "                            \"created\": \"2015-03-27T16:27:19.000\",\n" +
            "                            \"id\": \"sfYk4rKw18B\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 12,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2006110\",\n" +
            "                                \"created\": \"2012-09-20T08:46:05.594\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.402\",\n" +
            "                                \"name\": \"MCH Results given to caretaker\",\n" +
            "                                \"id\": \"BeynU4L6VCQ\",\n" +
            "                                \"shortName\": \"Results given to caretake\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Results given to caretaker\",\n" +
            "                                \"displayFormName\": \"Results given to caretaker\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Results given to caretake\",\n" +
            "                                \"formName\": \"Results given to caretaker\",\n" +
            "                                \"valueType\": \"TEXT\",\n" +
            "                                \"optionSet\": {\n" +
            "                                    \"id\": \"XdI8KRJiRoZ\",\n" +
            "                                    \"version\": 1\n" +
            "                                }\n" +
            "                            }\n" +
            "                        },\n" +
            "                        {\n" +
            "                            \"lastUpdated\": \"2015-08-06T20:16:48.460\",\n" +
            "                            \"created\": \"2015-03-27T16:27:19.000\",\n" +
            "                            \"id\": \"LiV2YoatDud\",\n" +
            "                            \"displayInReports\": false,\n" +
            "                            \"compulsory\": false,\n" +
            "                            \"allowProvidedElsewhere\": false,\n" +
            "                            \"sortOrder\": 13,\n" +
            "                            \"allowFutureDate\": false,\n" +
            "                            \"programStage\": {\n" +
            "                                \"id\": \"ZzYYXq4fJie\"\n" +
            "                            },\n" +
            "                            \"dataElement\": {\n" +
            "                                \"code\": \"DE_2008126\",\n" +
            "                                \"created\": \"2012-09-20T16:18:26.595\",\n" +
            "                                \"lastUpdated\": \"2014-11-11T21:56:05.403\",\n" +
            "                                \"name\": \"MCH Visit Comment\",\n" +
            "                                \"id\": \"OuJ6sgPyAbC\",\n" +
            "                                \"shortName\": \"Visit Comment\",\n" +
            "                                \"displayDescription\": \"Free text comment used to put additional information for a visit.\",\n" +
            "                                \"aggregationType\": \"AVERAGE\",\n" +
            "                                \"domainType\": \"TRACKER\",\n" +
            "                                \"displayName\": \"MCH Visit Comment\",\n" +
            "                                \"description\": \"Free text comment used to put additional information for a visit.\",\n" +
            "                                \"displayFormName\": \"Visit comment (optional)\",\n" +
            "                                \"zeroIsSignificant\": false,\n" +
            "                                \"displayShortName\": \"Visit Comment\",\n" +
            "                                \"formName\": \"Visit comment (optional)\",\n" +
            "                                \"valueType\": \"LONG_TEXT\"\n" +
            "                            }\n" +
            "                        }\n" +
            "                    ],\n" +
            "                    \"programStageSections\": [ ]\n" +
            "                }\n" +
            "            ],\n" +
            "            \"programRules\": [\n" +
            "                {\n" +
            "                    \"lastUpdated\": \"2015-09-14T22:22:15.383\",\n" +
            "                    \"created\": \"2015-09-14T21:17:40.841\",\n" +
            "                    \"name\": \"Ask for comment for low apgar\",\n" +
            "                    \"id\": \"NAgjOfWMXg6\",\n" +
            "                    \"condition\": \"#{apgarscore} >= 0 && #{apgarscore} < 4 && #{apgarcomment} == ''\",\n" +
            "                    \"displayName\": \"Ask for comment for low apgar\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    },\n" +
            "                    \"programRuleActions\": [\n" +
            "                        {\n" +
            "                            \"created\": \"2015-09-14T21:17:41.033\",\n" +
            "                            \"lastUpdated\": \"2015-09-14T22:22:15.458\",\n" +
            "                            \"id\": \"v434s5YPDcP\",\n" +
            "                            \"programRuleActionType\": \"SHOWWARNING\",\n" +
            "                            \"content\": \"It is suggested that an explanation is provided when the Apgar score is below 4\",\n" +
            "                            \"dataElement\": {\n" +
            "                                \"id\": \"H6uSAMO5WLD\"\n" +
            "                            },\n" +
            "                            \"programRule\": {\n" +
            "                                \"id\": \"NAgjOfWMXg6\"\n" +
            "                            }\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"lastUpdated\": \"2015-09-14T22:25:02.149\",\n" +
            "                    \"created\": \"2015-09-14T22:20:33.429\",\n" +
            "                    \"name\": \"Demand comment if apgar is under zero\",\n" +
            "                    \"id\": \"tTPMkizzUZg\",\n" +
            "                    \"condition\": \"#{apgarscore} <0 && #{apgarcomment} == ''\",\n" +
            "                    \"displayName\": \"Demand comment if apgar is under zero\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    },\n" +
            "                    \"programRuleActions\": [\n" +
            "                        {\n" +
            "                            \"created\": \"2015-09-14T22:20:33.543\",\n" +
            "                            \"lastUpdated\": \"2015-09-14T22:25:02.200\",\n" +
            "                            \"id\": \"t944GaMzNbs\",\n" +
            "                            \"programRuleActionType\": \"SHOWERROR\",\n" +
            "                            \"content\": \"If the apgar score is below zero, an explanation must be provided.\",\n" +
            "                            \"dataElement\": {\n" +
            "                                \"id\": \"a3kGcGDCuk6\"\n" +
            "                            },\n" +
            "                            \"programRule\": {\n" +
            "                                \"id\": \"tTPMkizzUZg\"\n" +
            "                            }\n" +
            "                        }\n" +
            "                    ]\n" +
            "                },\n" +
            "                {\n" +
            "                    \"lastUpdated\": \"2015-09-14T21:25:08.933\",\n" +
            "                    \"created\": \"2015-08-07T18:41:55.082\",\n" +
            "                    \"name\": \"Hide Apgar comment if score > 7\",\n" +
            "                    \"id\": \"ppdTpuQC7Q5\",\n" +
            "                    \"condition\": \"#{apgarscore} > 7\",\n" +
            "                    \"displayName\": \"Hide Apgar comment if score > 7\",\n" +
            "                    \"program\": {\n" +
            "                        \"id\": \"IpHINAT79UW\"\n" +
            "                    },\n" +
            "                    \"programRuleActions\": [\n" +
            "                        {\n" +
            "                            \"created\": \"2015-08-07T18:41:55.210\",\n" +
            "                            \"lastUpdated\": \"2015-09-14T21:25:08.981\",\n" +
            "                            \"id\": \"iwGAWKvStTt\",\n" +
            "                            \"programRuleActionType\": \"HIDEFIELD\",\n" +
            "                            \"content\": \"\",\n" +
            "                            \"dataElement\": {\n" +
            "                                \"id\": \"H6uSAMO5WLD\"\n" +
            "                            },\n" +
            "                            \"programRule\": {\n" +
            "                                \"id\": \"ppdTpuQC7Q5\"\n" +
            "                            }\n" +
            "                        }\n" +
            "                    ]\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    ]\n" +
            "\n" +
            "}";
}
