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
package org.hisp.dhis.android.core.visualization.internal

import android.database.Cursor
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.stores.internal.SingleParentChildStore
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreFactory.singleParentChildStore
import org.hisp.dhis.android.core.arch.db.stores.projections.internal.SingleParentChildProjection
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender
import org.hisp.dhis.android.core.visualization.LayoutPosition
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationDimension
import org.hisp.dhis.android.core.visualization.VisualizationDimensionItem
import org.hisp.dhis.android.core.visualization.VisualizationDimensionItemTableInfo

internal class VisualizationColumnsRowsFiltersChildrenAppender private constructor(
    private val linkChildStore: SingleParentChildStore<Visualization, VisualizationDimensionItem>
) : ChildrenAppender<Visualization>() {
    override fun appendChildren(visualization: Visualization): Visualization {
        val items = linkChildStore.getChildren(visualization)
        val groupedByPosition = items
            .groupBy { it.position() }
            .mapValues { (_, items) ->
                items
                    .groupBy { it.dimension() }
                    .map { (dimension, items) ->
                        VisualizationDimension.builder()
                            .id(dimension)
                            .items(items)
                            .build()
                    }
            }

        return visualization.toBuilder()
            .columns(groupedByPosition[LayoutPosition.COLUMN] ?: emptyList())
            .rows(groupedByPosition[LayoutPosition.ROW] ?: emptyList())
            .filters(groupedByPosition[LayoutPosition.FILTER] ?: emptyList())
            .build()
    }

    companion object {
        private val CHILD_PROJECTION = SingleParentChildProjection(
            VisualizationDimensionItemTableInfo.TABLE_INFO,
            VisualizationDimensionItemTableInfo.Columns.VISUALIZATION
        )

        fun create(databaseAdapter: DatabaseAdapter): ChildrenAppender<Visualization> {
            return VisualizationColumnsRowsFiltersChildrenAppender(
                singleParentChildStore(
                    databaseAdapter,
                    CHILD_PROJECTION
                ) { cursor: Cursor -> VisualizationDimensionItem.create(cursor) }
            )
        }
    }
}
