package org.hisp.dhis.android.core.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class CategoryModuleMockIntegrationShould extends MockIntegrationShould {

    @BeforeClass
    public static void setUpAll() throws Exception {
        downloadMetadata();
    }

    @Test
    public void allow_access_to_combos_without_children() {
        Set<CategoryCombo> combos = d2.categoryModule().categoryCombos.getSet();
        assertThat(combos.size(), is(2));
        for (CategoryCombo combo : combos) {
            assertThat(combo.categories() == null, is(true));
            assertThat(combo.categoryOptionCombos() == null, is(true));
        }
    }

    @Test
    public void allow_access_to_combos_with_children() {
        Set<CategoryCombo> combos = d2.categoryModule().categoryCombos.getSetWithAllChildren();
        assertThat(combos.size(), is(2));
        for (CategoryCombo combo : combos) {
            // TODO assertThat(combo.categories() == null, is(false));
            assertThat(combo.categoryOptionCombos() == null, is(false));
        }
    }

    @Test
    public void allow_access_to_combo_by_uid_without_children() {
        CategoryCombo combo = d2.categoryModule().categoryCombos.uid("m2jTvAj5kkm").get();
        assertThat(combo.uid(), is("m2jTvAj5kkm"));
        assertThat(combo.code(), is("BIRTHS"));
        assertThat(combo.name(), is("Births"));
        // TODO assertThat(combo.categories() == null, is(false));
        assertThat(combo.categoryOptionCombos() == null, is(true));
    }

    @Test
    public void allow_access_to_combo_by_uid_with_children() {
        CategoryCombo combo = d2.categoryModule().categoryCombos.uid("m2jTvAj5kkm").getWithAllChildren();
        assertThat(combo.uid(), is("m2jTvAj5kkm"));
        assertThat(combo.code(), is("BIRTHS"));
        assertThat(combo.name(), is("Births"));
        // TODO assertThat(combo.categories() == null, is(true));
        List<CategoryOptionCombo> optionCombos = combo.categoryOptionCombos();
        assertThat(optionCombos == null, is(false));
        assertThat(optionCombos.size(), is(1));
        assertThat(optionCombos.iterator().next().name(), is("Trained TBA, At PHU"));
    }

    @Test
    public void allow_access_to_categories_without_children() {
        Set<Category> categories = d2.categoryModule().categories.getSet();
        assertThat(categories.size(), is(4));
    }

    @Test
    public void allow_access_to_category_by_uid_without_children() {
        Category category = d2.categoryModule().categories.uid("vGs6omsRekv").get();
        assertThat(category.uid(), is("vGs6omsRekv"));
        assertThat(category.name(), is("default"));
        assertThat(category.dataDimensionType(), is("DISAGGREGATION"));
    }

    @Test
    public void allow_access_to_category_option_combos_without_children() {
        Set<CategoryOptionCombo> categoryOptionCombos = d2.categoryModule().categoryOptionCombos.getSet();
        assertThat(categoryOptionCombos.size(), is(2));
    }

    @Test
    public void allow_access_to_category_option_combo_by_uid_without_children() {
        CategoryOptionCombo categoryOptionCombo = d2.categoryModule().categoryOptionCombos.uid("Gmbgme7z9BF").get();
        assertThat(categoryOptionCombo.uid(), is("Gmbgme7z9BF"));
        assertThat(categoryOptionCombo.name(), is("Trained TBA, At PHU"));
    }

    @Test
    public void allow_access_to_category_combos_without_children() {
        Set<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions.getSet();
        assertThat(categoryOptions.size(), is(8));
    }

    @Test
    public void allow_access_to_category_combo_by_uid_without_children() {
        CategoryOption categoryOption = d2.categoryModule().categoryOptions.uid("apsOixVZlf1").get();
        assertThat(categoryOption.uid(), is("apsOixVZlf1"));
        assertThat(categoryOption.name(), is("Female"));
        assertThat(categoryOption.code(), is("FMLE"));
    }
}