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
package org.hisp.dhis.android.core.arch.repositories.collection.internal

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.common.CoreObject
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.IdentifiableObject

@Suppress("TooManyFunctions")
open class ReadOnlyIdentifiableCollectionRepositoryImpl<M, R : ReadOnlyCollectionRepository<M>> internal constructor(
    store: IdentifiableObjectStore<M>,
    childrenAppenders: ChildrenAppenderGetter<M>,
    scope: RepositoryScope,
    cf: FilterConnectorFactory<R>,
) : ReadOnlyWithUidCollectionRepositoryImpl<M, R>(store, childrenAppenders, scope, cf),
    ReadOnlyIdentifiableCollectionRepository<M, R> where M : CoreObject, M : IdentifiableObject {
    override fun byUid(): StringFilterConnector<R> {
        return cf.string(IdentifiableColumns.UID)
    }

    override fun byCode(): StringFilterConnector<R> {
        return cf.string(IdentifiableColumns.CODE)
    }

    override fun byName(): StringFilterConnector<R> {
        return cf.string(IdentifiableColumns.NAME)
    }

    override fun byDisplayName(): StringFilterConnector<R> {
        return cf.string(IdentifiableColumns.DISPLAY_NAME)
    }

    override fun byCreated(): DateFilterConnector<R> {
        return cf.date(IdentifiableColumns.CREATED)
    }

    override fun byLastUpdated(): DateFilterConnector<R> {
        return cf.date(IdentifiableColumns.LAST_UPDATED)
    }

    fun orderByUid(direction: OrderByDirection?): R {
        return cf.withOrderBy(IdentifiableColumns.UID, direction)
    }

    fun orderByCode(direction: OrderByDirection?): R {
        return cf.withOrderBy(IdentifiableColumns.CODE, direction)
    }

    fun orderByName(direction: OrderByDirection?): R {
        return cf.withOrderBy(IdentifiableColumns.NAME, direction)
    }

    fun orderByDisplayName(direction: OrderByDirection?): R {
        return cf.withOrderBy(IdentifiableColumns.DISPLAY_NAME, direction)
    }

    fun orderByCreated(direction: OrderByDirection?): R {
        return cf.withOrderBy(IdentifiableColumns.CREATED, direction)
    }

    fun orderByLastUpdated(direction: OrderByDirection?): R {
        return cf.withOrderBy(IdentifiableColumns.LAST_UPDATED, direction)
    }
}
