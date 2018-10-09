package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.common.OrderedLinkModelHandler;
import org.hisp.dhis.android.core.common.OrphanCleaner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
    private SyncHandlerWithTransformer<CategoryOptionCombo> optionComboHandler;

    @Mock
    private OrderedLinkModelHandler<Category, CategoryCategoryComboLinkModel> categoryCategoryComboLinkHandler;

    @Mock
    private OrphanCleaner<CategoryCombo, CategoryOptionCombo> categoryOptionCleaner;


    private final String comboUid = "comboId";

    @Mock
    private CategoryCombo combo;

    @Mock
    private List<CategoryOptionCombo> optionCombos;

    @Mock
    private List<Category> categories;

    private SyncHandler<CategoryCombo> categoryComboHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(combo.uid()).thenReturn(comboUid);
        when(combo.categoryOptionCombos()).thenReturn(optionCombos);
        when(combo.categories()).thenReturn(categories);

        categoryComboHandler = new CategoryComboHandler(categoryComboStore, optionComboHandler,
                categoryCategoryComboLinkHandler, categoryOptionCleaner);
    }

    @Test
    public void handle_option_combos() {
        categoryComboHandler.handle(combo);
        verify(optionComboHandler).handleMany(eq(optionCombos), any(ModelBuilder.class));
    }

    @Test
    public void handle_category_category_combo_links() {
        categoryComboHandler.handle(combo);
        verify(categoryCategoryComboLinkHandler).handleMany(same(comboUid), same(categories),
                any(CategoryCategoryComboLinkModelBuilder.class));
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