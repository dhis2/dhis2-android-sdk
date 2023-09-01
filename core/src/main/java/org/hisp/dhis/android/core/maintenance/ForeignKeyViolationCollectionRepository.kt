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
package org.hisp.dhis.android.core.maintenance

import dagger.Reusable
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyViolationStore
import javax.inject.Inject

@Reusable
class ForeignKeyViolationCollectionRepository @Inject internal constructor(
    store: ForeignKeyViolationStore,
    childrenAppenders: Map<String, ChildrenAppender<ForeignKeyViolation>>,
    scope: RepositoryScope,
) : ReadOnlyCollectionRepositoryImpl<ForeignKeyViolation, ForeignKeyViolationCollectionRepository>(
    store,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ForeignKeyViolationCollectionRepository(
            store,
            childrenAppenders,
            s,
        )
    },
) {
    fun byFromTable(): StringFilterConnector<ForeignKeyViolationCollectionRepository> {
        return cf.string(ForeignKeyViolationTableInfo.Columns.FROM_TABLE)
    }

    fun byFromColumn(): StringFilterConnector<ForeignKeyViolationCollectionRepository> {
        return cf.string(ForeignKeyViolationTableInfo.Columns.FROM_COLUMN)
    }

    fun byToTable(): StringFilterConnector<ForeignKeyViolationCollectionRepository> {
        return cf.string(ForeignKeyViolationTableInfo.Columns.TO_TABLE)
    }

    fun byToColumn(): StringFilterConnector<ForeignKeyViolationCollectionRepository> {
        return cf.string(ForeignKeyViolationTableInfo.Columns.TO_COLUMN)
    }

    fun byNotFoundValue(): StringFilterConnector<ForeignKeyViolationCollectionRepository> {
        return cf.string(ForeignKeyViolationTableInfo.Columns.NOT_FOUND_VALUE)
    }

    fun byFromObjectUid(): StringFilterConnector<ForeignKeyViolationCollectionRepository> {
        return cf.string(ForeignKeyViolationTableInfo.Columns.FROM_OBJECT_UID)
    }

    fun byFromObjectRow(): StringFilterConnector<ForeignKeyViolationCollectionRepository> {
        return cf.string(ForeignKeyViolationTableInfo.Columns.FROM_OBJECT_ROW)
    }

    fun byCreated(): DateFilterConnector<ForeignKeyViolationCollectionRepository> {
        return cf.date(ForeignKeyViolationTableInfo.Columns.CREATED)
    }
}
