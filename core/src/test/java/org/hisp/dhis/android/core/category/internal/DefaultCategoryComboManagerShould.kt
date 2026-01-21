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

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.category.CategoryComboInternalAccessor.accessCategoryOptionCombos
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

class DefaultCategoryComboManagerShould {
    private val categoryComboStore: CategoryComboStore = mock()
    private val categoryComboHandler: CategoryComboHandler = mock()
    private val networkHandler: CategoryComboNetworkHandler = mock()

    private val comboUid = "defaultComboUid"
    private val optionComboUid = "defaultOptionComboUid"
    private val categoryUid = "defaultCategoryUid"

    private val categoryCombo: CategoryCombo = mock()
    private val categoryOptionCombo: CategoryOptionCombo = mock()
    private val category: Category = mock()

    private lateinit var manager: DefaultCategoryComboManager

    @Before
    fun setUp() {
        whenever(categoryCombo.uid()).doReturn(comboUid)
        whenever(categoryOptionCombo.uid()).doReturn(optionComboUid)
        whenever(category.uid()).doReturn(categoryUid)
        whenever(accessCategoryOptionCombos(categoryCombo)).doReturn(listOf(categoryOptionCombo))
        whenever(categoryCombo.categories()).doReturn(listOf(category))

        manager = DefaultCategoryComboManager(
            categoryComboStore,
            categoryComboHandler,
            networkHandler,
        )
    }

    @Test
    fun return_cached_value_when_available() = runTest {
        manager.setDefaults(categoryCombo)

        val result = manager.getDefaultCategoryComboUid()

        assertThat(result).isEqualTo(comboUid)
        verify(categoryComboStore, never()).selectOneWhere(any())
        verifyNoMoreInteractions(networkHandler)
    }

    @Test
    fun return_all_cached_values_when_available() = runTest {
        manager.setDefaults(categoryCombo)

        assertThat(manager.getDefaultCategoryComboUid()).isEqualTo(comboUid)
        assertThat(manager.getDefaultCategoryOptionComboUid()).isEqualTo(optionComboUid)
        assertThat(manager.getDefaultCategoryUid()).isEqualTo(categoryUid)

        verify(categoryComboStore, never()).selectOneWhere(any())
        verifyNoMoreInteractions(networkHandler)
    }

    @Test
    fun query_database_when_cache_is_empty() = runTest {
        whenever(categoryComboStore.selectOneWhere(any())).doReturn(categoryCombo)

        val result = manager.getDefaultCategoryComboUid()

        assertThat(result).isEqualTo(comboUid)
        verify(categoryComboStore).selectOneWhere(any())
        verifyNoMoreInteractions(networkHandler)
    }

    @Test
    fun return_null_when_not_in_database() = runTest {
        whenever(categoryComboStore.selectOneWhere(any())).doReturn(null)

        val result = manager.getDefaultCategoryComboUid()

        assertThat(result).isNull()
        verify(categoryComboStore).selectOneWhere(any())
        verifyNoMoreInteractions(networkHandler)
    }

    @Test
    fun download_defaults_makes_api_call_and_stores_result() = runTest {
        whenever(networkHandler.getDefaultCategoryCombo()).doReturn(categoryCombo)

        manager.downloadDefaults()

        assertThat(manager.getDefaultCategoryComboUid()).isEqualTo(comboUid)
        verify(networkHandler).getDefaultCategoryCombo()
        verify(categoryComboHandler).handle(categoryCombo)
    }

    @Test
    fun set_defaults_extracts_all_uids_correctly() {
        manager.setDefaults(categoryCombo)

        assertThat(manager.getDefaultCategoryComboUid()).isEqualTo(comboUid)
        assertThat(manager.getDefaultCategoryOptionComboUid()).isEqualTo(optionComboUid)
        assertThat(manager.getDefaultCategoryUid()).isEqualTo(categoryUid)
    }

    @Test
    fun clear_cache_clears_all_values() = runTest {
        manager.setDefaults(categoryCombo)

        assertThat(manager.getDefaultCategoryComboUid()).isEqualTo(comboUid)

        manager.clearCache()

        whenever(categoryComboStore.selectOneWhere(any())).doReturn(null)

        val result = manager.getDefaultCategoryComboUid()

        assertThat(result).isNull()
        verify(categoryComboStore).selectOneWhere(any())
    }

    @Test
    fun download_defaults_handles_api_failure_gracefully() = runTest {
        whenever(networkHandler.getDefaultCategoryCombo()).thenThrow(RuntimeException("Network error"))
        whenever(categoryComboStore.selectOneWhere(any())).doReturn(null)

        manager.downloadDefaults()

        assertThat(manager.getDefaultCategoryComboUid()).isNull()
        verify(networkHandler).getDefaultCategoryCombo()
        verify(categoryComboHandler, never()).handle(any<CategoryCombo>())
    }

    @Test
    fun handle_null_category_option_combos() {
        whenever(accessCategoryOptionCombos(categoryCombo)).doReturn(null)
        whenever(categoryCombo.categories()).doReturn(listOf(category))

        manager.setDefaults(categoryCombo)

        assertThat(manager.getDefaultCategoryComboUid()).isEqualTo(comboUid)
        assertThat(manager.getDefaultCategoryOptionComboUid()).isNull()
        assertThat(manager.getDefaultCategoryUid()).isEqualTo(categoryUid)
    }

    @Test
    fun handle_null_categories() {
        whenever(accessCategoryOptionCombos(categoryCombo)).doReturn(listOf(categoryOptionCombo))
        whenever(categoryCombo.categories()).doReturn(null)

        manager.setDefaults(categoryCombo)

        assertThat(manager.getDefaultCategoryComboUid()).isEqualTo(comboUid)
        assertThat(manager.getDefaultCategoryOptionComboUid()).isEqualTo(optionComboUid)
        assertThat(manager.getDefaultCategoryUid()).isNull()
    }

    @Test
    fun handle_empty_category_option_combos() {
        whenever(accessCategoryOptionCombos(categoryCombo)).doReturn(emptyList())
        whenever(categoryCombo.categories()).doReturn(listOf(category))

        manager.setDefaults(categoryCombo)

        assertThat(manager.getDefaultCategoryComboUid()).isEqualTo(comboUid)
        assertThat(manager.getDefaultCategoryOptionComboUid()).isNull()
        assertThat(manager.getDefaultCategoryUid()).isEqualTo(categoryUid)
    }

    @Test
    fun handle_empty_categories() {
        whenever(accessCategoryOptionCombos(categoryCombo)).doReturn(listOf(categoryOptionCombo))
        whenever(categoryCombo.categories()).doReturn(emptyList())

        manager.setDefaults(categoryCombo)

        assertThat(manager.getDefaultCategoryComboUid()).isEqualTo(comboUid)
        assertThat(manager.getDefaultCategoryOptionComboUid()).isEqualTo(optionComboUid)
        assertThat(manager.getDefaultCategoryUid()).isNull()
    }
}
