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

import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.settings.AnalyticsDhisVisualizationType
import org.hisp.dhis.android.core.settings.internal.AnalyticsDhisVisualizationCleaner
import org.hisp.dhis.android.core.visualization.LayoutPosition
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationDimension
import org.hisp.dhis.android.core.visualization.VisualizationDimensionItem
import org.koin.core.annotation.Singleton

@Singleton
internal class VisualizationHandler(
    store: VisualizationStore,
    private val visualizationCollectionCleaner: VisualizationCollectionCleaner,
    private val analyticsDhisVisualizationCleaner: AnalyticsDhisVisualizationCleaner,
    private val itemHandler: VisualizationDimensionItemHandler,
) : IdentifiableHandlerImpl<Visualization>(store) {

    override suspend fun afterObjectHandled(o: Visualization, action: HandleAction) {
        val items =
            toItems(o.columns(), LayoutPosition.COLUMN) +
                toItems(o.rows(), LayoutPosition.ROW) +
                toItems(o.filters(), LayoutPosition.FILTER)

        itemHandler.handleMany(o.uid(), items)
    }

    override suspend fun afterCollectionHandled(oCollection: Collection<Visualization>?) {
        visualizationCollectionCleaner.deleteNotPresent(oCollection)
        analyticsDhisVisualizationCleaner.deleteNotPresent(
            uids = store.selectUids(),
            type = AnalyticsDhisVisualizationType.VISUALIZATION,
        )
    }

    private fun toItems(
        dimensions: List<VisualizationDimension>?,
        position: LayoutPosition,
    ): List<VisualizationDimensionItem> {
        return dimensions?.map { dimension ->
            val nonNullItems = dimension.items()?.filterNotNull()
            if (nonNullItems.isNullOrEmpty()) {
                // Add auxiliary empty item to persist in the database
                listOf(
                    VisualizationDimensionItem.builder()
                        .position(position)
                        .dimension(dimension.id())
                        .build(),
                )
            } else {
                nonNullItems
            }
        }?.flatten() ?: emptyList()
    }
}
