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
import org.hisp.dhis.android.core.common.CollectionCleaner;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectStyleModel;
import org.hisp.dhis.android.core.common.ObjectStyleModelBuilder;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class ProgramStageHandlerShould {
    @Mock
    private IdentifiableObjectStore<ProgramStageModel> programStageStore;

    @Mock
    private ProgramStageSectionHandler programStageSectionHandler;

    @Mock
    private ProgramStageDataElementHandler programStageDataElementHandler;

    @Mock
    private ProgramStage programStage;

    @Mock
    private DataAccess dataAccess;

    @Mock
    private Access access;

    @Mock
    private ObjectStyle objectStyle;

    @Mock
    private List<ProgramStageDataElement> programStageDataElements;

    @Mock
    private List<ProgramStageSection> programStageSections;

    @Mock
    private GenericHandler<ObjectStyle, ObjectStyleModel> styleHandler;

    @Mock
    private OrphanCleaner<ProgramStage, ProgramStageDataElement> programStageDataElementCleaner;

    @Mock
    private OrphanCleaner<ProgramStage, ProgramStageSection> programStageSectionCleaner;

    @Mock
    private CollectionCleaner<ProgramStage> collectionCleaner;

    @Mock
    private DHISVersionManager versionManager;

    @Mock
    private ProgramStage.Builder builder;

    // object to test
    private ProgramStageHandler programStageHandler;

    private ProgramStageModelBuilder programStageModelBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programStageModelBuilder = new ProgramStageModelBuilder();

        programStageHandler = new ProgramStageHandler(
                programStageStore, programStageSectionHandler,
                programStageDataElementHandler,
                styleHandler,
                programStageDataElementCleaner,
                programStageSectionCleaner,
                collectionCleaner,
                versionManager);

        when(programStage.uid()).thenReturn("test_program_stage_uid");
        when(programStage.style()).thenReturn(objectStyle);
        when(programStage.programStageDataElements()).thenReturn(programStageDataElements);
        when(programStage.programStageSections()).thenReturn(programStageSections);
        when(dataAccess.read()).thenReturn(true);
        when(access.data()).thenReturn(dataAccess);
        when(programStage.access()).thenReturn(access);
        when(programStage.toBuilder()).thenReturn(builder);
        when(builder.featureType(any(FeatureType.class))).thenReturn(builder);
        when(builder.captureCoordinates(any(Boolean.class))).thenReturn(builder);
        when(builder.build()).thenReturn(programStage);
    }

    @Test
    public void call_program_stage_data_element_handler() throws Exception {
        programStageHandler.handle(programStage, programStageModelBuilder);
        verify(programStageDataElementHandler).handleProgramStageDataElements(
                programStageDataElements);
    }

    @Test
    public void call_program_stage_section_handler() throws Exception {
        programStageHandler.handle(programStage, programStageModelBuilder);
        verify(programStageSectionHandler).handleProgramStageSection("test_program_stage_uid",
                programStageSections);
    }

    @Test
    public void call_style_handler() throws Exception {
        programStageHandler.handle(programStage, programStageModelBuilder);
        verify(styleHandler).handle(eq(objectStyle), any(ObjectStyleModelBuilder.class));
    }

    @Test
    public void clean_orphan_data_elements_after_update() {
        when(programStageStore.updateOrInsert(any(ProgramStageModel.class))).thenReturn(HandleAction.Update);
        programStageHandler.handle(programStage, new ProgramStageModelBuilder());
        verify(programStageDataElementCleaner).deleteOrphan(programStage, programStageDataElements);
    }

    @Test
    public void not_clean_orphan_data_elements_after_insert() {
        when(programStageStore.updateOrInsert(any(ProgramStageModel.class))).thenReturn(HandleAction.Insert);
        programStageHandler.handle(programStage, new ProgramStageModelBuilder());
        verify(programStageDataElementCleaner, never()).deleteOrphan(programStage, programStageDataElements);
    }

    @Test
    public void clean_orphan_sections_after_update() {
        when(programStageStore.updateOrInsert(any(ProgramStageModel.class))).thenReturn(HandleAction.Update);
        programStageHandler.handle(programStage, new ProgramStageModelBuilder());
        verify(programStageSectionCleaner).deleteOrphan(programStage, programStageSections);
    }

    @Test
    public void not_clean_orphan_sections_after_insert() {
        when(programStageStore.updateOrInsert(any(ProgramStageModel.class))).thenReturn(HandleAction.Insert);
        programStageHandler.handle(programStage, new ProgramStageModelBuilder());
        verify(programStageSectionCleaner, never()).deleteOrphan(programStage, programStageSections);
    }

    @Test
    public void call_collection_cleaner_when_calling_handle_many() {
        List<ProgramStage> programStages = Collections.singletonList(programStage);
        programStageHandler.handleMany(programStages, new ProgramStageModelBuilder());
        verify(collectionCleaner).deleteNotPresent(programStages);
    }

    @Test
    public void save_feature_type_if_version_is_2_29() {
        when(versionManager.is2_29()).thenReturn(true);
        when(programStage.captureCoordinates()).thenReturn(true);
        when(programStage.featureType()).thenReturn(FeatureType.POINT);

        programStageHandler.handle(programStage, programStageModelBuilder);
        assertThat(programStageHandler.beforeObjectHandled(programStage).featureType()).isEqualTo(FeatureType.POINT);
    }

    @Test
    public void save_capture_coordinates_if_version_is_higher_than_2_29() {
        when(versionManager.is2_29()).thenReturn(false);
        when(programStage.featureType()).thenReturn(FeatureType.POLYGON);
        when(programStage.captureCoordinates()).thenReturn(true);

        programStageHandler.handle(programStage, programStageModelBuilder);
        assertThat(programStageHandler.beforeObjectHandled(programStage).captureCoordinates()).isTrue();
    }
}