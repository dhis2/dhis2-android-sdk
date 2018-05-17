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
import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleModel;
import org.hisp.dhis.android.core.common.ObjectStyleModelBuilder;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramHandlerShould {

    @Mock
    private IdentifiableObjectStore<ProgramModel> programStore;

    @Mock
    private ProgramRuleVariableHandler programRuleVariableHandler;

    @Mock
    private ProgramIndicatorHandler programIndicatorHandler;

    @Mock
    private ProgramRuleHandler programRuleHandler;

    @Mock
    private GenericHandler<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeModel>
            programTrackedEntityAttributeHandler;

    @Mock
    private GenericHandler<ProgramSection, ProgramSectionModel> programSectionHandler;

    @Mock
    private GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler;

    @Mock
    private Program program;

    @Mock
    private DataAccess dataAccess;

    @Mock
    private Access access;

    @Mock
    private List<ObjectWithUid> programStages;

    @Mock
    private Program relatedProgram;

    @Mock
    private TrackedEntityType trackedEntityType;

    @Mock
    private List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

    @Mock
    private List<ProgramIndicator> programIndicators;

    @Mock
    private List<ProgramRule> programRules;

    @Mock
    private List<ProgramRuleVariable> programRuleVariables;

    @Mock
    private List<ProgramSection> programSections;

    // object to test
    private ProgramHandler programHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programHandler = new ProgramHandler(
                programStore, programRuleVariableHandler, programIndicatorHandler, programRuleHandler,
                programTrackedEntityAttributeHandler, programSectionHandler, styleHandler);

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
        when(program.relationshipText()).thenReturn("test relationship");
        when(program.relatedProgram()).thenReturn(relatedProgram);
        when(program.trackedEntityType()).thenReturn(trackedEntityType);

        when(program.programStages()).thenReturn(programStages);
        when(program.programTrackedEntityAttributes()).thenReturn(programTrackedEntityAttributes);
        when(program.programIndicators()).thenReturn(programIndicators);
        when(program.programRules()).thenReturn(programRules);
        when(program.programRuleVariables()).thenReturn(programRuleVariables);
        when(program.programSections()).thenReturn(programSections);
        when(program.access()).thenReturn(access);
        when(access.data()).thenReturn(dataAccess);
        when(dataAccess.read()).thenReturn(true);
        when(dataAccess.write()).thenReturn(true);
    }

    @Test
    public void call_program_tracked_entity_attributes_handler() throws Exception {
        programHandler.handle(program, new ProgramModelBuilder());
        verify(programTrackedEntityAttributeHandler).handleMany(anyListOf(ProgramTrackedEntityAttribute.class),
                any(ProgramTrackedEntityAttributeModelBuilder.class));
    }

    @Test
    public void call_program_indicator_handler() throws Exception {
        programHandler.handle(program, new ProgramModelBuilder());
        verify(programIndicatorHandler).handleProgramIndicator(null, programIndicators);
    }

    @Test
    public void call_program_rule_handler() throws Exception {
        programHandler.handle(program, new ProgramModelBuilder());
        verify(programRuleHandler).handleProgramRules(programRules);
    }

    @Test
    public void call_program_rule_variable_handler() throws Exception {
        programHandler.handle(program, new ProgramModelBuilder());
        verify(programRuleVariableHandler).handleProgramRuleVariables(programRuleVariables);
    }

    @Test
    public void call_style_handler() throws Exception {
        programHandler.handle(program, new ProgramModelBuilder());
        verify(styleHandler).handle(same(program.style()), any(ObjectStyleModelBuilder.class));
    }

    @Test
    public void call_program_section_handler() throws Exception {
        programHandler.handle(program, new ProgramModelBuilder());
        verify(programSectionHandler).handleMany(anyListOf(ProgramSection.class),
                any(ProgramSectionModelBuilder.class));
    }
}
