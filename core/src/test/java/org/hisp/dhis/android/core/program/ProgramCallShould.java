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

import android.database.Cursor;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramCallShould {

    @Mock
    private ProgramService programService;

    @Mock
    private DatabaseAdapter databaseAdapter;

    @Mock
    private ProgramStore programStore;

    @Mock
    private TrackedEntityAttributeStore trackedEntityAttributeStore;

    @Mock
    private ProgramTrackedEntityAttributeStore programTrackedEntityAttributeStore;

    @Mock
    private ProgramRuleVariableStore programRuleVariableStore;

    @Mock
    private ProgramIndicatorStore programIndicatorStore;

    @Mock
    private ProgramStageSectionProgramIndicatorLinkStore programStageSectionProgramIndicatorLinkStore;

    @Mock
    private ProgramRuleActionStore programRuleActionStore;

    @Mock
    private ProgramRuleStore programRuleStore;

    @Mock
    private ProgramStageDataElementStore programStageDataElementStore;

    @Mock
    private ProgramStageSectionStore programStageSectionStore;

    @Mock
    private ProgramStageStore programStageStore;

    @Mock
    private RelationshipTypeStore relationshipStore;

    @Mock
    private ResourceStore resourceStore;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private retrofit2.Call<Payload<Program>> programCall;

    @Mock
    private DataAccess dataAccess;

    @Mock
    private Access access;

    @Mock
    private Program program;

    @Captor
    private ArgumentCaptor<Fields<Program>> fieldsCaptor;

    @Captor
    private ArgumentCaptor<Filter<Program, String>> lastUpdatedFilter;

    @Captor
    private ArgumentCaptor<Filter<Program, String>> idInFilter;

    @Mock
    private Transaction transaction;

    @Mock
    private Payload<Program> payload;

    @Mock
    private Cursor cursor;

    @Mock
    private Date serverDate;

    @Mock
    private GenericHandler<DataElement> dataElementHandler;

    // the call we are testing
    private Call<Response<Payload<Program>>> programSyncCall;


    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        Set<String> uids = new HashSet<>();
        uids.add("test_program_uid");
        uids.add("test_program1_uid");

        programSyncCall = new ProgramCall(programService, databaseAdapter,
                resourceStore, uids, programStore, serverDate, trackedEntityAttributeStore,
                programTrackedEntityAttributeStore, programRuleVariableStore, programIndicatorStore,
                programStageSectionProgramIndicatorLinkStore, programRuleActionStore, programRuleStore,
                programStageDataElementStore, programStageSectionStore, programStageStore,
                relationshipStore, dataElementHandler
        );

        when(program.uid()).thenReturn("test_program_uid");
        when(program.access()).thenReturn(access);
        when(access.data()).thenReturn(dataAccess);
        when(dataAccess.read()).thenReturn(true);
        when(dataAccess.write()).thenReturn(true);

        when(payload.items()).thenReturn(Collections.singletonList(program));


        when(databaseAdapter.query(ResourceModel.TABLE, "SELECT " + ResourceModel.Columns.LAST_SYNCED +
                " FROM " + ResourceModel.TABLE +
                " WHERE " + ResourceModel.Columns.RESOURCE_TYPE +
                " = " +
                ProgramModel.class.getSimpleName())).thenReturn(cursor);
        when(cursor.moveToFirst()).thenReturn(Boolean.FALSE);
        when(cursor.getString(cursor.getColumnIndex(ResourceModel.Columns.LAST_SYNCED))).thenReturn(null);


        when(programService.getPrograms(any(Fields.class), any(Filter.class), any(Filter.class), anyBoolean())
        ).thenReturn(programCall);

        when(databaseAdapter.beginNewTransaction()).thenReturn(transaction);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void return_correct_fields_when_invoke_server() throws Exception {
        when(programCall.execute()).thenReturn(Response.success(payload));

        when(programService.getPrograms(
                fieldsCaptor.capture(), lastUpdatedFilter.capture(), idInFilter.capture(), anyBoolean())
        ).thenReturn(programCall);


        programSyncCall.call();

        assertThat(fieldsCaptor.getValue().fields()).contains(
                Program.uid, Program.code, Program.name, Program.displayName, Program.created,
                Program.lastUpdated, Program.shortName, Program.displayShortName, Program.description,
                Program.displayDescription, Program.version, Program.captureCoordinates, Program.dataEntryMethod,
                Program.deleted, Program.displayFrontPageList, Program.displayIncidentDate,
                Program.enrollmentDateLabel, Program.ignoreOverdueEvents, Program.incidentDateLabel,
                Program.onlyEnrollOnce, Program.programType, Program.registration,
                Program.relationshipFromA, Program.relationshipText,
                Program.selectEnrollmentDatesInFuture, Program.selectIncidentDatesInFuture,
                Program.useFirstStageDuringRegistration,
                Program.relatedProgram.with(
                        Program.uid
                ),
                Program.programStages.with(
                        ProgramStage.uid, ProgramStage.code, ProgramStage.name, ProgramStage.displayName,
                        ProgramStage.created, ProgramStage.lastUpdated, ProgramStage.allowGenerateNextVisit,
                        ProgramStage.autoGenerateEvent, ProgramStage.blockEntryForm, ProgramStage.captureCoordinates,
                        ProgramStage.deleted, ProgramStage.displayGenerateEventBox, ProgramStage.executionDateLabel,
                        ProgramStage.formType, ProgramStage.generatedByEnrollmentDate, ProgramStage.hideDueDate,
                        ProgramStage.minDaysFromStart, ProgramStage.openAfterEnrollment, ProgramStage.repeatable,
                        ProgramStage.reportDateToUse, ProgramStage.sortOrder, ProgramStage.standardInterval,
                        ProgramStage.validCompleteOnly, ProgramStage.programStageDataElements.with(
                                ProgramStageDataElement.uid, ProgramStageDataElement.code,
                                ProgramStageDataElement.created, ProgramStageDataElement.lastUpdated,
                                ProgramStageDataElement.allowFutureDate,
                                ProgramStageDataElement.allowProvidedElsewhere, ProgramStageDataElement.compulsory,
                                ProgramStageDataElement.deleted, ProgramStageDataElement.displayInReports,
                                ProgramStageDataElement.sortOrder,
                                ProgramStageDataElement.programStage.with(
                                        ProgramStage.uid
                                ),
                                ProgramStageDataElement.dataElement.with(DataElement.allFields)
                        ),
                        ProgramStage.programStageSections.with(
                                ProgramStageSection.uid, ProgramStageSection.code, ProgramStageSection.name,
                                ProgramStageSection.displayName, ProgramStageSection.created,
                                ProgramStageSection.lastUpdated, ProgramStageSection.sortOrder,
                                ProgramStageSection.deleted, ProgramStageSection.dataElements.with(DataElement.uid),
                                ProgramStageSection.programIndicators.with(
                                        ProgramIndicator.uid,
                                        ProgramIndicator.program.with(
                                                Program.uid
                                        )
                                )
                        )
                ),
                Program.programRules.with(
                        ProgramRule.uid, ProgramRule.code, ProgramRule.name, ProgramRule.displayName,
                        ProgramRule.created, ProgramRule.lastUpdated, ProgramRule.deleted,
                        ProgramRule.priority, ProgramRule.condition,
                        ProgramRule.program.with(
                                Program.uid
                        ),
                        ProgramRule.programStage.with(
                                ProgramStage.uid
                        ),
                        ProgramRule.programRuleActions.with(
                                ProgramRuleAction.uid, ProgramRuleAction.code, ProgramRuleAction.name,
                                ProgramRuleAction.displayName, ProgramRuleAction.created,
                                ProgramRuleAction.lastUpdated, ProgramRuleAction.content, ProgramRuleAction.data,
                                ProgramRuleAction.deleted, ProgramRuleAction.location,
                                ProgramRuleAction.programRuleActionType,
                                ProgramRuleAction.programRule.with(
                                        ProgramRule.uid
                                ),
                                ProgramRuleAction.dataElement.with(
                                        DataElement.uid
                                ),
                                ProgramRuleAction.programIndicator.with(
                                        ProgramIndicator.uid
                                ),
                                ProgramRuleAction.programStage.with(
                                        ProgramStage.uid
                                ),
                                ProgramRuleAction.programStageSection.with(
                                        ProgramStageSection.uid
                                ),
                                ProgramRuleAction.trackedEntityAttribute.with(
                                        TrackedEntityAttribute.uid
                                )
                        )
                ),
                Program.programRuleVariables.with(
                        ProgramRuleVariable.uid, ProgramRuleVariable.code, ProgramRuleVariable.name,
                        ProgramRuleVariable.displayName, ProgramRuleVariable.created, ProgramRuleVariable.lastUpdated,
                        ProgramRuleVariable.deleted, ProgramRuleVariable.programRuleVariableSourceType,
                        ProgramRuleVariable.useCodeForOptionSet,
                        ProgramRuleVariable.program.with(
                                Program.uid
                        ),
                        ProgramRuleVariable.dataElement.with(
                                DataElement.uid
                        ),
                        ProgramRuleVariable.programStage.with(
                                ProgramStage.uid
                        ),
                        ProgramRuleVariable.trackedEntityAttribute.with(
                                TrackedEntityAttribute.uid
                        )
                ),
                Program.programIndicators.with(
                        ProgramIndicator.uid, ProgramIndicator.code, ProgramIndicator.name,
                        ProgramIndicator.displayName, ProgramIndicator.created,
                        ProgramIndicator.lastUpdated, ProgramIndicator.shortName,
                        ProgramIndicator.displayShortName, ProgramIndicator.description,
                        ProgramIndicator.displayDescription, ProgramIndicator.decimals,
                        ProgramIndicator.deleted, ProgramIndicator.dimensionItem,
                        ProgramIndicator.displayInForm,
                        ProgramIndicator.expression, ProgramIndicator.filter, ProgramIndicator.program.with(
                                Program.uid
                        )
                ),
                Program.programTrackedEntityAttributes.with(
                        ProgramTrackedEntityAttribute.uid, ProgramTrackedEntityAttribute.code,
                        ProgramTrackedEntityAttribute.name, ProgramTrackedEntityAttribute.displayName,
                        ProgramTrackedEntityAttribute.created, ProgramTrackedEntityAttribute.lastUpdated,
                        ProgramTrackedEntityAttribute.shortName, ProgramTrackedEntityAttribute.displayShortName,
                        ProgramTrackedEntityAttribute.description, ProgramTrackedEntityAttribute.displayDescription,
                        ProgramTrackedEntityAttribute.allowFutureDate, ProgramTrackedEntityAttribute.deleted,
                        ProgramTrackedEntityAttribute.displayInList, ProgramTrackedEntityAttribute.mandatory,
                        ProgramTrackedEntityAttribute.program.with(
                                Program.uid
                        ),
                        ProgramTrackedEntityAttribute.trackedEntityAttribute.with(
                                TrackedEntityAttribute.uid, TrackedEntityAttribute.code,
                                TrackedEntityAttribute.created, TrackedEntityAttribute.lastUpdated,
                                TrackedEntityAttribute.name, TrackedEntityAttribute.displayName,
                                TrackedEntityAttribute.shortName, TrackedEntityAttribute.displayShortName,
                                TrackedEntityAttribute.description, TrackedEntityAttribute.displayDescription,
                                TrackedEntityAttribute.displayInListNoProgram,
                                TrackedEntityAttribute.displayOnVisitSchedule, TrackedEntityAttribute.expression,
                                TrackedEntityAttribute.generated, TrackedEntityAttribute.inherit,
                                TrackedEntityAttribute.orgUnitScope, TrackedEntityAttribute.programScope,
                                TrackedEntityAttribute.pattern, TrackedEntityAttribute.sortOrderInListNoProgram,
                                TrackedEntityAttribute.unique, TrackedEntityAttribute.valueType,
                                TrackedEntityAttribute.searchScope, TrackedEntityAttribute.optionSet.with(
                                        OptionSet.uid, OptionSet.version
                                )

                        )
                ),
                Program.trackedEntity.with(
                        TrackedEntity.uid
                ),
                Program.categoryCombo.with(
                        CategoryCombo.uid
                ),

                Program.relationshipType.with(
                        RelationshipType.uid, RelationshipType.code, RelationshipType.name,
                        RelationshipType.displayName, RelationshipType.created, RelationshipType.lastUpdated,
                        RelationshipType.aIsToB, RelationshipType.bIsToA, RelationshipType.deleted
                )
        );
    }

    @Test
    public void not_invoke_program_store_if_request_fail() throws Exception {
        when(programCall.execute()).thenReturn(Response.<Payload<Program>>error(HttpURLConnection.HTTP_UNAUTHORIZED,
                ResponseBody.create(MediaType.parse("application/json"), "{}")));

        Response<Payload<Program>> response = programSyncCall.call();

        assertThat(response.code()).isEqualTo(HttpURLConnection.HTTP_UNAUTHORIZED);

        // verify that no transactions is created
        verify(databaseAdapter, never()).beginNewTransaction();
        verify(transaction, never()).setSuccessful();
        verify(transaction, never()).end();

        // verify that program store is never called
        verify(programStore, never()).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyInt(),
                anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                any(ProgramType.class), anyString(), anyString(), anyString(), anyString(), anyString(), anyBoolean());

        verify(programStore, never()).update(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyInt(),
                anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                any(ProgramType.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyBoolean(), anyString());

        verify(programStore, never()).delete(anyString());

        verify(resourceStore, never()).insert(anyString(), any(Date.class));
        verify(resourceStore, never()).update(anyString(), any(Date.class), anyString());
        verify(resourceStore, never()).delete(anyString());
    }

    @Test
    public void invoke_program_handler_and_update_resource_into_table_if_request_succeeds() throws Exception {
        when(programCall.execute()).thenReturn(Response.success(payload));
        when(payload.items()).thenReturn(Arrays.asList(program, program, program));
        when(resourceStore.update(anyString(), any(Date.class), anyString())).thenReturn(1);

        programSyncCall.call();

        // verify that transactions is created also in the correct order
        verify(databaseAdapter, times(1)).beginNewTransaction();
        InOrder transactionMethodsOrder = inOrder(transaction);
        transactionMethodsOrder.verify(transaction, times(1)).setSuccessful();
        transactionMethodsOrder.verify(transaction, times(1)).end();

        // assert that payload contains 3 times and all is handled by ProgramHandler
        assertThat(payload.items().size()).isEqualTo(3);

        verify(programStore, times(3)).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyInt(),
                anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                any(ProgramType.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyBoolean());

        verify(resourceStore, times(1)).update(anyString(), any(Date.class), anyString());

        // verify that nothing is inserted into resourceStore
        verify(resourceStore, never()).insert(anyString(), any(Date.class));
    }

    @Test
    public void invoke_program_handler_and_insert_resource_into_table_if_request_succeeds() throws Exception {
        when(programCall.execute()).thenReturn(Response.success(payload));
        when(payload.items()).thenReturn(Arrays.asList(program, program, program));
        when(resourceStore.update(anyString(), any(Date.class), anyString())).thenReturn(0);

        programSyncCall.call();

        // verify that transactions is created also in the correct order
        verify(databaseAdapter, times(1)).beginNewTransaction();
        InOrder transactionMethodsOrder = inOrder(transaction);
        transactionMethodsOrder.verify(transaction, times(1)).setSuccessful();
        transactionMethodsOrder.verify(transaction, times(1)).end();

        // assert that payload contains 3 times and all is handled by ProgramHandler
        assertThat(payload.items().size()).isEqualTo(3);

        // verify that insert is called 3 times in program store
        verify(programStore, times(3)).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyInt(),
                anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                any(ProgramType.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyBoolean());

        // we need to verify that resource store is invoked with update since we update before we insert
        verify(resourceStore, times(1)).update(anyString(), any(Date.class), anyString());

        // check that insert is called once
        verify(resourceStore, times(1)).insert(anyString(), any(Date.class));
    }

    @Test
    public void invoke_program_handler_if_last_synced_program_is_not_null() throws Exception {
        when(cursor.moveToFirst()).thenReturn(Boolean.TRUE);
        when(cursor.getString(anyInt())).thenReturn("2017-02-09");
        when(programCall.execute()).thenReturn(Response.success(payload));

        programSyncCall.call();

        // verify that transactions is created also in the correct order

        verify(databaseAdapter, times(1)).beginNewTransaction();
        InOrder transactionMethodsOrder = inOrder(transaction);
        transactionMethodsOrder.verify(transaction, times(1)).setSuccessful();
        transactionMethodsOrder.verify(transaction, times(1)).end();

        // cursor.getString is also getting called if insert and update into resource store is invoked
        verify(cursor, atLeastOnce()).getString(cursor.getColumnIndex(ResourceModel.Columns.LAST_SYNCED));


        // only 1 program in payload (See setUp method)
        assertThat(payload.items().size()).isEqualTo(1);

        // verify that insert is called once in program store
        verify(programStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(), any(Date.class),
                any(Date.class), anyString(), anyString(), anyString(), anyString(), anyInt(),
                anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                any(ProgramType.class), anyString(), anyString(), anyString(), anyString(), anyString(),
                anyBoolean());
    }

    @Test
    public void mark_call_as_executed_on_success() throws Exception {
        when(programCall.execute()).thenReturn(Response.success(payload));
        programSyncCall.call();

        assertThat(programSyncCall.isExecuted()).isTrue();
    }


    @Test
    public void throw_exception_when_executing_consecutive_call() throws Exception {
        when(programCall.execute()).thenReturn(Response.success(payload));
        programSyncCall.call();

        try {
            programSyncCall.call();
            fail("Invoking the programSyncCall multiple times should throw exception");
        } catch (Exception ex) {
            // do nothing
        }
    }
    @Test
    @SuppressWarnings("unchecked")
    public void throw_io_exception_when_call_is_executed() throws Exception {
        when(programCall.execute()).thenThrow(IOException.class);

        try {
            programSyncCall.call();
        } catch (IOException ioe) {
            // do nothing
        }

        assertThat(programSyncCall.isExecuted()).isTrue();

    }
}
