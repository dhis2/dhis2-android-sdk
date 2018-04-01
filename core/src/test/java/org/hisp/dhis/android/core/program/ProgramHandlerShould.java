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

import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleModel;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramHandlerShould {

    @Mock
    private ProgramStore programStore;

    @Mock
    private ProgramRuleVariableHandler programRuleVariableHandler;

    @Mock
    private GenericHandler<ProgramStage, ProgramStageModel> programStageHandler;

    @Mock
    private ProgramIndicatorHandler programIndicatorHandler;

    @Mock
    private ProgramRuleHandler programRuleHandler;

    @Mock
    private ProgramTrackedEntityAttributeHandler programTrackedEntityAttributeHandler;

    @Mock
    private Program program;

    @Mock
    private RelationshipType relationshipType;

    @Mock
    private DataAccess dataAccess;

    @Mock
    private Access access;

    @Mock
    private List<ObjectWithUid> programStages;

    @Mock
    private Program relatedProgram;

    @Mock
    private TrackedEntity trackedEntity;

    @Mock
    private GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler;

    @Mock
    private RelationshipTypeHandler relationshipTypeHandler;

    // object to test
    private ProgramHandler programHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programHandler = new ProgramHandler(
                programStore, programRuleVariableHandler, programIndicatorHandler, programRuleHandler,
                programTrackedEntityAttributeHandler, relationshipTypeHandler, styleHandler);
        when(relationshipType.uid()).thenReturn("relationshipTypeUid");

        when(program.uid()).thenReturn("test_program_uid");
        when(program.code()).thenReturn("test_program_code");
        when(program.name()).thenReturn("test_program_name");
        when(program.displayName()).thenReturn("test_program_display_name");
        when(program.shortName()).thenReturn("test_program");
        when(program.displayShortName()).thenReturn("test_program");
        when(program.description()).thenReturn("A test program for the integration tests.");
        when(program.displayDescription()).thenReturn("A test program for the integration tests.");

        //ProgramModel attributes:
        when(program.version()).thenReturn(1);
        when(program.onlyEnrollOnce()).thenReturn(true);
        when(program.enrollmentDateLabel()).thenReturn("enrollment date");
        when(program.displayIncidentDate()).thenReturn(true);
        when(program.incidentDateLabel()).thenReturn("incident date label");
        when(program.registration()).thenReturn(true);
        when(program.selectEnrollmentDatesInFuture()).thenReturn(true);
        when(program.dataEntryMethod()).thenReturn(true);
        when(program.ignoreOverdueEvents()).thenReturn(false);
        when(program.relationshipFromA()).thenReturn(true);
        when(program.selectIncidentDatesInFuture()).thenReturn(true);
        when(program.captureCoordinates()).thenReturn(true);
        when(program.useFirstStageDuringRegistration()).thenReturn(true);
        when(program.displayFrontPageList()).thenReturn(true);
        when(program.programType()).thenReturn(ProgramType.WITH_REGISTRATION);
        when(program.relationshipType()).thenReturn(relationshipType);
        when(program.relationshipText()).thenReturn("test relationship");
        when(program.relatedProgram()).thenReturn(relatedProgram);
        when(program.trackedEntity()).thenReturn(trackedEntity);

        when(program.programStages()).thenReturn(programStages);
        when(program.programTrackedEntityAttributes()).thenReturn(new ArrayList<ProgramTrackedEntityAttribute>());
        when(program.programIndicators()).thenReturn(new ArrayList<ProgramIndicator>());
        when(program.programRules()).thenReturn(new ArrayList<ProgramRule>());
        when(program.programRuleVariables()).thenReturn(new ArrayList<ProgramRuleVariable>());
        when(program.access()).thenReturn(access);
        when(access.data()).thenReturn(dataAccess);
        when(dataAccess.read()).thenReturn(true);
        when(dataAccess.write()).thenReturn(true);
    }

    @Test
    public void invoke_deleted_when_handle_program_set_as_deleted() throws Exception {
        when(program.deleted()).thenReturn(true);

        programHandler.handleProgram(program);

        // check that program store is invoked with delete method
        verify(programStore, times(1)).delete(anyString());

        // check that update and insert is never called
        verify(programStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(ProgramType.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean());

        verify(programStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(ProgramType.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean(), anyString());

        // verify that all the handlers is called once
        verify(programTrackedEntityAttributeHandler, times(1)).handleProgramTrackedEntityAttributes(
                program.programTrackedEntityAttributes());
        verify(programIndicatorHandler, times(1)).handleProgramIndicator(null, program.programIndicators());
        verify(programRuleHandler, times(1)).handleProgramRules(program.programRules());
        verify(programRuleVariableHandler, times(1)).handleProgramRuleVariables(program.programRuleVariables());
    }

    @Test
    public void invoke_update_and_insert_when_handle_program_not_inserted() throws Exception {
        when(programStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(ProgramType.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean(), anyString())).thenReturn(0);

        programHandler.handleProgram(program);

        // verify that insert is called
        verify(programStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(ProgramType.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean());

        // verify that update is called since we update before we can insert
        verify(programStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(ProgramType.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean(), anyString());

        // verify that delete is never called
        verify(programStore, never()).delete(anyString());

        // verify that all the handlers is called once
        verify(programTrackedEntityAttributeHandler, times(1)).handleProgramTrackedEntityAttributes(
                program.programTrackedEntityAttributes());
        verify(programIndicatorHandler, times(1)).handleProgramIndicator(null, program.programIndicators());
        verify(programRuleHandler, times(1)).handleProgramRules(program.programRules());
        verify(programRuleVariableHandler, times(1)).handleProgramRuleVariables(program.programRuleVariables());
    }

    @Test
    public void invoke_only_update_when_handle_program_inserted() throws Exception {
        when(programStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(ProgramType.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean(), anyString())).thenReturn(1);

        programHandler.handleProgram(program);

        // verify that update is called
        verify(programStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(ProgramType.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean(), anyString());

        // check that insert and delete is never called
        verify(programStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(ProgramType.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean());

        verify(programStore, never()).delete(anyString());

        // verify that all the handlers is called once
        verify(programTrackedEntityAttributeHandler, times(1)).handleProgramTrackedEntityAttributes(
                program.programTrackedEntityAttributes());
        verify(programIndicatorHandler, times(1)).handleProgramIndicator(null, program.programIndicators());
        verify(programRuleHandler, times(1)).handleProgramRules(program.programRules());
        verify(programRuleVariableHandler, times(1)).handleProgramRuleVariables(program.programRuleVariables());
    }

    @Test
    public void do_nothing_with_null_argument() throws Exception {
        programHandler.handleProgram(null);

        // verify that programStore is never called with insert, update or delete
        verify(programStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(ProgramType.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean());

        verify(programStore, never()).delete(anyString());

        verify(programStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyInt(), anyBoolean(), anyString(), anyBoolean(), anyString(), anyBoolean(),
                anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(),
                anyBoolean(), anyBoolean(), any(ProgramType.class), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyBoolean(), anyString());

        // verify that handlers is never called
        verify(programStageHandler, never()).handleMany(anyListOf(ProgramStage.class),
                any(ProgramStageModelBuilder.class));
        verify(programTrackedEntityAttributeHandler, never()).handleProgramTrackedEntityAttributes(
                anyListOf(ProgramTrackedEntityAttribute.class)
        );
        verify(programIndicatorHandler, never()).handleProgramIndicator(anyString(), anyListOf(ProgramIndicator.class));
        verify(programRuleHandler, never()).handleProgramRules(anyListOf(ProgramRule.class));
        verify(programRuleVariableHandler, never()).handleProgramRuleVariables(anyListOf(ProgramRuleVariable.class));
    }
}
