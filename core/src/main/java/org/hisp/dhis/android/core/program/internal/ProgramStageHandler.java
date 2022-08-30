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
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.attribute.Attribute;
import org.hisp.dhis.android.core.attribute.AttributeValueUtils;
import org.hisp.dhis.android.core.attribute.ProgramStageAttributeValueLink;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageInternalAccessor;
import org.hisp.dhis.android.core.program.ProgramStageSection;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class ProgramStageHandler extends IdentifiableHandlerImpl<ProgramStage> {
    private final HandlerWithTransformer<ProgramStageSection> programStageSectionHandler;
    private final Handler<ProgramStageDataElement> programStageDataElementHandler;
    private final OrphanCleaner<ProgramStage, ProgramStageDataElement> programStageDataElementCleaner;
    private final OrphanCleaner<ProgramStage, ProgramStageSection> programStageSectionCleaner;
    private final SubCollectionCleaner<ProgramStage> programStageCleaner;
    private final Handler<Attribute> attributeHandler;
    private final LinkHandler<Attribute, ProgramStageAttributeValueLink>
            programStageAttributeValueLinkHandler;

    @Inject
    ProgramStageHandler(IdentifiableObjectStore<ProgramStage> programStageStore,
                        HandlerWithTransformer<ProgramStageSection> programStageSectionHandler,
                        Handler<ProgramStageDataElement> programStageDataElementHandler,
                        OrphanCleaner<ProgramStage, ProgramStageDataElement> programStageDataElementCleaner,
                        OrphanCleaner<ProgramStage, ProgramStageSection> programStageSectionCleaner,
                        SubCollectionCleaner<ProgramStage> programStageCleaner,
                        Handler<Attribute> attributeHandler,
                        LinkHandler<Attribute, ProgramStageAttributeValueLink> programStageAttributeValueLinkHandler) {
        super(programStageStore);
        this.programStageSectionHandler = programStageSectionHandler;
        this.programStageDataElementHandler = programStageDataElementHandler;
        this.programStageDataElementCleaner = programStageDataElementCleaner;
        this.programStageSectionCleaner = programStageSectionCleaner;
        this.programStageCleaner = programStageCleaner;
        this.attributeHandler = attributeHandler;
        this.programStageAttributeValueLinkHandler = programStageAttributeValueLinkHandler;
    }

    @Override
    protected void afterObjectHandled(final ProgramStage programStage, HandleAction action) {
        programStageDataElementHandler.handleMany(
                ProgramStageInternalAccessor.accessProgramStageDataElements(programStage));

        programStageSectionHandler.handleMany(ProgramStageInternalAccessor.accessProgramStageSections(programStage),
                programStageSection -> programStageSection.toBuilder()
                        .programStage(ObjectWithUid.create(programStage.uid()))
                        .build());

        if (action == HandleAction.Update) {
            programStageDataElementCleaner.deleteOrphan(programStage,
                    ProgramStageInternalAccessor.accessProgramStageDataElements(programStage));
            programStageSectionCleaner.deleteOrphan(programStage,
                    ProgramStageInternalAccessor.accessProgramStageSections(programStage));
        }

        if (programStage.attributeValues() != null){
            final List<Attribute> attributes = AttributeValueUtils.extractAttributes(programStage.attributeValues());

            attributeHandler.handleMany(attributes);

            programStageAttributeValueLinkHandler.handleMany(programStage.uid(), attributes,
                    attribute -> ProgramStageAttributeValueLink.builder()
                            .programStage(programStage.uid())
                            .attribute(attribute.uid())
                            .value(AttributeValueUtils.extractValue(programStage.attributeValues(), attribute.uid()))
                            .build());
        }
    }

    @Override
    protected void afterCollectionHandled(Collection<ProgramStage> programStages) {
        programStageCleaner.deleteNotPresent(programStages);
    }
}