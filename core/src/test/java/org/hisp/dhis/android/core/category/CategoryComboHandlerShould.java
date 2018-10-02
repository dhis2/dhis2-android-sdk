package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class CategoryComboHandlerShould {

    @Mock
    private LinkModelHandler<CategoryOption, CategoryOptionComboCategoryOptionLinkModel>
            categoryOptionComboCategoryOptionLinkHandler;

    @Mock
    private LinkModelHandler<Category, CategoryCategoryComboLinkModel> mockComboLinkStore;

    @Mock
    private SyncHandler<CategoryOptionCombo> mockOptionComboHandler;

    @Mock
    private CategoryComboStore mockComboStore;

    @Mock
    private OrphanCleaner<CategoryCombo, CategoryOptionCombo> categoryOptionCleaner;

    private CategoryComboHandler categoryComboHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        categoryComboHandler = new CategoryComboHandler(mockComboStore, categoryOptionComboCategoryOptionLinkHandler,
                mockComboLinkStore, mockOptionComboHandler, categoryOptionCleaner);
    }

    @Test
    public void handle_a_new_category_combo() {
        CategoryCombo combo = givenACategoryCombo();

        when(mockComboStore.update(any(CategoryCombo.class), any(CategoryCombo.class))).thenReturn(
                false);

        categoryComboHandler.handle(combo);

        verify(mockComboStore).update(combo, combo);
        verify(mockComboStore).insert(combo);
        verify(categoryOptionCleaner, never()).deleteOrphan(
                any(CategoryCombo.class), anyListOf(CategoryOptionCombo.class));
    }

    @Test
    public void handle_a_deleted_category_combo() {
        CategoryCombo deletedCombo = givenADeletedCategoryCombo();

        categoryComboHandler.handle(deletedCombo);
        verify(mockComboStore).delete(deletedCombo);
        verify(categoryOptionCleaner, never()).deleteOrphan(
                any(CategoryCombo.class), anyListOf(CategoryOptionCombo.class));
    }

    @Test
    public void handle_updated_category() {
        CategoryCombo updatedCombo = givenACategoryCombo();

        when(mockComboStore.update(any(CategoryCombo.class), any(CategoryCombo.class))).thenReturn(
                true);

        categoryComboHandler.handle(updatedCombo);

        verify(mockComboStore).update(updatedCombo, updatedCombo);
        verifyZeroInteractions(mockComboStore);
        verify(categoryOptionCleaner).deleteOrphan(
                any(CategoryCombo.class), anyListOf(CategoryOptionCombo.class));
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