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
package org.hisp.dhis.android.core.visualization

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.visualization.internal.TrackerVisualizationColumnsFiltersChildrenAppender
import org.hisp.dhis.android.core.visualization.internal.TrackerVisualizationStore
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class TrackerVisualizationCollectionRepository internal constructor(
    store: TrackerVisualizationStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
) : ReadOnlyIdentifiableCollectionRepositoryImpl<TrackerVisualization, TrackerVisualizationCollectionRepository>(
    store,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        TrackerVisualizationCollectionRepository(
            store,
            databaseAdapter,
            s,
        )
    },
) {
    fun byDescription(): StringFilterConnector<TrackerVisualizationCollectionRepository> {
        return cf.string(TrackerVisualizationTableInfo.Columns.DESCRIPTION)
    }

    fun byDisplayDescription(): StringFilterConnector<TrackerVisualizationCollectionRepository> {
        return cf.string(TrackerVisualizationTableInfo.Columns.DISPLAY_DESCRIPTION)
    }

    fun byType(): EnumFilterConnector<TrackerVisualizationCollectionRepository, TrackerVisualizationType> {
        return cf.enumC(TrackerVisualizationTableInfo.Columns.TYPE)
    }

    fun byOutputType(): EnumFilterConnector<TrackerVisualizationCollectionRepository, TrackerVisualizationOutputType> {
        return cf.enumC(TrackerVisualizationTableInfo.Columns.OUTPUT_TYPE)
    }

    fun byProgram(): StringFilterConnector<TrackerVisualizationCollectionRepository> {
        return cf.string(TrackerVisualizationTableInfo.Columns.PROGRAM)
    }

    fun byProgramStage(): StringFilterConnector<TrackerVisualizationCollectionRepository> {
        return cf.string(TrackerVisualizationTableInfo.Columns.PROGRAM_STAGE)
    }

    fun byTrackedEntityType(): StringFilterConnector<TrackerVisualizationCollectionRepository> {
        return cf.string(TrackerVisualizationTableInfo.Columns.TRACKED_ENTITY_TYPE)
    }

    fun withColumnsAndFilters(): TrackerVisualizationCollectionRepository {
        return cf.withChild(ITEMS)
    }

    internal companion object {
        private const val ITEMS = "items"

        val childrenAppenders: ChildrenAppenderGetter<TrackerVisualization> = mapOf(
            ITEMS to TrackerVisualizationColumnsFiltersChildrenAppender::create,
        )
    }
}
