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
package org.hisp.dhis.android.core.category.internal

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.same
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.category.CategoryComboInternalAccessor.accessCategoryOptionCombos
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.junit.Before
import org.junit.Test

class CategoryComboHandlerShould {
    private val categoryComboStore: CategoryComboStore = mock()
    private val optionComboHandler: CategoryOptionComboHandler = mock()
    private val categoryCategoryComboLinkHandler: CategoryCategoryComboLinkHandler = mock()
    private val categoryOptionCleaner: CategoryOptionComboOrphanCleaner = mock()

    private val comboUid = "comboId"

    private val combo: CategoryCombo = mock()
    private val category: Category = mock()
    private val optionCombos: List<CategoryOptionCombo> = mock()

    private lateinit var categoryComboHandler: Handler<CategoryCombo>
    private lateinit var categories: List<Category>

    @Before
    fun setUp() {
        categories = listOf(category)
        whenever(combo.uid()).doReturn(comboUid)
        whenever(accessCategoryOptionCombos(combo)).doReturn(optionCombos)
        whenever(combo.categories()).thenReturn(categories)
        whenever(categoryComboStore.updateOrInsert(any())).doReturn(HandleAction.Insert)

        categoryComboHandler = CategoryComboHandler(
            categoryComboStore, optionComboHandler,
            categoryCategoryComboLinkHandler, categoryOptionCleaner
        )
    }

    @Test
    fun handle_option_combos() {
        categoryComboHandler.handle(combo)
        verify(optionComboHandler).handleMany(eq(optionCombos), any())
    }

    @Test
    fun handle_category_category_combo_links() {
        categoryComboHandler.handle(combo)
        verify(categoryCategoryComboLinkHandler).handleMany(same(comboUid), eq(categories), any())
    }

    @Test
    fun clean_option_combo_orphans_for_update() {
        whenever(categoryComboStore.updateOrInsert(combo)).doReturn(HandleAction.Update)
        categoryComboHandler.handle(combo)
        verify(categoryOptionCleaner).deleteOrphan(combo, optionCombos)
    }

    @Test
    fun not_clean_option_combo_orphans_for_insert() {
        whenever(categoryComboStore.updateOrInsert(combo)).doReturn(HandleAction.Insert)
        categoryComboHandler.handle(combo)
        verify(categoryOptionCleaner, never()).deleteOrphan(combo, optionCombos)
    }
}
