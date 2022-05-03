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
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.attribute.Attribute;
import org.hisp.dhis.android.core.attribute.AttributeValueUtils;
import org.hisp.dhis.android.core.attribute.ProgramAttributeValueLink;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramInternalAccessor;
import org.hisp.dhis.android.core.program.ProgramRuleVariable;
import org.hisp.dhis.android.core.program.ProgramSection;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class ProgramHandler extends IdentifiableHandlerImpl<Program> {

    private final Handler<ProgramRuleVariable> programRuleVariableHandler;
    private final Handler<ProgramTrackedEntityAttribute> programTrackedEntityAttributeHandler;
    private final Handler<ProgramSection> programSectionHandler;
    private final ParentOrphanCleaner<Program> orphanCleaner;
    private final CollectionCleaner<Program> collectionCleaner;
    private final LinkCleaner<Program> linkCleaner;

    private final Handler<Attribute> attributeHandler;
    private final LinkHandler<Attribute, ProgramAttributeValueLink>
            programAttributeLinkHandler;


    @Inject
    ProgramHandler(ProgramStoreInterface programStore,
                   Handler<ProgramRuleVariable> programRuleVariableHandler,
                   Handler<ProgramTrackedEntityAttribute> programTrackedEntityAttributeHandler,
                   Handler<ProgramSection> programSectionHandler,
                   ParentOrphanCleaner<Program> orphanCleaner,
                   CollectionCleaner<Program> collectionCleaner,
                   LinkCleaner<Program> linkCleaner,
                   Handler<Attribute> attributeHandler,
                   LinkHandler<Attribute, ProgramAttributeValueLink> programAttributeLinkHandler) {
        super(programStore);
        this.programRuleVariableHandler = programRuleVariableHandler;
        this.programTrackedEntityAttributeHandler = programTrackedEntityAttributeHandler;
        this.programSectionHandler = programSectionHandler;
        this.orphanCleaner = orphanCleaner;
        this.collectionCleaner = collectionCleaner;
        this.linkCleaner = linkCleaner;
        this.attributeHandler = attributeHandler;
        this.programAttributeLinkHandler = programAttributeLinkHandler;
    }

    @Override
    protected void afterObjectHandled(Program program, HandleAction action) {
        programTrackedEntityAttributeHandler.handleMany(ProgramInternalAccessor
                .accessProgramTrackedEntityAttributes(program));
        programRuleVariableHandler.handleMany(ProgramInternalAccessor.accessProgramRuleVariables(program));
        programSectionHandler.handleMany(ProgramInternalAccessor.accessProgramSections(program));

        if (action == HandleAction.Update) {
            orphanCleaner.deleteOrphan(program);
        }

        if (program.attributeValues() != null) {
            final List<Attribute> attributes = AttributeValueUtils.extractAttributes(program.attributeValues());

            attributeHandler.handleMany(attributes);

            programAttributeLinkHandler.handleMany(program.uid(), attributes,
                    attribute -> ProgramAttributeValueLink.builder()
                            .program(program.uid())
                            .attribute(attribute.uid())
                            .value(AttributeValueUtils.extractValue(program.attributeValues(), attribute.uid()))
                            .build());
        }

    }

    @Override
    protected Collection<Program> beforeCollectionHandled(Collection<Program> programs) {
        List<Program> filteredPrograms = new ArrayList<>(programs.size());
        for (Program p: programs) {
            if (!(p.programType() == ProgramType.WITH_REGISTRATION && p.trackedEntityType() == null)) {
                filteredPrograms.add(p);
            }
        }
        return filteredPrograms;
    }

    @Override
    protected void afterCollectionHandled(Collection<Program> programs) {
        collectionCleaner.deleteNotPresent(programs);
        linkCleaner.deleteNotPresent(programs);
    }
}