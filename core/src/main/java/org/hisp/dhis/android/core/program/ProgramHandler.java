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
import org.hisp.dhis.android.core.common.CollectionCleaner;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.ObjectStyleHandler;
import org.hisp.dhis.android.core.common.ParentOrphanCleaner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class ProgramHandler extends IdentifiableSyncHandlerImpl<Program> {

    private final SyncHandler<ProgramRuleVariable> programRuleVariableHandler;
    private final SyncHandler<ProgramIndicator> programIndicatorHandler;
    private final SyncHandler<ProgramRule> programRuleHandler;
    private final SyncHandler<ProgramTrackedEntityAttribute> programTrackedEntityAttributeHandler;
    private final SyncHandler<ProgramSection> programSectionHandler;
    private final ObjectStyleHandler styleHandler;
    private final ParentOrphanCleaner<Program> orphanCleaner;
    private final CollectionCleaner<Program> collectionCleaner;
    private final ProgramDHISVersionManager programVersionManager;

    @Inject
    ProgramHandler(ProgramStoreInterface programStore,
                   SyncHandler<ProgramRuleVariable> programRuleVariableHandler,
                   SyncHandler<ProgramIndicator> programIndicatorHandler,
                   SyncHandler<ProgramRule> programRuleHandler,
                   SyncHandler<ProgramTrackedEntityAttribute> programTrackedEntityAttributeHandler,
                   SyncHandler<ProgramSection> programSectionHandler,
                   ObjectStyleHandler styleHandler,
                   ParentOrphanCleaner<Program> orphanCleaner,
                   CollectionCleaner<Program> collectionCleaner,
                   ProgramDHISVersionManager programVersionManager) {
        super(programStore);
        this.programRuleVariableHandler = programRuleVariableHandler;
        this.programIndicatorHandler = programIndicatorHandler;
        this.programRuleHandler = programRuleHandler;
        this.programTrackedEntityAttributeHandler = programTrackedEntityAttributeHandler;
        this.programSectionHandler = programSectionHandler;
        this.styleHandler = styleHandler;
        this.orphanCleaner = orphanCleaner;
        this.collectionCleaner = collectionCleaner;
        this.programVersionManager = programVersionManager;
    }

    @Override
    protected Program beforeObjectHandled(Program program) {
        return programVersionManager.addCaptureCoordinatesOrFeatureType(program);
    }

    @Override
    protected void afterObjectHandled(Program program, HandleAction action) {
        programTrackedEntityAttributeHandler.handleMany(program.programTrackedEntityAttributes());
        programIndicatorHandler.handleMany(program.programIndicators());
        programRuleHandler.handleMany(program.programRules());
        programRuleVariableHandler.handleMany(program.programRuleVariables());
        programSectionHandler.handleMany(program.programSections());
        styleHandler.handle(program.style(), program.uid(), ProgramTableInfo.TABLE_INFO.name());

        if (action == HandleAction.Update) {
            orphanCleaner.deleteOrphan(program);
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
    }
}