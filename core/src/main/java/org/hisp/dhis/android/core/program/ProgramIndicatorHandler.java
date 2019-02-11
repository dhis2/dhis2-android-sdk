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

import org.hisp.dhis.android.core.arch.handlers.IdentifiableSyncHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.LinkSyncHandler;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLink;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
final class ProgramIndicatorHandler extends IdentifiableSyncHandlerImpl<ProgramIndicator> {
    private final SyncHandler<LegendSet> legendSetHandler;
    private final LinkSyncHandler<ProgramIndicatorLegendSetLink> programIndicatorLegendSetLinkHandler;

    @Inject
    ProgramIndicatorHandler(IdentifiableObjectStore<ProgramIndicator> programIndicatorStore,
                            SyncHandler<LegendSet> legendSetHandler,
                            LinkSyncHandler<ProgramIndicatorLegendSetLink> programIndicatorLegendSetLinkHandler) {
        super(programIndicatorStore);
        this.legendSetHandler = legendSetHandler;
        this.programIndicatorLegendSetLinkHandler = programIndicatorLegendSetLinkHandler;
    }

    @Override
    protected void afterObjectHandled(ProgramIndicator programIndicator, HandleAction action) {
        legendSetHandler.handleMany(programIndicator.legendSets());

        List<ProgramIndicatorLegendSetLink> programIndicatorLegendSetLinks = new ArrayList<>();
        for (LegendSet legendSet : programIndicator.legendSets()) {
            programIndicatorLegendSetLinks.add(ProgramIndicatorLegendSetLink.builder()
                    .programIndicator(programIndicator.uid()).legendSet(legendSet.uid()).build());
        }
        programIndicatorLegendSetLinkHandler.handleMany(programIndicator.uid(), programIndicatorLegendSetLinks);
    }
}