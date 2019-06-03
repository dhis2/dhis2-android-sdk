/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.SyncHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectStyleHandler;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import java.util.Collection;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class ProgramStageHandler extends IdentifiableSyncHandlerImpl<ProgramStage> {
    private final SyncHandlerWithTransformer<ProgramStageSection> programStageSectionHandler;
    private final SyncHandler<ProgramStageDataElement> programStageDataElementHandler;
    private final ObjectStyleHandler styleHandler;
    private final OrphanCleaner<ProgramStage, ProgramStageDataElement> programStageDataElementCleaner;
    private final OrphanCleaner<ProgramStage, ProgramStageSection> programStageSectionCleaner;
    private final CollectionCleaner<ProgramStage> collectionCleaner;
    private final DHISVersionManager versionManager;

    @Inject
    ProgramStageHandler(IdentifiableObjectStore<ProgramStage> programStageStore,
                        SyncHandlerWithTransformer<ProgramStageSection> programStageSectionHandler,
                        SyncHandler<ProgramStageDataElement> programStageDataElementHandler,
                        ObjectStyleHandler styleHandler,
                        OrphanCleaner<ProgramStage, ProgramStageDataElement> programStageDataElementCleaner,
                        OrphanCleaner<ProgramStage, ProgramStageSection> programStageSectionCleaner,
                        CollectionCleaner<ProgramStage> collectionCleaner,
                        DHISVersionManager versionManager) {
        super(programStageStore);
        this.programStageSectionHandler = programStageSectionHandler;
        this.programStageDataElementHandler = programStageDataElementHandler;
        this.styleHandler = styleHandler;
        this.programStageDataElementCleaner = programStageDataElementCleaner;
        this.programStageSectionCleaner = programStageSectionCleaner;
        this.collectionCleaner = collectionCleaner;
        this.versionManager = versionManager;
    }

    @Override
    protected ProgramStage beforeObjectHandled(ProgramStage programStage) {
        ProgramStage adaptedProgramStage;
        ProgramStage.Builder builder = programStage.toBuilder();
        if (versionManager.is2_29()) {
            adaptedProgramStage = programStage.captureCoordinates() ? builder.featureType(FeatureType.POINT).build() :
                    builder.featureType(FeatureType.NONE).build();
        } else {
            if (programStage.featureType() == null) {
                adaptedProgramStage = builder.captureCoordinates(false).featureType(FeatureType.NONE).build();
            } else {
                adaptedProgramStage = builder.captureCoordinates(
                        programStage.featureType() != FeatureType.NONE).build();
            }
        }
        return adaptedProgramStage;
    }

    @Override
    protected void afterObjectHandled(final ProgramStage programStage, HandleAction action) {

        programStageDataElementHandler.handleMany(programStage.programStageDataElements());

        programStageSectionHandler.handleMany(programStage.programStageSections(),
                programStageSection -> programStageSection.toBuilder()
                        .programStage(ObjectWithUid.create(programStage.uid()))
                        .build());

        styleHandler.handle(programStage.style(), programStage.uid(), ProgramStageTableInfo.TABLE_INFO.name());

        if (action == HandleAction.Update) {
            programStageDataElementCleaner.deleteOrphan(programStage, programStage.programStageDataElements());
            programStageSectionCleaner.deleteOrphan(programStage, programStage.programStageSections());
        }
    }

    @Override
    protected void afterCollectionHandled(Collection<ProgramStage> programStages) {
        collectionCleaner.deleteNotPresent(programStages);
    }
}