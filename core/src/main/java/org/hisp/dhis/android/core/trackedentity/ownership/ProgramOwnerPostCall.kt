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
package org.hisp.dhis.android.core.trackedentity.ownership

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStore
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.maintenance.D2Error

@Reusable
internal class ProgramOwnerPostCall @Inject constructor(
    private val ownershipService: OwnershipService,
    private val apiCallExecutor: APICallExecutor,
    private val programOwnerStore: ObjectWithoutUidStore<ProgramOwner>,
    private val dataStatePropagator: DataStatePropagator
) {

    fun uploadProgramOwner(programOwner: ProgramOwner): Boolean {
        return try {
            val response = apiCallExecutor.executeObjectCall(
                ownershipService.transfer(
                    programOwner.trackedEntityInstance(),
                    programOwner.program(),
                    programOwner.ownerOrgUnit()
                )
            )

            @Suppress("MagicNumber")
            val isSuccessful = response.httpStatusCode() == 200

            if (isSuccessful) {
                val syncedProgramOwner = programOwner.toBuilder().syncState(State.SYNCED).build()
                programOwnerStore.updateOrInsertWhere(syncedProgramOwner)
                dataStatePropagator.refreshTrackedEntityInstanceAggregatedSyncState(
                    programOwner.trackedEntityInstance()
                )
            }
            isSuccessful
        } catch (e: D2Error) {
            // TODO Create a record in TEI
            false
        }
    }
}
