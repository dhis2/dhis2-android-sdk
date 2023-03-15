/*
 *  Copyright (c) 2004-2022, University of Oslo
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

import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.cleaners.internal.CollectionCleaner
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.visualization.CategoryDimension
import org.hisp.dhis.android.core.visualization.DataDimensionItem
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationCategoryDimensionLink
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class VisualizationHandlerShould {

    private val visualizationStore: IdentifiableObjectStore<Visualization> = mock()
    private val visualizationCollectionCleaner: CollectionCleaner<Visualization> = mock()
    private val dataDimensionItemStore: LinkStore<DataDimensionItem> = mock()
    private val visualizationCategoryDimensionLinkStore: LinkStore<VisualizationCategoryDimensionLink> = mock()
    private val visualizationCategoryDimensionLinkHandler:
        LinkHandler<VisualizationCategoryDimensionLink, VisualizationCategoryDimensionLink> = mock()
    private val dataDimensionItemHandler: LinkHandler<DataDimensionItem, DataDimensionItem> = mock()
    private val dataDimensionItem: DataDimensionItem = mock()
    private val categoryDimension: CategoryDimension = mock()
    private val category: ObjectWithUid = mock()
    private val visualization: Visualization = mock()
    private var categories: List<ObjectWithUid> = mock()

    // object to test
    private lateinit var visualizationHandler: VisualizationHandler

    @Before
    fun setUp() {
        visualizationHandler = VisualizationHandler(
            visualizationStore,
            visualizationCollectionCleaner,
            visualizationCategoryDimensionLinkStore,
            dataDimensionItemStore,
            visualizationCategoryDimensionLinkHandler,
            dataDimensionItemHandler
        )
        val dataDimensionItems = listOf(dataDimensionItem)
        val categoryDimensions = listOf(categoryDimension)
        categories = listOf(category)

        whenever(visualization.dataDimensionItems()).doReturn(dataDimensionItems)
        whenever(visualizationStore.updateOrInsert(any())).doReturn(HandleAction.Insert)
        whenever(visualization.uid()).doReturn("visualization_uid")
        whenever(category.uid()).doReturn("category_uid")
        whenever(categoryDimension.category()).doReturn(category)
        whenever(visualization.categoryDimensions()).doReturn(categoryDimensions)
    }

    @Test
    fun call_stores_to_delete_before_collection_handled() {
        visualizationHandler.handleMany(listOf(visualization))
        verify(visualizationCategoryDimensionLinkStore).delete()
        verify(dataDimensionItemStore).delete()
    }

    @Test
    fun call_data_dimension_items_handler() {
        visualizationHandler.handleMany(listOf(visualization))
        verify(dataDimensionItemHandler).handleMany(any(), any(), any())
    }

    @Test
    fun call_category_dimensions_link_handler() {
        visualizationHandler.handleMany(listOf(visualization))
        verify(visualizationCategoryDimensionLinkHandler).handleMany(any(), any(), any())
    }

    @Test
    fun call_collection_cleaner() {
        visualizationHandler.handleMany(listOf(visualization))
        verify(visualizationCollectionCleaner).deleteNotPresent(any())
    }
}
