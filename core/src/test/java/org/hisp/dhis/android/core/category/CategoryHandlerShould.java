package org.hisp.dhis.android.core.category;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;

public class CategoryHandlerShould {

    @Mock
    private Store<Category> mockCategoryStore;

    @Mock
    private Store<CategoryOption> mockCategoryOptionStore;

    @Mock
    private Store<CategoryOptionLinkModel> mockCategoryOptionLinkStore;


    private CategoryHandler categoryHandler;

    @Before
    public void setUp() throws Exception {


        MockitoAnnotations.initMocks(this);
        CategoryOptionHandler categoryOptionHandler = new CategoryOptionHandler(
                mockCategoryOptionStore);

        categoryHandler = new CategoryHandler(mockCategoryStore, categoryOptionHandler,
                mockCategoryOptionLinkStore);
    }


    @Test
    public void handle_deleted_category() {
        Category deletedCategory = givenADeletedCategory();

        categoryHandler.handle(deletedCategory);
        verify(mockCategoryStore).delete(deletedCategory);
    }

    @Test
    public void handle_new_category() {
        Category newCategory = givenACategory();

        when(mockCategoryStore.update(any(Category.class), any(Category.class))).thenReturn(false);

        categoryHandler.handle(newCategory);

        verify(mockCategoryStore).update(newCategory, newCategory);
        verify(mockCategoryStore).insert(newCategory);

    }

    @Test
    public void handle_updated_category() {
        Category updatedCategory = givenACategory();

        when(mockCategoryStore.update(any(Category.class), any(Category.class))).thenReturn(true);

        categoryHandler.handle(updatedCategory);

        verify(mockCategoryStore).update(updatedCategory, updatedCategory);
        verifyZeroInteractions(mockCategoryStore);

    }

    private Category givenADeletedCategory() {
        Date today = new Date();

        return Category.builder()
                .uid("KfdsGBcoiCa")
                .code("BIRTHS_ATTENDED")
                .created(today)
                .deleted(true)
                .name("Births attended by")
                .shortName("Births attended by")
                .displayName("Births attended by")
                .categoryOptions(new ArrayList<CategoryOption>())
                .dataDimensionType("DISAGGREGATION").build();
    }


    private Category givenACategory() {
        Date today = new Date();

        return Category.builder()
                .uid("KfdsGBcoiCa")
                .code("BIRTHS_ATTENDED")
                .created(today)
                .name("Births attended by")
                .shortName("Births attended by")
                .displayName("Births attended by")
                .categoryOptions(new ArrayList<CategoryOption>())
                .dataDimensionType("DISAGGREGATION").build();
    }
}