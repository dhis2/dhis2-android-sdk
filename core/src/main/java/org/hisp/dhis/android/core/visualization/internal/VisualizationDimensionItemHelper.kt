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

/**
 * Helper to build and recognize the placeholder
 * [org.hisp.dhis.android.core.visualization.VisualizationDimensionItem] that represents an automatic
 * ("all options") selection for a dimension.
 *
 * The primary key of the persisted item is (visualization, dimensionItem) and does not accept null values, so a null
 * dimensionItem (which previously expressed "all options") cannot be stored. Instead, a synthetic placeholder of the
 * form `<dimensionId>.allItems` is persisted. Prefixing it with the dimension id keeps it unique per dimension, so two
 * dimensions with automatic selection in the same visualization do not collide on the primary key.
 */
internal object VisualizationDimensionItemHelper {

    private const val ALL_ITEMS_SUFFIX = "allItems"

    private val allItemsRegex = "^\\w{11}\\.$ALL_ITEMS_SUFFIX$".toRegex()

    fun allItemsPlaceholder(dimensionId: String): String = "$dimensionId.$ALL_ITEMS_SUFFIX"

    fun isAllItemsPlaceholder(dimensionItem: String?): Boolean =
        dimensionItem != null && allItemsRegex.matches(dimensionItem)
}
