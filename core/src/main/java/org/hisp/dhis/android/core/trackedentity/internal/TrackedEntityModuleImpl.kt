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
import javax.inject.Inject
import org.hisp.dhis.android.core.trackedentity.*
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService
import org.hisp.dhis.android.core.trackedentity.ownership.OwnershipManager
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryCollectionRepository

@Reusable
@Suppress("LongParameterList", "TooManyFunctions")
internal class TrackedEntityModuleImpl @Inject constructor(
    private val trackedEntityTypes: TrackedEntityTypeCollectionRepository,
    private val trackedEntityInstances: TrackedEntityInstanceCollectionRepository,
    private val trackedEntityDataValues: TrackedEntityDataValueCollectionRepository,
    private val trackedEntityAttributeValues: TrackedEntityAttributeValueCollectionRepository,
    private val trackedEntityAttributes: TrackedEntityAttributeCollectionRepository,
    private val trackedEntityTypeAttributes: TrackedEntityTypeAttributeCollectionRepository,
    private val trackedEntityInstanceFilters: TrackedEntityInstanceFilterCollectionRepository,
    private val reservedValueManager: TrackedEntityAttributeReservedValueManager,
    private val trackedEntityInstanceDownloader: TrackedEntityInstanceDownloader,
    private val trackedEntityInstanceQuery: TrackedEntityInstanceQueryCollectionRepository,
    private val trackedEntityInstanceService: TrackedEntityInstanceService,
    private val ownershipManager: OwnershipManager
) : TrackedEntityModule {

    override fun trackedEntityTypes(): TrackedEntityTypeCollectionRepository {
        return trackedEntityTypes
    }

    override fun trackedEntityInstances(): TrackedEntityInstanceCollectionRepository {
        return trackedEntityInstances
    }

    override fun trackedEntityDataValues(): TrackedEntityDataValueCollectionRepository {
        return trackedEntityDataValues
    }

    override fun trackedEntityAttributeValues(): TrackedEntityAttributeValueCollectionRepository {
        return trackedEntityAttributeValues
    }

    override fun trackedEntityAttributes(): TrackedEntityAttributeCollectionRepository {
        return trackedEntityAttributes
    }

    override fun trackedEntityTypeAttributes(): TrackedEntityTypeAttributeCollectionRepository {
        return trackedEntityTypeAttributes
    }

    override fun trackedEntityInstanceFilters(): TrackedEntityInstanceFilterCollectionRepository {
        return trackedEntityInstanceFilters
    }

    override fun trackedEntityInstanceQuery(): TrackedEntityInstanceQueryCollectionRepository {
        return trackedEntityInstanceQuery
    }

    override fun reservedValueManager(): TrackedEntityAttributeReservedValueManager {
        return reservedValueManager
    }

    override fun trackedEntityInstanceDownloader(): TrackedEntityInstanceDownloader {
        return trackedEntityInstanceDownloader
    }

    override fun trackedEntityInstanceService(): TrackedEntityInstanceService {
        return trackedEntityInstanceService
    }

    override fun ownershipManager(): OwnershipManager {
        return ownershipManager
    }
}
