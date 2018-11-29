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