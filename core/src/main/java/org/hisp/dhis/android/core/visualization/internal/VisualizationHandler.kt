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

import dagger.Reusable
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.visualization.LayoutPosition
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationDimension
import org.hisp.dhis.android.core.visualization.VisualizationDimensionItem
import javax.inject.Inject

@Reusable
internal class VisualizationHandler @Inject constructor(
    store: IdentifiableObjectStore<Visualization>,
    private val visualizationCollectionCleaner: CollectionCleaner<Visualization>,
    private val itemHandler: LinkHandler<VisualizationDimensionItem, VisualizationDimensionItem>
) : IdentifiableHandlerImpl<Visualization>(store) {

    override fun afterObjectHandled(o: Visualization, action: HandleAction) {
        val items =
            toItems(o.columns(), LayoutPosition.COLUMN) +
                toItems(o.rows(), LayoutPosition.ROW) +
                toItems(o.filters(), LayoutPosition.FILTER)

        itemHandler.handleMany(o.uid(), items) {
            it.toBuilder().visualization(o.uid()).build()
        }
    }

    override fun afterCollectionHandled(oCollection: Collection<Visualization>?) {
        visualizationCollectionCleaner.deleteNotPresent(oCollection)
    }

    private fun toItems(dimensions: List<VisualizationDimension>?,
                        position: LayoutPosition): List<VisualizationDimensionItem> {
        return dimensions?.map { dimension ->
            if (dimension.items().isNullOrEmpty()) {
                listOf(
                    VisualizationDimensionItem.builder()
                        .position(position)
                        .dimension(dimension.id())
                        .build()
                )
            } else {
                dimension.items()!!.map { item ->
                    item.toBuilder()
                        .position(position)
                        .dimension(dimension.id())
                        .build()
                }
            }
        }?.flatten() ?: emptyList()
    }
}
