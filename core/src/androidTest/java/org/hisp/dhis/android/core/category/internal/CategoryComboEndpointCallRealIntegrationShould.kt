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
package org.hisp.dhis.android.core.category.internal

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore
import org.hisp.dhis.android.core.category.CategoryCategoryComboLink
import org.hisp.dhis.android.core.category.CategoryOptionCombo

class CategoryComboEndpointCallRealIntegrationShould : BaseRealIntegrationTest() {

    // @Test
    fun download_categories_combos_and_relatives() {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.databaseAdapter().setForeignKeyConstraintsEnabled(false)
        assertNotCombosInDB()
        assertThat(categoryCategoryComboLinks.isEmpty()).isTrue()

        val categoryComboEndpointCall = getD2DIComponent(d2).internalModules().category.categoryComboCall.download(
            setOf("bjDvmb4bfuf"),
        )
        val categoryCombos = categoryComboEndpointCall.blockingGet()
        assertThat(categoryCombos.isEmpty()).isFalse()

        downloadCategories()
        assertDataIsProperlyParsedAndInsertedInTheDB()
    }

    private fun assertDataIsProperlyParsedAndInsertedInTheDB() {
        assertThereAreCombosInDB()
        assertThat(categoryCategoryComboLinks.isEmpty()).isFalse()
        assertThereAreCategoryOptionCombosInDB()
        assertThereAreCategoriesInDB()
    }

    private fun downloadCategories() {
        getD2DIComponent(d2).internalModules().category.categoryCall.download(
            setOf("GLevLNI9wkl"),
        ).blockingGet()
    }

    private fun assertNotCombosInDB() {
        val categoryComboStore = CategoryComboStoreImpl(d2.databaseAdapter())
        val categoryCombos = categoryComboStore.selectAll()
        assertThat(categoryCombos.isEmpty()).isTrue()
    }

    private fun assertThereAreCombosInDB() {
        val categoryComboStore = CategoryComboStoreImpl(d2.databaseAdapter())
        val categoryCombos = categoryComboStore.selectAll()
        assertThat(categoryCombos.isNotEmpty()).isTrue()
    }

    private val categoryCategoryComboLinks: List<CategoryCategoryComboLink>
        get() {
            val categoryCategoryComboLinkStore = CategoryCategoryComboLinkStoreImpl(d2.databaseAdapter())
            return categoryCategoryComboLinkStore.selectAll()
        }

    private fun assertThereAreCategoryOptionCombosInDB() {
        val categoryOptionComboStore: IdentifiableObjectStore<CategoryOptionCombo> =
            CategoryOptionComboStoreImpl(d2.databaseAdapter())
        val categoryOptionCombos = categoryOptionComboStore.selectAll()
        assertThat(categoryOptionCombos.isNotEmpty()).isTrue()
    }

    private fun assertThereAreCategoriesInDB() {
        val categoryOptionStore = CategoryOptionStoreImpl(d2.databaseAdapter())
        val categoryOptionUids = categoryOptionStore.selectUids()
        assertThat(categoryOptionUids.isNotEmpty()).isTrue()
    }
}
