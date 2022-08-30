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
package org.hisp.dhis.android.core.trackedentity.internal

import dagger.Reusable
import io.reactivex.Completable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitModuleDownloader
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

@Reusable
internal class TrackedEntityInstancePersistenceCallFactory @Inject constructor(
    private val trackedEntityInstanceHandler: TrackedEntityInstanceHandler,
    private val uidsHelper: TrackedEntityInstanceUidHelper,
    private val organisationUnitDownloader: OrganisationUnitModuleDownloader
) {
    fun persistTEIs(
        trackedEntityInstances: List<TrackedEntityInstance>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives
    ): Completable {
        return persistTEIsInternal(trackedEntityInstances, params, relatives)
    }

    fun persistRelationships(trackedEntityInstances: List<TrackedEntityInstance>): Completable {
        val params = IdentifiableDataHandlerParams(
            hasAllAttributes = false,
            overwrite = false,
            asRelationship = true
        )
        return persistTEIsInternal(trackedEntityInstances, params, relatives = null)
    }

    private fun persistTEIsInternal(
        trackedEntityInstances: List<TrackedEntityInstance>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives?
    ): Completable {
        return Completable.defer {
            trackedEntityInstanceHandler.handleMany(
                trackedEntityInstances, params, relatives
            )
            if (uidsHelper.hasMissingOrganisationUnitUids(trackedEntityInstances)) {
                organisationUnitDownloader.refreshOrganisationUnits()
            } else {
                Completable.complete()
            }
        }
    }
}
