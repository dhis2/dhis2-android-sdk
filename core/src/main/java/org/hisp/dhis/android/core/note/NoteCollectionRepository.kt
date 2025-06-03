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
package org.hisp.dhis.android.core.note

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadWriteWithUidCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyObjectRepository
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyOneObjectRepositoryFinalImpl
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper.withUidFilterItem
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.note.Note.NoteType
import org.hisp.dhis.android.core.note.internal.NoteProjectionTransformer
import org.hisp.dhis.android.core.note.internal.NoteStore
import org.koin.core.annotation.Singleton

@Singleton
class NoteCollectionRepository internal constructor(
    store: NoteStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
    transformer: NoteProjectionTransformer,
    private val dataStatePropagator: DataStatePropagator,
) : ReadWriteWithUidCollectionRepositoryImpl<Note, NoteCreateProjection, NoteCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    transformer,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        NoteCollectionRepository(
            store,
            databaseAdapter,
            s,
            transformer,
            dataStatePropagator,
        )
    },
) {
    fun byUid(): StringFilterConnector<NoteCollectionRepository> {
        return cf.string(IdentifiableColumns.UID)
    }

    fun byNoteType(): EnumFilterConnector<NoteCollectionRepository, NoteType> {
        return cf.enumC(NoteTableInfo.Columns.NOTE_TYPE)
    }

    fun byEventUid(): StringFilterConnector<NoteCollectionRepository> {
        return cf.string(NoteTableInfo.Columns.EVENT)
    }

    fun byEnrollmentUid(): StringFilterConnector<NoteCollectionRepository> {
        return cf.string(NoteTableInfo.Columns.ENROLLMENT)
    }

    fun byValue(): StringFilterConnector<NoteCollectionRepository> {
        return cf.string(NoteTableInfo.Columns.VALUE)
    }

    fun byStoredBy(): StringFilterConnector<NoteCollectionRepository> {
        return cf.string(NoteTableInfo.Columns.STORED_BY)
    }

    fun byStoredDate(): StringFilterConnector<NoteCollectionRepository> {
        return cf.string(NoteTableInfo.Columns.STORED_DATE)
    }

    fun bySyncState(): EnumFilterConnector<NoteCollectionRepository, State> {
        return cf.enumC(NoteTableInfo.Columns.SYNC_STATE)
    }

    override fun uid(uid: String?): ReadOnlyObjectRepository<Note> {
        val updatedScope: RepositoryScope = withUidFilterItem(scope, uid)
        return ReadOnlyOneObjectRepositoryFinalImpl(store, databaseAdapter, childrenAppenders, updatedScope)
    }

    override suspend fun propagateState(m: Note, action: HandleAction?) {
        dataStatePropagator.propagateNoteCreation(m)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<Note> = emptyMap()
    }
}
