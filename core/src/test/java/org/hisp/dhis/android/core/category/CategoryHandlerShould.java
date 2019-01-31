/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.DataAccess;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.OrderedLinkModelHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CategoryHandlerShould {

    @Mock
    private IdentifiableObjectStore<Category> categoryStore;

    @Mock
    private OrderedLinkModelHandler<CategoryOption, CategoryCategoryOptionLinkModel>
            categoryCategoryOptionLinkHandler;

    @Mock
    private SyncHandler<CategoryOption> categoryOptionHandler;

    private final String categoryUid = "cId";

    @Mock
    private Category category;

    private List<CategoryOption> categoryOptions;

    @Mock
    private CategoryOption categoryOption;

    @Mock
    private Access access;

    @Mock
    private DataAccess dataAccess;

    private SyncHandler<Category> categoryHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        categoryOptions = Collections.singletonList(categoryOption);

        when(category.categoryOptions()).thenReturn(categoryOptions);
        when(category.uid()).thenReturn(categoryUid);

        when(categoryOption.access()).thenReturn(access);
        when(access.data()).thenReturn(dataAccess);
        when(dataAccess.read()).thenReturn(true);

        categoryHandler = new CategoryHandler(categoryStore, categoryOptionHandler,
                categoryCategoryOptionLinkHandler);
    }

    @Test
    public void handle_category_options() {
        categoryHandler.handle(category);
        verify(categoryOptionHandler).handleMany(eq(categoryOptions));
    }

    @Test
    public void not_handle_category_options_for_null_category_options() {
        when(category.categoryOptions()).thenReturn(null);
        categoryHandler.handle(category);
        verify(categoryOptionHandler, never()).handleMany(anyListOf(CategoryOption.class));
    }

    @Test
    public void not_handle_category_options_for_category_options_when_access_data_read_is_false() {
        when(dataAccess.read()).thenReturn(false);
        categoryHandler.handle(category);
        verify(categoryOptionHandler).handleMany(new ArrayList<CategoryOption>());
    }

    @Test
    public void handle_category_option_links() {
        categoryHandler.handle(category);
        verify(categoryCategoryOptionLinkHandler).handleMany(same(categoryUid), eq(categoryOptions),
                any(CategoryCategoryOptionLinkModelBuilder.class));
    }

    @Test
    public void not_handle_category_option_links_for_null_category_options() {
        when(category.categoryOptions()).thenReturn(null);
        categoryHandler.handle(category);
        verify(categoryCategoryOptionLinkHandler, never()).handleMany(anyString(), anyListOf(CategoryOption.class),
                any(CategoryCategoryOptionLinkModelBuilder.class));
    }

    @Test
    public void not_handle_category_option_when_data_access_data_read_is_false() {
        when(dataAccess.read()).thenReturn(false);
        categoryHandler.handle(category);
        verify(categoryCategoryOptionLinkHandler).handleMany(
                same(categoryUid),
                eq(Collections.<CategoryOption>emptyList()),
                any(CategoryCategoryOptionLinkModelBuilder.class));
    }
}