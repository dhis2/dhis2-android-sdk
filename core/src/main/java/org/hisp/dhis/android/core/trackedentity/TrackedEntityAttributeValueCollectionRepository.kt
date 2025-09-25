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

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DeletedFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeValueStore
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeValueTableInfo
import org.koin.core.annotation.Singleton

@Singleton
class TrackedEntityAttributeValueCollectionRepository internal constructor(
    private val store: TrackedEntityAttributeValueStore,
    scope: RepositoryScope,
    private val dataStatePropagator: DataStatePropagator,
) : ReadOnlyCollectionRepositoryImpl<TrackedEntityAttributeValue, TrackedEntityAttributeValueCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        TrackedEntityAttributeValueCollectionRepository(
            store,
            s,
            dataStatePropagator,
        )
    },
) {
    fun value(
        trackedEntityAttribute: String,
        trackedEntityInstance: String,
    ): TrackedEntityAttributeValueObjectRepository {
        val updatedScope = byTrackedEntityAttribute().eq(trackedEntityAttribute)
            .byTrackedEntityInstance().eq(trackedEntityInstance).scope
        return TrackedEntityAttributeValueObjectRepository(
            store,
            childrenAppenders,
            updatedScope,
            dataStatePropagator,
            trackedEntityAttribute,
            trackedEntityInstance,
        )
    }

    fun byTrackedEntityAttribute(): StringFilterConnector<TrackedEntityAttributeValueCollectionRepository> {
        return cf.string(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_ATTRIBUTE)
    }

    fun byValue(): StringFilterConnector<TrackedEntityAttributeValueCollectionRepository> {
        return cf.string(TrackedEntityAttributeValueTableInfo.Columns.VALUE)
    }

    fun byCreated(): DateFilterConnector<TrackedEntityAttributeValueCollectionRepository> {
        return cf.date(TrackedEntityAttributeValueTableInfo.Columns.CREATED)
    }

    fun byLastUpdated(): DateFilterConnector<TrackedEntityAttributeValueCollectionRepository> {
        return cf.date(TrackedEntityAttributeValueTableInfo.Columns.LAST_UPDATED)
    }

    fun byTrackedEntityInstance(): StringFilterConnector<TrackedEntityAttributeValueCollectionRepository> {
        return cf.string(TrackedEntityAttributeValueTableInfo.Columns.TRACKED_ENTITY_INSTANCE)
    }

    fun byDeleted(): DeletedFilterConnector<TrackedEntityAttributeValueCollectionRepository> {
        return cf.deleted(TrackedEntityAttributeValueTableInfo.Columns.VALUE)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<TrackedEntityAttributeValue> = emptyMap()
    }
}
