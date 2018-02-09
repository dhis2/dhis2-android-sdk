package org.hisp.dhis.android.core.category;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

public class CategoryOptionHandlerShould {

    @Mock
    private CategoryOptionStore mockCategoryOptionStore;
    @Mock
    private CategoryCategoryOptionLinkStore mockCategoryCategoryOptionStore;

    private CategoryOptionHandler categoryOptionHandler;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        categoryOptionHandler = new CategoryOptionHandler(mockCategoryOptionStore, mockCategoryCategoryOptionStore);
    }

    @Test
    public void handle_deleted_categoryOption() {
        CategoryOption deletedOption = givenADeletedOption();

        categoryOptionHandler.handle(deletedOption);
        verify(mockCategoryOptionStore).delete(deletedOption.uid());
        categoryOptionHandler.handle("", deletedOption);
        verify(mockCategoryOptionStore).delete(deletedOption);
    }

    @Test
    public void handle_new_categoryOption() {
        CategoryOption newOption = givenAOption();

        when(mockCategoryOptionStore.update(any(CategoryOption.class))).thenReturn(false);
        when(mockCategoryOptionStore.update(any(CategoryOption.class))).thenReturn(0);

        categoryOptionHandler.handle(any(String.class), newOption);

        verify(mockCategoryOptionStore).update(newOption);
        verify(mockCategoryOptionStore).insert(newOption);

    }

    @Test
    public void handle_updated_categoryOption() {
        CategoryOption updatedOption = givenAOption();

        when(mockCategoryOptionStore.update(any(CategoryOption.class))).thenReturn(true);
        when(mockCategoryOptionStore.update(any(CategoryOption.class))).thenReturn(1);

        categoryOptionHandler.handle(any(String.class), updatedOption);

        verify(mockCategoryOptionStore).update(updatedOption);
        verify(mockCategoryOptionStore).update(updatedOption);
        verifyZeroInteractions(mockCategoryOptionStore);

    }

    private CategoryOption givenADeletedOption() {
        Date today = new Date();

        return CategoryOption.builder()
                .uid("TNYQzTHdoxL")
                .code("MCH_AIDES")
                .created(today)
                .deleted(true)
                .name("MCH Aides")
                .shortName("MCH Aides")
                .displayName("MCH Aides")
                .build();
    }

    private CategoryOption givenAOption() {
        Date today = new Date();

        return CategoryOption.builder()
                .uid("TNYQzTHdoxL")
                .code("MCH_AIDES")
                .created(today)
                .name("MCH Aides")
                .shortName("MCH Aides")
                .displayName("MCH Aides")
                .build();
    }
}