/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.category.internal

import kotlinx.coroutines.runBlocking
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.category.CategoryComboInternalAccessor
import org.hisp.dhis.android.persistence.category.CategoryComboTableInfo
import org.hisp.dhis.android.persistence.common.querybuilders.WhereClauseBuilder
import org.koin.core.annotation.Singleton

@Singleton
internal class DefaultCategoryComboManager(
    private val categoryComboStore: CategoryComboStore,
    private val networkHandler: CategoryComboNetworkHandler,
) {
    private var defaultCategoryComboUid: String? = null
    private var defaultCategoryOptionComboUid: String? = null
    private var defaultCategoryUid: String? = null

    fun getDefaultCategoryComboUid(): String? = defaultCategoryComboUid
        ?: runBlocking { loadDefaultsFromDatabase() }.let { defaultCategoryComboUid }

    fun getDefaultCategoryOptionComboUid(): String? = defaultCategoryOptionComboUid
        ?: runBlocking { loadDefaultsFromDatabase() }.let { defaultCategoryOptionComboUid }

    fun getDefaultCategoryUid(): String? = defaultCategoryUid
        ?: runBlocking { loadDefaultsFromDatabase() }.let { defaultCategoryUid }

    fun setDefaults(categoryCombo: CategoryCombo) {
        defaultCategoryComboUid = categoryCombo.uid()
        defaultCategoryOptionComboUid = CategoryComboInternalAccessor.accessCategoryOptionCombos(categoryCombo)
            ?.firstOrNull()?.uid()
        defaultCategoryUid = categoryCombo.categories()?.firstOrNull()?.uid()
    }

    fun clearCache() {
        defaultCategoryComboUid = null
        defaultCategoryOptionComboUid = null
        defaultCategoryUid = null
    }

    suspend fun fetchDefaults() {
        networkHandler.getDefaultCategoryCombo()?.let { setDefaults(it) }
    }

    private suspend fun loadDefaultsFromDatabase() {
        if (defaultCategoryComboUid != null) return

        val whereClause = WhereClauseBuilder()
            .appendKeyNumberValue(CategoryComboTableInfo.Columns.IS_DEFAULT, 1)
            .build()

        categoryComboStore.selectOneWhere(whereClause)?.let { setDefaults(it) }
    }
}
