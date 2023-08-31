/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.program.internal

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLink
import org.hisp.dhis.android.core.legendset.internal.ProgramIndicatorLegendSetLinkHandler
import org.hisp.dhis.android.core.program.ProgramIndicator
import javax.inject.Inject

@Reusable
internal class ProgramIndicatorHandler @Inject constructor(
    private val programIndicatorStore: ProgramIndicatorStore,
    private val programIndicatorLegendSetLinkHandler: ProgramIndicatorLegendSetLinkHandler,
    private val analyticsPeriodBoundaryHandler: AnalyticsPeriodBoundaryHandler,
) : IdentifiableHandlerImpl<ProgramIndicator>(programIndicatorStore) {

    override fun afterCollectionHandled(oCollection: Collection<ProgramIndicator>?) {
        val inDbProgramIndicatorUids = programIndicatorStore.selectUids()
        val apiProgramIndicatorUids = oCollection?.map(ProgramIndicator::uid)
        val deleteProgramIndicatorUid = inDbProgramIndicatorUids.filter { inDbProgramIndicatorUid ->
            val isPresentOnline = apiProgramIndicatorUids?.contains(inDbProgramIndicatorUid)
            isPresentOnline == false
        }

        if (deleteProgramIndicatorUid.isNotEmpty()) {
            val query = WhereClauseBuilder()
                .appendInKeyStringValues(IdentifiableColumns.UID, deleteProgramIndicatorUid)
            if (!query.isEmpty) {
                programIndicatorStore.deleteWhere(query.build())
            }
        }
    }

    override fun afterObjectHandled(o: ProgramIndicator, action: HandleAction) {
        programIndicatorLegendSetLinkHandler.handleMany(o.uid(), o.legendSets()) { legendSet, sortOrder ->
            ProgramIndicatorLegendSetLink.builder()
                .programIndicator(o.uid())
                .legendSet(legendSet.uid())
                .sortOrder(sortOrder)
                .build()
        }
        analyticsPeriodBoundaryHandler.handleMany(o.uid(), o.analyticsPeriodBoundaries() ?: emptyList()) { b ->
            b.toBuilder().programIndicator(o.uid()).build()
        }
    }
}
