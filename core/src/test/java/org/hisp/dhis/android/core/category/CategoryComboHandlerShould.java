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
import java.util.List;

public class CategoryComboHandlerShould {

    @Mock
    private CategoryCategoryComboLinkStoreImpl mockComboLinkStore;

    @Mock
    private CategoryOptionComboHandler mockOptionComboHandler;

    @Mock
    private CategoryComboStore mockCategoryComboStore;

    private CategoryComboHandler categoryComboHandler;

    @Before
    public void setUp() throws Exception {


        MockitoAnnotations.initMocks(this);


        categoryComboHandler = new CategoryComboHandler(mockCategoryComboStore,
                mockComboLinkStore, mockOptionComboHandler);
    }

    @Test
    public void handle_a_new_category_combo() {
        CategoryCombo categoryCombo = givenACategoryCombo();

        when(mockCategoryComboStore.update(any(CategoryCombo.class))).thenReturn(0);

        categoryComboHandler.handle(categoryCombo);

        verify(mockCategoryComboStore).update(categoryCombo);
        verify(mockCategoryComboStore).insert(categoryCombo);

    }

    @Test
    public void handle_a_deleted_category_combo() {
        CategoryCombo deletedCombo = givenADeletedCategoryCombo();

        categoryComboHandler.handle(deletedCombo);
        verify(mockCategoryComboStore).delete(deletedCombo.uid());
    }

    @Test
    public void handle_updated_category() {
        CategoryCombo updatedCategoryCombo = givenACategoryCombo();

        when(mockCategoryComboStore.update(any(CategoryCombo.class))).thenReturn(1);

        categoryComboHandler.handle(updatedCategoryCombo);

        verify(mockCategoryComboStore).update(updatedCategoryCombo);
        verifyZeroInteractions(mockCategoryComboStore);

    }

    private CategoryCombo givenADeletedCategoryCombo() {
        Date today = new Date();

        return CategoryCombo.builder()
                .uid("m2jTvAj5kkm")
                .deleted(true)
                .code("BIRTHS")
                .created(today)
                .name("Births")
                .displayName("Births")
                .categoryOptionCombos(givenAListOfCategoriesOptionCombo())
                .categories(givenAListOfCategories())
                .build();
    }

    private CategoryCombo givenACategoryCombo() {
        Date today = new Date();

        return CategoryCombo.builder()
                .uid("m2jTvAj5kkm")
                .code("BIRTHS")
                .created(today)
                .name("Births")
                .displayName("Births")
                .categoryOptionCombos(givenAListOfCategoriesOptionCombo())
                .categories(givenAListOfCategories())
                .build();
    }

    private List<Category> givenAListOfCategories() {
        List<Category> list = new ArrayList<>();

        list.add(givenACategory());

        return list;
    }

    private List<CategoryOptionCombo> givenAListOfCategoriesOptionCombo() {
        List<CategoryOptionCombo> list = new ArrayList<>();

        list.add(givenACategoryComboOption());

        return list;
    }

    private List<CategoryOption> givenAListOfCategoriesOptions() {
        List<CategoryOption> list = new ArrayList<>();

        list.add(givenACategoryOption());

        return list;
    }

    private CategoryOption givenACategoryOption() {
        Date today = new Date();

        return CategoryOption.builder()
                .uid("jRbMi0aBjYn")
                .code("MLE")
                .created(today)
                .name("Male")
                .shortName("Male")
                .displayName("Male")
                .build();
    }

    private CategoryOptionCombo givenACategoryComboOption() {
        Date today = new Date();

        //noinspection ConstantConditions
        return CategoryOptionCombo.builder()
                .uid("NZAKyj67WW2")
                .code("BIRTHS_ATTENDED")
                .created(today)
                .name("SECHN, Male")
                .shortName("SECHN, Male")
                .displayName("SECHN, Male")
                .categoryOptions(givenAListOfCategoriesOptions())
                .build();
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
                .build();
    }
}