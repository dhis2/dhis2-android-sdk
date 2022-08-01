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

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.cleaners.internal.SubCollectionCleaner;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.attribute.Attribute;
import org.hisp.dhis.android.core.attribute.ProgramStageAttributeValueLink;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.FeatureType;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageInternalAccessor;
import org.hisp.dhis.android.core.program.ProgramStageSection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramStageHandlerShould {
    @Mock
    private IdentifiableObjectStore<ProgramStage> programStageStore;

    @Mock
    private HandlerWithTransformer<ProgramStageSection> programStageSectionHandler;

    @Mock
    private Handler<ProgramStageDataElement> programStageDataElementHandler;

    @Mock
    private ProgramStage programStage;

    @Mock
    private DataAccess dataAccess;

    @Mock
    private Access access;

    @Mock
    private ObjectStyle objectStyle;

    @Mock
    private ProgramStageDataElement programStageDataElement;

    private List<ProgramStageDataElement> programStageDataElements;

    @Mock
    private ProgramStageSection programStageSection;

    private List<ProgramStageSection> programStageSections;

    @Mock
    private OrphanCleaner<ProgramStage, ProgramStageDataElement> programStageDataElementCleaner;

    @Mock
    private OrphanCleaner<ProgramStage, ProgramStageSection> programStageSectionCleaner;

    @Mock
    private SubCollectionCleaner<ProgramStage> programStageCleaner;

    @Mock
    private ProgramStage.Builder programStageBuilder;

    @Mock
    private Handler<Attribute> attributeHandler;

    @Mock
    private LinkHandler<Attribute, ProgramStageAttributeValueLink> programStageAttributeValueLinkHandler;

    // object to test
    private ProgramStageHandler programStageHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programStageHandler = new ProgramStageHandler(
                programStageStore,
                programStageSectionHandler,
                programStageDataElementHandler,
                programStageDataElementCleaner,
                programStageSectionCleaner,
                programStageCleaner,
                attributeHandler,
                programStageAttributeValueLinkHandler);

        programStageSections = new ArrayList<>();
        programStageSections.add(programStageSection);

        programStageDataElements = Collections.singletonList(programStageDataElement);

        when(programStage.uid()).thenReturn("test_program_stage_uid");
        when(programStage.style()).thenReturn(objectStyle);
        when(ProgramStageInternalAccessor.accessProgramStageDataElements(programStage))
                .thenReturn(programStageDataElements);
        when(ProgramStageInternalAccessor.accessProgramStageSections(programStage)).thenReturn(programStageSections);
        when(dataAccess.read()).thenReturn(true);
        when(access.data()).thenReturn(dataAccess);
        when(programStage.access()).thenReturn(access);

        when(programStage.toBuilder()).thenReturn(programStageBuilder);
        when(programStageBuilder.featureType(any(FeatureType.class))).thenReturn(programStageBuilder);
        when(programStageBuilder.build()).thenReturn(programStage);
    }

    @Test
    public void call_program_stage_data_element_handler() throws Exception {
        programStageHandler.handle(programStage);
        verify(programStageDataElementHandler).handleMany(programStageDataElements);
    }

    @Test
    public void call_program_stage_section_handler() throws Exception {
        programStageHandler.handle(programStage);
        verify(programStageSectionHandler).handleMany(eq(programStageSections), any());
    }

    @Test
    public void call_attribute_handler() throws Exception {
        programStageHandler.handle(programStage);
        verify(attributeHandler).handleMany( anyCollection());
    }

    @Test
    public void clean_orphan_data_elements_after_update() {
        when(programStageStore.updateOrInsert(any(ProgramStage.class))).thenReturn(HandleAction.Update);
        programStageHandler.handle(programStage);
        verify(programStageDataElementCleaner).deleteOrphan(programStage, programStageDataElements);
    }

    @Test
    public void not_clean_orphan_data_elements_after_insert() {
        when(programStageStore.updateOrInsert(any(ProgramStage.class))).thenReturn(HandleAction.Insert);
        programStageHandler.handle(programStage);
        verify(programStageDataElementCleaner, never()).deleteOrphan(programStage, programStageDataElements);
    }

    @Test
    public void clean_orphan_sections_after_update() {
        when(programStageStore.updateOrInsert(any(ProgramStage.class))).thenReturn(HandleAction.Update);
        programStageHandler.handle(programStage);
        verify(programStageSectionCleaner).deleteOrphan(programStage, programStageSections);
    }

    @Test
    public void not_clean_orphan_sections_after_insert() {
        when(programStageStore.updateOrInsert(any(ProgramStage.class))).thenReturn(HandleAction.Insert);
        programStageHandler.handle(programStage);
        verify(programStageSectionCleaner, never()).deleteOrphan(programStage, programStageSections);
    }

    @Test
    public void call_collection_cleaner_when_calling_handle_many() {
        List<ProgramStage> programStages = Collections.singletonList(programStage);
        programStageHandler.handleMany(programStages);
        verify(programStageCleaner).deleteNotPresent(programStages);
    }
}