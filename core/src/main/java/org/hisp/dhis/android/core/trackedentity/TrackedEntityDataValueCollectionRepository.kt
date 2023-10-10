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

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DeletedFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityDataValueStore
import javax.inject.Inject

@Reusable
class TrackedEntityDataValueCollectionRepository @Inject internal constructor(
    private val store: TrackedEntityDataValueStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
    private val dataStatePropagator: DataStatePropagator,
) : ReadOnlyCollectionRepositoryImpl<TrackedEntityDataValue, TrackedEntityDataValueCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        TrackedEntityDataValueCollectionRepository(
            store,
            databaseAdapter,
            s,
            dataStatePropagator,
        )
    },
) {
    fun value(event: String, dataElement: String): TrackedEntityDataValueObjectRepository {
        val updatedScope = byEvent().eq(event).byDataElement().eq(dataElement).scope
        return TrackedEntityDataValueObjectRepository(
            store,
            databaseAdapter,
            childrenAppenders,
            updatedScope,
            dataStatePropagator,
            event,
            dataElement,
        )
    }

    fun byEvent(): StringFilterConnector<TrackedEntityDataValueCollectionRepository> {
        return cf.string(TrackedEntityDataValueTableInfo.Columns.EVENT)
    }

    fun byCreated(): DateFilterConnector<TrackedEntityDataValueCollectionRepository> {
        return cf.date(TrackedEntityDataValueTableInfo.Columns.CREATED)
    }

    fun byLastUpdated(): DateFilterConnector<TrackedEntityDataValueCollectionRepository> {
        return cf.date(TrackedEntityDataValueTableInfo.Columns.LAST_UPDATED)
    }

    fun byDataElement(): StringFilterConnector<TrackedEntityDataValueCollectionRepository> {
        return cf.string(TrackedEntityDataValueTableInfo.Columns.DATA_ELEMENT)
    }

    fun byStoredBy(): StringFilterConnector<TrackedEntityDataValueCollectionRepository> {
        return cf.string(TrackedEntityDataValueTableInfo.Columns.STORED_BY)
    }

    fun byValue(): StringFilterConnector<TrackedEntityDataValueCollectionRepository> {
        return cf.string(TrackedEntityDataValueTableInfo.Columns.VALUE)
    }

    fun byProvidedElsewhere(): BooleanFilterConnector<TrackedEntityDataValueCollectionRepository> {
        return cf.bool(TrackedEntityDataValueTableInfo.Columns.PROVIDED_ELSEWHERE)
    }

    fun byDeleted(): DeletedFilterConnector<TrackedEntityDataValueCollectionRepository> {
        return cf.deleted(TrackedEntityDataValueTableInfo.Columns.VALUE)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<TrackedEntityDataValue> = emptyMap()
    }
}
