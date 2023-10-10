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
package org.hisp.dhis.android.core.trackedentity.ownership

import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.koin.core.annotation.Singleton

@Singleton
internal class ProgramOwnerPostCall(
    private val ownershipService: OwnershipService,
    private val coroutineAPICallExecutor: CoroutineAPICallExecutor,
    private val programOwnerStore: ProgramOwnerStore,
    private val dataStatePropagator: DataStatePropagator,
) {

    suspend fun uploadProgramOwner(programOwner: ProgramOwner) {
        val response = coroutineAPICallExecutor.wrap(storeError = true) {
            ownershipService.transfer(
                programOwner.trackedEntityInstance(),
                programOwner.program(),
                programOwner.ownerOrgUnit(),
            )
        }

        response.fold(
            onSuccess = { messageResponse ->
                @Suppress("MagicNumber")
                if (messageResponse.httpStatusCode() == 200) {
                    val syncedProgramOwner = programOwner.toBuilder().syncState(State.SYNCED).build()
                    programOwnerStore.updateOrInsertWhere(syncedProgramOwner)
                    dataStatePropagator.refreshTrackedEntityInstanceAggregatedSyncState(
                        programOwner.trackedEntityInstance(),
                    )
                }
            },
            onFailure = {
                // TODO Create a record in TEI
            },
        )
    }
}
