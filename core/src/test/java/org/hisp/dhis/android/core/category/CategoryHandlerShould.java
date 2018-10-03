package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.OrderedLinkModelHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Matchers.any;
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

    @Mock
    private List<CategoryOption> categoryOptions;

    private SyncHandler<Category> categoryHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(category.categoryOptions()).thenReturn(categoryOptions);
        when(category.uid()).thenReturn(categoryUid);

        categoryHandler = new CategoryHandler(categoryStore, categoryOptionHandler,
                categoryCategoryOptionLinkHandler);
    }

    @Test
    public void handle_category_options() {
        categoryHandler.handle(category);
        verify(categoryOptionHandler).handleMany(categoryOptions);
    }

    @Test
    public void not_handle_category_options_for_empty_category_options() {
        when(category.categoryOptions()).thenReturn(null);
        categoryHandler.handle(category);
        verify(categoryOptionHandler, never()).handleMany(categoryOptions);
    }

    @Test
    public void handle_category_option_links() {
        categoryHandler.handle(category);
        verify(categoryCategoryOptionLinkHandler).handleMany(same(categoryUid), same(categoryOptions),
                any(CategoryCategoryOptionLinkModelBuilder.class));
    }

    @Test
    public void not_handle_category_option_links_for_empty_category_options() {
        when(category.categoryOptions()).thenReturn(null);
        categoryHandler.handle(category);
        verify(categoryCategoryOptionLinkHandler, never()).handleMany(same(categoryUid), same(categoryOptions),
                any(CategoryCategoryOptionLinkModelBuilder.class));
    }
}