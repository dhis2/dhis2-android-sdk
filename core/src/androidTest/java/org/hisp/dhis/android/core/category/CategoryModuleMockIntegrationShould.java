package org.hisp.dhis.android.core.category;

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.MockIntegrationShould;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

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
        List<CategoryCombo> combos = d2.categoryModule().categoryCombos.get();
        assertThat(combos.size(), is(2));
        for (CategoryCombo combo : combos) {
            assertThat(combo.categories() == null, is(true));
            assertThat(combo.categoryOptionCombos() == null, is(true));
        }
    }

    @Test
    public void allow_access_to_combos_with_category_option_combos() {
        List<CategoryCombo> combos = d2.categoryModule().categoryCombos.getWithAllChildren();
        assertThat(combos.size(), is(2));
        for (CategoryCombo combo : combos) {
            assertThat(combo.categoryOptionCombos() == null, is(false));
        }
    }

    @Test
    public void allow_access_to_combos_with_categories() {
        List<CategoryCombo> combos = d2.categoryModule().categoryCombos.getWithAllChildren();
        assertThat(combos.size(), is(2));
        for (CategoryCombo combo : combos) {
            assertThat(combo.categories() == null, is(false));
        }
    }

    @Test
    public void allow_access_to_combo_by_uid_without_children() {
        CategoryCombo combo = d2.categoryModule().categoryCombos.uid("m2jTvAj5kkm").get();
        assertThat(combo.uid(), is("m2jTvAj5kkm"));
        assertThat(combo.code(), is("BIRTHS"));
        assertThat(combo.name(), is("Births"));
        assertThat(combo.categories() == null, is(true));
        assertThat(combo.categoryOptionCombos() == null, is(true));
    }

    @Test
    public void allow_access_to_combo_by_uid_with_category_option_combos() {
        CategoryCombo combo = d2.categoryModule().categoryCombos.uid("m2jTvAj5kkm").getWithAllChildren();
        assertThat(combo.uid(), is("m2jTvAj5kkm"));
        assertThat(combo.code(), is("BIRTHS"));
        assertThat(combo.name(), is("Births"));
        List<CategoryOptionCombo> optionCombos = combo.categoryOptionCombos();
        assertThat(optionCombos == null, is(false));
        assertThat(optionCombos.size(), is(1));
        assertThat(optionCombos.iterator().next().name(), is("Trained TBA, At PHU"));
    }

    @Test
    public void allow_access_to_combo_by_uid_with_categories() {
        CategoryCombo combo = d2.categoryModule().categoryCombos.uid("m2jTvAj5kkm").getWithAllChildren();
        assertThat(combo.uid(), is("m2jTvAj5kkm"));
        assertThat(combo.code(), is("BIRTHS"));
        assertThat(combo.name(), is("Births"));
        List<Category> categories = combo.categories();
        assertThat(combo.categories() == null, is(false));
        assertThat(categories.size(), is(2));
        assertThat(categories.iterator().next().code(), is("BIRTHS_ATTENDED"));
    }

    @Test
    public void allow_access_to_categories_without_children() {
        List<Category> categories = d2.categoryModule().categories.get();
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
    public void allow_access_to_categories_with_category_options() {
        List<Category> categories = d2.categoryModule().categories.getWithAllChildren();
        assertThat(categories.size(), is(4));
        for (Category category : categories) {
            assertThat(category.categoryOptions() == null, is(false));
        }
    }

    @Test
    public void allow_access_to_category_by_uid_with_category_options() {
        Category category = d2.categoryModule().categories.uid("vGs6omsRekv").getWithAllChildren();
        assertThat(category.uid(), is("vGs6omsRekv"));
        assertThat(category.name(), is("default"));
        assertThat(category.dataDimensionType(), is("DISAGGREGATION"));
        List<CategoryOption> categoryOptions = category.categoryOptions();
        assertThat(categoryOptions == null, is(false));
        assertThat(categoryOptions.size(), is(1));
        assertThat(categoryOptions.iterator().next().uid(), is("as6ygGvUGNg"));
    }

    @Test
    public void allow_access_to_category_option_combos_without_children() {
        List<CategoryOptionCombo> categoryOptionCombos = d2.categoryModule().categoryOptionCombos.get();
        assertThat(categoryOptionCombos.size(), is(2));
    }

    @Test
    public void allow_access_to_category_option_combos_with_category_options() {
        List<CategoryOptionCombo> categoryOptionCombos = d2.categoryModule().categoryOptionCombos.getWithAllChildren();
        assertThat(categoryOptionCombos.size(), is(2));
        for (CategoryOptionCombo categoryOptionCombo : categoryOptionCombos) {
            assertThat(categoryOptionCombo.categoryOptions() == null, is(false));
        }
    }

    @Test
    public void allow_access_to_category_option_combo_by_uid_without_children() {
        CategoryOptionCombo categoryOptionCombo = d2.categoryModule().categoryOptionCombos.uid("Gmbgme7z9BF").get();
        assertThat(categoryOptionCombo.uid(), is("Gmbgme7z9BF"));
        assertThat(categoryOptionCombo.name(), is("Trained TBA, At PHU"));
    }

    @Test
    public void allow_access_to_category_option_combo_by_uid_with_category_options() {
        CategoryOptionCombo categoryOptionCombo = d2.categoryModule().categoryOptionCombos.uid("Gmbgme7z9BF").getWithAllChildren();
        assertThat(categoryOptionCombo.uid(), is("Gmbgme7z9BF"));
        assertThat(categoryOptionCombo.name(), is("Trained TBA, At PHU"));

        List<CategoryOption> categoryOptions = categoryOptionCombo.categoryOptions();
        assertThat(categoryOptions == null, is(false));
        assertThat(categoryOptions.size(), is(2));

        Map<String, CategoryOption> categoryOptionsMap = UidsHelper.mapByUid(categoryOptions);
        CategoryOption categoryOption1 = categoryOptionsMap.get("uZUnebiT5DI");
        assertThat(categoryOption1.uid(), is("uZUnebiT5DI"));
        assertThat(categoryOption1.name(), is("Trained TBA"));

        CategoryOption categoryOption2 = categoryOptionsMap.get("Fp4gVHbRvEV");
        assertThat(categoryOption2.uid(), is("Fp4gVHbRvEV"));
        assertThat(categoryOption2.name(), is("At PHU"));
    }

    @Test
    public void allow_access_to_category_combos_without_children() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions.get();
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