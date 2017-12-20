package org.hisp.dhis.android.core;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboHandler;
import org.hisp.dhis.android.core.category.CategoryOptionComboStore;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

public class CategoryOptionComboHandlerShould {

    @Mock
    private CategoryOptionComboStore mockStore;

    private CategoryOptionComboHandler handler;

    @Before
    public void setUp() throws Exception {


        MockitoAnnotations.initMocks(this);
        handler = new CategoryOptionComboHandler(mockStore);
    }

    @Test
    public void handle_a_new_categoryOptionCombo() {
        CategoryOptionCombo newOptionCombo = givenAOptionCombo();

        when(mockStore.update(any(CategoryOptionCombo.class),
                any(CategoryOptionCombo.class))).thenReturn(false);

        handler.handle(newOptionCombo);

        verify(mockStore).update(newOptionCombo, newOptionCombo);
        verify(mockStore).insert(newOptionCombo);

    }

    @Test
    public void handle_a_deleted_categoryOption() {
        CategoryOptionCombo deletedOptionCombo = givenADeletedOptionCombo();

        handler.handle(deletedOptionCombo);
        verify(mockStore).delete(deletedOptionCombo);
    }

    @Test
    public void handle_an_updated_categoryOption() {
        CategoryOptionCombo updatedOption = givenAOptionCombo();

        when(mockStore.update(any(CategoryOptionCombo.class),
                any(CategoryOptionCombo.class))).thenReturn(true);

        handler.handle(updatedOption);

        verify(mockStore).update(updatedOption, updatedOption);
        verifyZeroInteractions(mockStore);

    }


    @NonNull
    private CategoryOptionCombo givenADeletedOptionCombo() {
        Date today = new Date();

        //noinspection ConstantConditions
        return CategoryOptionCombo.builder()
                .uid("NZAKyj67WW2")
                .code(null)
                .created(today)
                .deleted(true)
                .name("MCH Aides")
                .shortName("SECHN, Male")
                .displayName("SECHN, Male")
                .categoryCombo(null)
                .build();
    }

    @NonNull
    private CategoryOptionCombo givenAOptionCombo() {
        Date today = new Date();

        //noinspection ConstantConditions
        return CategoryOptionCombo.builder()
                .uid("NZAKyj67WW2")
                .code(null)
                .created(today)
                .name("MCH Aides")
                .shortName("SECHN, Male")
                .displayName("SECHN, Male")
                .categoryCombo(null)
                .build();
    }
}