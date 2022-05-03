/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.LinkCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.ParentOrphanCleaner;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.attribute.Attribute;
import org.hisp.dhis.android.core.attribute.AttributeValue;
import org.hisp.dhis.android.core.attribute.ProgramAttributeValueLink;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramInternalAccessor;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramSection;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramHandlerShould {

    @Mock
    private ProgramStoreInterface programStore;

    @Mock
    private Handler<ProgramRuleVariable> programRuleVariableHandler;

    @Mock
    private Handler<ProgramTrackedEntityAttribute> programTrackedEntityAttributeHandler;

    @Mock
    private Handler<ProgramSection> programSectionHandler;

    @Mock
    private ParentOrphanCleaner<Program> orphanCleaner;

    @Mock
    private CollectionCleaner<Program> collectionCleaner;

    @Mock
    private LinkCleaner<Program> linkCleaner;

    @Mock
    private LinkHandler<Attribute, ProgramAttributeValueLink> programAttributeValueLinkHandler;

    @Mock
    private Handler<Attribute> attributeHandler;

    @Mock
    private Program program;

    @Mock
    private DataAccess dataAccess;

    @Mock
    private Access access;

    @Mock
    private ObjectWithUid relatedProgram;

    @Mock
    private TrackedEntityType trackedEntityType;

    @Mock
    private List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

    @Mock
    private ProgramRuleVariable programRuleVariable;

    @Mock
    private List<ProgramRuleVariable> programRuleVariables;

    @Mock
    private List<ProgramSection> programSections;

    private List<AttributeValue> attributeValues = new ArrayList<>();

    Attribute attribute;

    // object to test
    private ProgramHandler programHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programHandler = new ProgramHandler(
                programStore,
                programRuleVariableHandler,
                programTrackedEntityAttributeHandler,
                programSectionHandler,
                orphanCleaner,
                collectionCleaner,
                linkCleaner,
                attributeHandler,
                programAttributeValueLinkHandler
        );

        when(program.uid()).thenReturn("test_program_uid");
        when(program.code()).thenReturn("test_program_code");
        when(program.name()).thenReturn("test_program_name");
        when(program.displayName()).thenReturn("test_program_display_name");
        when(program.shortName()).thenReturn("test_program");
        when(program.displayShortName()).thenReturn("test_program");
        when(program.description()).thenReturn("A test program for the integration tests.");
        when(program.displayDescription()).thenReturn("A test program for the integration tests.");

        //Program attributes:
        when(program.version()).thenReturn(1);
        when(program.onlyEnrollOnce()).thenReturn(true);
        when(program.enrollmentDateLabel()).thenReturn("enrollment date");
        when(program.displayIncidentDate()).thenReturn(true);
        when(program.incidentDateLabel()).thenReturn("incident date label");
        when(program.registration()).thenReturn(true);
        when(program.selectEnrollmentDatesInFuture()).thenReturn(true);
        when(program.dataEntryMethod()).thenReturn(true);
        when(program.ignoreOverdueEvents()).thenReturn(false);
        when(program.selectIncidentDatesInFuture()).thenReturn(true);
        when(program.useFirstStageDuringRegistration()).thenReturn(true);
        when(program.displayFrontPageList()).thenReturn(true);
        when(program.programType()).thenReturn(ProgramType.WITH_REGISTRATION);
        when(program.relatedProgram()).thenReturn(relatedProgram);
        when(program.trackedEntityType()).thenReturn(trackedEntityType);

        programRuleVariables = Collections.singletonList(programRuleVariable);

        when(ProgramInternalAccessor.accessProgramTrackedEntityAttributes(program))
                .thenReturn(programTrackedEntityAttributes);
        when(ProgramInternalAccessor.accessProgramRuleVariables(program)).thenReturn(programRuleVariables);
        when(ProgramInternalAccessor.accessProgramSections(program)).thenReturn(programSections);
        when(program.access()).thenReturn(access);
        when(access.data()).thenReturn(dataAccess);
        when(dataAccess.read()).thenReturn(true);
        when(dataAccess.write()).thenReturn(true);

        attribute = Attribute.builder()
                .programAttribute(true)
                .uid("Att_Uid")
                .name("att")
                .code("att")
                .valueType(ValueType.TEXT)
                .build();

        AttributeValue attValue = AttributeValue.builder()
                .value("5")
                .attribute(attribute)
                .build();

        attributeValues.add(attValue);

        when(program.attributeValues()).thenReturn(attributeValues);
    }

    @Test
    public void call_program_tracked_entity_attributes_handler() {
        programHandler.handle(program);
        verify(programTrackedEntityAttributeHandler).handleMany(anyListOf(ProgramTrackedEntityAttribute.class));
    }

    @Test
    public void call_program_rule_variable_handler() {
        programHandler.handle(program);
        verify(programRuleVariableHandler).handleMany(programRuleVariables);
    }

    @Test
    public void call_program_section_handler() {
        programHandler.handle(program);
        verify(programSectionHandler).handleMany(anyListOf(ProgramSection.class));
    }

    @Test
    public void clean_orphan_options_after_update() {
        when(programStore.updateOrInsert(any(Program.class))).thenReturn(HandleAction.Update);
        programHandler.handle(program);
        verify(orphanCleaner).deleteOrphan(program);
    }

    @Test
    public void not_clean_orphan_options_after_insert() {
        when(programStore.updateOrInsert(any(Program.class))).thenReturn(HandleAction.Insert);
        programHandler.handle(program);
        verify(orphanCleaner, never()).deleteOrphan(program);
    }

    @Test
    public void call_collection_cleaner_when_calling_handle_many() {
        List<Program> programs = Collections.singletonList(program);
        programHandler.handleMany(programs);
        verify(collectionCleaner).deleteNotPresent(programs);
    }

    @Test
    public void not_store_tracker_program_witout_tracked_entity_type() {
        when(program.programType()).thenReturn(ProgramType.WITH_REGISTRATION);
        when(program.trackedEntityType()).thenReturn(null);
        programHandler.handleMany(Collections.singletonList(program));
        verifyNoMoreInteractions(programStore);
    }

    @Test
    public void call_attribute_handlers() {
        programHandler.handleMany(Collections.singletonList(program));
        verify(attributeHandler).handleMany(eq(Arrays.asList(attribute)));
        verify(programAttributeValueLinkHandler).handleMany(eq(program.uid()),eq(Arrays.asList(attribute)),any());
    }
}