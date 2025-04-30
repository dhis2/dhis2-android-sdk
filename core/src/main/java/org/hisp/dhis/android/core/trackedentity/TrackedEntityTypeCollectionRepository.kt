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
package org.hisp.dhis.android.core.trackedentity

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyNameableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeAttributeChildrenAppender
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeStore
import org.koin.core.annotation.Singleton

@Singleton
class TrackedEntityTypeCollectionRepository internal constructor(
    store: TrackedEntityTypeStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyNameableCollectionRepositoryImpl<TrackedEntityType, TrackedEntityTypeCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        TrackedEntityTypeCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byFeatureType(): EnumFilterConnector<TrackedEntityTypeCollectionRepository, FeatureType> {
        return cf.enumC(TrackedEntityTypeTableInfo.Columns.FEATURE_TYPE)
    }

    fun byColor(): StringFilterConnector<TrackedEntityTypeCollectionRepository> {
        return cf.string(TrackedEntityTypeTableInfo.Columns.COLOR)
    }

    fun byIcon(): StringFilterConnector<TrackedEntityTypeCollectionRepository> {
        return cf.string(TrackedEntityTypeTableInfo.Columns.ICON)
    }

    fun withTrackedEntityTypeAttributes(): TrackedEntityTypeCollectionRepository {
        return cf.withChild(TRACKED_ENTITY_TYPE_ATTRIBUTES)
    }

    internal companion object {
        private const val TRACKED_ENTITY_TYPE_ATTRIBUTES = "trackedEntityTypeAttributes"

        val childrenAppenders: ChildrenAppenderGetter<TrackedEntityType> = mapOf(
            TRACKED_ENTITY_TYPE_ATTRIBUTES to
                TrackedEntityTypeAttributeChildrenAppender::create,
        )
    }
}
