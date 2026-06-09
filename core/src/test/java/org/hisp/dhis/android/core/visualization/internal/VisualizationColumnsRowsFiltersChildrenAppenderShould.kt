/*
 *  Copyright (c) 2004-2024, University of Oslo
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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.visualization.LayoutPosition
import org.hisp.dhis.android.core.visualization.Visualization
import org.hisp.dhis.android.core.visualization.VisualizationDimensionItem
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class VisualizationColumnsRowsFiltersChildrenAppenderShould {

    private val childStore: VisualizationDimensionItemStore = mock()

    private val visualizationUid = "PYBH8ZaAQnC"
    private val visualization = Visualization.builder().uid(visualizationUid).build()

    private val categoryUid1 = "GMpWZUg2QUf"
    private val categoryUid2 = "AC6H8zCDb3B"
    private val categoryOptionUid = "eEIN8RQWxWp"

    private lateinit var appender: VisualizationColumnsRowsFiltersChildrenAppender

    @Before
    fun setUp() {
        appender = VisualizationColumnsRowsFiltersChildrenAppender(childStore)
    }

    private fun item(dimension: String, dimensionItem: String) =
        VisualizationDimensionItem.builder()
            .visualization(visualizationUid)
            .position(LayoutPosition.COLUMN)
            .dimension(dimension)
            .dimensionItem(dimensionItem)
            .build()

    @Test
    fun `convert allItems placeholder into an empty item list`() = runTest {
        whenever(childStore.getVisualizationDimensionItemForVisualization(visualizationUid)).doReturn(
            listOf(item(categoryUid1, "$categoryUid1.allItems")),
        )

        val result = appender.appendChildren(visualization)

        val columns = result.columns()!!
        assertThat(columns).hasSize(1)
        assertThat(columns.first().id()).isEqualTo(categoryUid1)
        assertThat(columns.first().items()).isEmpty()
    }

    @Test
    fun `keep allItems placeholders independent for each dimension`() = runTest {
        whenever(childStore.getVisualizationDimensionItemForVisualization(visualizationUid)).doReturn(
            listOf(
                item(categoryUid1, "$categoryUid1.allItems"),
                item(categoryUid2, "$categoryUid2.allItems"),
            ),
        )

        val result = appender.appendChildren(visualization)

        val columns = result.columns()!!
        assertThat(columns).hasSize(2)
        columns.forEach { assertThat(it.items()).isEmpty() }
    }

    @Test
    fun `keep real dimension items`() = runTest {
        whenever(childStore.getVisualizationDimensionItemForVisualization(visualizationUid)).doReturn(
            listOf(item(categoryUid1, categoryOptionUid)),
        )

        val result = appender.appendChildren(visualization)

        val columns = result.columns()!!
        assertThat(columns).hasSize(1)
        assertThat(columns.first().items()!!.mapNotNull { it.dimensionItem() }).containsExactly(categoryOptionUid)
    }
}
