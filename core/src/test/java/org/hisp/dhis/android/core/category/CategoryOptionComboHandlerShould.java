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

public class CategoryOptionComboHandlerShould {

    @Mock
    private CategoryOptionComboStore mockStore;
    @Mock
    private CategoryOptionComboCategoryLinkStore categoryOptionComboStore;

    private CategoryOptionComboHandler handler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        handler = new CategoryOptionComboHandler(mockStore, categoryOptionComboStore);
    }

    @Test
    public void handle_a_new_combo_option() {
        CategoryOptionCombo newOptionCombo = givenAOptionCombo();

        when(mockStore.update(any(CategoryOptionCombo.class))).thenReturn(0);

        handler.handle(newOptionCombo);

        verify(mockStore).update(newOptionCombo);
        verify(mockStore).insert(newOptionCombo);

    }

    @Test
    public void handle_a_deleted_combo_option() {
        CategoryOptionCombo deletedComboOption = givenADeletedOptionCombo();

        handler.handle(deletedComboOption);
        verify(mockStore).delete(deletedComboOption.uid());
    }

    @Test
    public void handle_an_updated_combo_option() {
        CategoryOptionCombo updatedOptionCombo = givenAOptionCombo();

        when(mockStore.update(any(CategoryOptionCombo.class))).thenReturn(1);

        handler.handle(updatedOptionCombo);

        verify(mockStore).update(updatedOptionCombo);
        verifyZeroInteractions(mockStore);

    }

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

    private CategoryOptionCombo givenADeletedOptionCombo() {
        Date today = new Date();

        //noinspection ConstantConditions
        return CategoryOptionCombo.builder()
                .uid("NZAKyj67WW2")
                .code(null)
                .deleted(true)
                .created(today)
                .name("MCH Aides")
                .shortName("SECHN, Male")
                .displayName("SECHN, Male")
                .categoryCombo(null)
                .build();
    }
}