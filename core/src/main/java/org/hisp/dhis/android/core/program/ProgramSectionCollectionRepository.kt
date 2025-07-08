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
package org.hisp.dhis.android.core.program

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.program.internal.ProgramSectionAttributeChildrenAppender
import org.hisp.dhis.android.core.program.internal.ProgramSectionStore
import org.koin.core.annotation.Singleton

@Singleton
class ProgramSectionCollectionRepository internal constructor(
    store: ProgramSectionStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramSection, ProgramSectionCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ProgramSectionCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byDescription(): StringFilterConnector<ProgramSectionCollectionRepository> {
        return cf.string(ProgramSectionTableInfo.Columns.DESCRIPTION)
    }

    fun byProgramUid(): StringFilterConnector<ProgramSectionCollectionRepository> {
        return cf.string(ProgramSectionTableInfo.Columns.PROGRAM)
    }

    fun bySortOrder(): IntegerFilterConnector<ProgramSectionCollectionRepository> {
        return cf.integer(ProgramSectionTableInfo.Columns.SORT_ORDER)
    }

    fun byFormName(): StringFilterConnector<ProgramSectionCollectionRepository> {
        return cf.string(ProgramSectionTableInfo.Columns.FORM_NAME)
    }

    fun byColor(): StringFilterConnector<ProgramSectionCollectionRepository> {
        return cf.string(ProgramSectionTableInfo.Columns.COLOR)
    }

    fun byIcon(): StringFilterConnector<ProgramSectionCollectionRepository> {
        return cf.string(ProgramSectionTableInfo.Columns.ICON)
    }

    fun byDesktopRenderType(): StringFilterConnector<ProgramSectionCollectionRepository> {
        return cf.string(ProgramSectionTableInfo.Columns.DESKTOP_RENDER_TYPE)
    }

    fun byMobileRenderType(): StringFilterConnector<ProgramSectionCollectionRepository> {
        return cf.string(ProgramSectionTableInfo.Columns.MOBILE_RENDER_TYPE)
    }

    fun withAttributes(): ProgramSectionCollectionRepository {
        return cf.withChild(TRACKED_ENTITY_ATTRIBUTES)
    }

    internal companion object {
        private const val TRACKED_ENTITY_ATTRIBUTES = "trackedEntityAttributes"

        val childrenAppenders: ChildrenAppenderGetter<ProgramSection> = mapOf(
            TRACKED_ENTITY_ATTRIBUTES to ProgramSectionAttributeChildrenAppender::create,
        )
    }
}
