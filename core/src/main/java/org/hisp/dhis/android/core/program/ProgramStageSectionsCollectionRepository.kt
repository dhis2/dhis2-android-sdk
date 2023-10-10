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

import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.IntegerFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.program.internal.ProgramStageSectionDataElementChildrenAppender
import org.hisp.dhis.android.core.program.internal.ProgramStageSectionFields
import org.hisp.dhis.android.core.program.internal.ProgramStageSectionProgramIndicatorChildrenAppender
import org.hisp.dhis.android.core.program.internal.ProgramStageSectionStore
import javax.inject.Inject

@Reusable
class ProgramStageSectionsCollectionRepository @Inject internal constructor(
    store: ProgramStageSectionStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<ProgramStageSection, ProgramStageSectionsCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        ProgramStageSectionsCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun bySortOrder(): IntegerFilterConnector<ProgramStageSectionsCollectionRepository> {
        return cf.integer(ProgramStageSectionTableInfo.Columns.SORT_ORDER)
    }

    fun byProgramStageUid(): StringFilterConnector<ProgramStageSectionsCollectionRepository> {
        return cf.string(ProgramStageSectionTableInfo.Columns.PROGRAM_STAGE)
    }

    fun byDesktopRenderType(): StringFilterConnector<ProgramStageSectionsCollectionRepository> {
        return cf.string(ProgramStageSectionTableInfo.Columns.DESKTOP_RENDER_TYPE)
    }

    fun byMobileRenderType(): StringFilterConnector<ProgramStageSectionsCollectionRepository> {
        return cf.string(ProgramStageSectionTableInfo.Columns.MOBILE_RENDER_TYPE)
    }

    fun byDescription(): StringFilterConnector<ProgramStageSectionsCollectionRepository> {
        return cf.string(ProgramStageSectionTableInfo.Columns.DESCRIPTION)
    }

    fun byDisplayDescription(): StringFilterConnector<ProgramStageSectionsCollectionRepository> {
        return cf.string(ProgramStageSectionTableInfo.Columns.DISPLAY_DESCRIPTION)
    }

    fun withProgramIndicators(): ProgramStageSectionsCollectionRepository {
        return cf.withChild(ProgramStageSectionFields.PROGRAM_INDICATORS)
    }

    fun withDataElements(): ProgramStageSectionsCollectionRepository {
        return cf.withChild(ProgramStageSectionFields.DATA_ELEMENTS)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<ProgramStageSection> = mapOf(
            ProgramStageSectionFields.PROGRAM_INDICATORS to ProgramStageSectionProgramIndicatorChildrenAppender::create,
            ProgramStageSectionFields.DATA_ELEMENTS to ProgramStageSectionDataElementChildrenAppender::create,
        )
    }
}
