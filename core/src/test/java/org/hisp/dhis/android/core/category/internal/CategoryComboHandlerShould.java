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

package org.hisp.dhis.android.core.category.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.OrderedLinkHandler;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLink;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboInternalAccessor;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CategoryComboHandlerShould {

    @Mock
    private IdentifiableObjectStore<CategoryCombo> categoryComboStore;

    @Mock
    private HandlerWithTransformer<CategoryOptionCombo> optionComboHandler;

    @Mock
    private OrderedLinkHandler<Category, CategoryCategoryComboLink> categoryCategoryComboLinkHandler;

    @Mock
    private OrphanCleaner<CategoryCombo, CategoryOptionCombo> categoryOptionCleaner;

    private final String comboUid = "comboId";

    @Mock
    private CategoryCombo combo;

    @Mock
    private Category category;

    @Mock
    private List<CategoryOptionCombo> optionCombos;

    private Handler<CategoryCombo> categoryComboHandler;

    private List<Category> categories;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        categories = new ArrayList<>();
        categories.add(category);
        when(combo.uid()).thenReturn(comboUid);
        when(CategoryComboInternalAccessor.accessCategoryOptionCombos(combo)).thenReturn(optionCombos);
        when(combo.categories()).thenReturn(categories);

        categoryComboHandler = new CategoryComboHandler(categoryComboStore, optionComboHandler,
                categoryCategoryComboLinkHandler, categoryOptionCleaner);
    }

    @Test
    public void handle_option_combos() {
        categoryComboHandler.handle(combo);
        verify(optionComboHandler).handleMany(eq(optionCombos), any());
    }

    @Test
    public void handle_category_category_combo_links() {
        categoryComboHandler.handle(combo);
        verify(categoryCategoryComboLinkHandler).handleMany(same(comboUid), eq(categories),
                any());
    }

    @Test
    public void clean_option_combo_orphans_for_update() {
        when(categoryComboStore.updateOrInsert(combo)).thenReturn(HandleAction.Update);
        categoryComboHandler.handle(combo);
        verify(categoryOptionCleaner).deleteOrphan(combo, optionCombos);
    }

    @Test
    public void not_clean_option_combo_orphans_for_insert() {
        when(categoryComboStore.updateOrInsert(combo)).thenReturn(HandleAction.Insert);
        categoryComboHandler.handle(combo);
        verify(categoryOptionCleaner, never()).deleteOrphan(combo, optionCombos);
    }
}