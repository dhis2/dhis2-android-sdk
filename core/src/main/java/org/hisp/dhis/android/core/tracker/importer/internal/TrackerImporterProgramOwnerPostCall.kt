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
package org.hisp.dhis.android.core.tracker.importer.internal

import dagger.Reusable
import io.reactivex.Observable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwner
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerPostCall
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramOwnerTableInfo

@Reusable
internal class TrackerImporterProgramOwnerPostCall @Inject constructor(
    private val programOwnerPostCall: ProgramOwnerPostCall,
    private val programOwnerStore: ObjectWithoutUidStore<ProgramOwner>,
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore
) {

    fun uploadProgramOwners(
        programOwners: Map<String, List<ProgramOwner>>,
        onlyExistingTeis: Boolean = false
    ): Observable<D2Progress> {
        return Observable.create { emitter ->
            programOwners.forEach { (tei, programOwners) ->
                programOwners
                    .filter {
                        val unsyncedOwner by lazy { selectWhere(it)?.syncState() != State.SYNCED }
                        val existingTei by lazy {
                            trackedEntityInstanceStore.selectByUid(tei)?.syncState() != State.TO_POST
                        }

                        unsyncedOwner && (!onlyExistingTeis || existingTei)
                    }.forEach {
                        programOwnerPostCall.uploadProgramOwner(it)
                    }
            }

            emitter.onComplete()
        }
    }

    private fun selectWhere(o: ProgramOwner): ProgramOwner? {
        val selectWhere = WhereClauseBuilder()
            .appendKeyStringValue(ProgramOwnerTableInfo.Columns.PROGRAM, o.program())
            .appendKeyStringValue(ProgramOwnerTableInfo.Columns.TRACKED_ENTITY_INSTANCE, o.trackedEntityInstance())
            .appendKeyStringValue(ProgramOwnerTableInfo.Columns.OWNER_ORGUNIT, o.ownerOrgUnit())
            .build()

        return programOwnerStore.selectOneWhere(selectWhere)
    }
}
