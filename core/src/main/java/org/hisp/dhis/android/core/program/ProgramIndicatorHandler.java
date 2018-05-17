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

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.hisp.dhis.android.core.legendset.LegendSetHandler;
import org.hisp.dhis.android.core.legendset.LegendSetModel;
import org.hisp.dhis.android.core.legendset.LegendSetModelBuilder;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLinkModel;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLinkModelBuilder;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLinkStore;

import java.util.Collection;

public class ProgramIndicatorHandler extends IdentifiableHandlerImpl<ProgramIndicator, ProgramIndicatorModel> {
    private final ProgramStageSectionProgramIndicatorLinkStore programStageSectionProgramIndicatorLinkStore;
    private final ObjectWithoutUidStore<ProgramIndicatorLegendSetLinkModel> programIndicatorLegendSetLinkStore;
    private final GenericHandler<LegendSet, LegendSetModel> legendSetHandler;

    ProgramIndicatorHandler(IdentifiableObjectStore<ProgramIndicatorModel> programIndicatorStore,
                                   ProgramStageSectionProgramIndicatorLinkStore
                                           programStageSectionProgramIndicatorLinkStore,
                                   ObjectWithoutUidStore<ProgramIndicatorLegendSetLinkModel>
                                           programIndicatorLegendSetLinkStore,
                                   GenericHandler<LegendSet, LegendSetModel> legendSetHandler) {
        super(programIndicatorStore);
        this.programStageSectionProgramIndicatorLinkStore = programStageSectionProgramIndicatorLinkStore;
        this.programIndicatorLegendSetLinkStore = programIndicatorLegendSetLinkStore;
        this.legendSetHandler = legendSetHandler;
    }

    public static ProgramIndicatorHandler create(DatabaseAdapter databaseAdapter) {
        return new ProgramIndicatorHandler(
                ProgramIndicatorStore.create(databaseAdapter),
                new ProgramStageSectionProgramIndicatorLinkStoreImpl(databaseAdapter),
                ProgramIndicatorLegendSetLinkStore.create(databaseAdapter),
                LegendSetHandler.create(databaseAdapter)
        );
    }

    void handleManyWithProgramStageSection(Collection<ProgramIndicator> pCollection, ModelBuilder<ProgramIndicator,
            ProgramIndicatorModel> modelBuilder, String programStageSection) {
        super.handleMany(pCollection, modelBuilder);

        updateProgramStageSectionProgramIndicatorLink(pCollection, programStageSection);
    }


    private void updateProgramStageSectionProgramIndicatorLink(Collection<ProgramIndicator> pCollection,
                                                       String programStageSection) {
        for(ProgramIndicator programIndicator : pCollection) {
            int updatedLink = programStageSectionProgramIndicatorLinkStore.update(
                    programStageSection, programIndicator.uid(),
                    programStageSection, programIndicator.uid()
            );

            if (updatedLink <= 0) {
                programStageSectionProgramIndicatorLinkStore.insert(
                        programStageSection, programIndicator.uid()
                );
            }
        }
    }

    @Override
    protected void afterObjectPersisted(ProgramIndicator programIndicator) {
        if (programIndicator.legendSets() != null && programIndicator.legendSets().size() > 0) {
            handleLegendSet(programIndicator);
        }
    }

    private void handleLegendSet(ProgramIndicator programIndicator) {
        legendSetHandler.handleMany(programIndicator.legendSets(), new LegendSetModelBuilder());

        ProgramIndicatorLegendSetLinkModelBuilder builder =
                new ProgramIndicatorLegendSetLinkModelBuilder(programIndicator);

        for (LegendSet legendSet : programIndicator.legendSets()) {
            programIndicatorLegendSetLinkStore.updateOrInsertWhere(builder.buildModel(legendSet));
        }
    }
}
