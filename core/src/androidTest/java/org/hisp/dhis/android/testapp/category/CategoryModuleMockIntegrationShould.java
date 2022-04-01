/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *  
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.testapp.category;

import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryComboInternalAccessor;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class CategoryModuleMockIntegrationShould extends BaseMockIntegrationTestFullDispatcher {

    @Test
    public void allow_access_to_combos_without_children() {
        List<CategoryCombo> combos = d2.categoryModule().categoryCombos().blockingGet();
        assertThat(combos.size()).isEqualTo(2);
        for (CategoryCombo combo : combos) {
            assertThat(combo.categories() == null).isTrue();
            assertThat(accessCategoryOptionCombos(combo) == null).isTrue();
        }
    }

    @Test
    public void allow_access_to_combos_with_category_option_combos() {
        List<CategoryCombo> combos = d2.categoryModule().categoryCombos().withCategoryOptionCombos().blockingGet();
        assertThat(combos.size()).isEqualTo(2);
        for (CategoryCombo combo : combos) {
            assertThat(accessCategoryOptionCombos(combo) == null).isFalse();
        }
    }

    @Test
    public void allow_access_to_combos_with_categories() {
        List<CategoryCombo> combos = d2.categoryModule().categoryCombos().withCategories().blockingGet();
        assertThat(combos.size()).isEqualTo(2);
        for (CategoryCombo combo : combos) {
            assertThat(combo.categories() == null).isFalse();
        }
    }

    @Test
    public void allow_access_to_combo_by_uid_without_children() {
        CategoryCombo combo = d2.categoryModule().categoryCombos().uid("m2jTvAj5kkm").blockingGet();
        assertThat(combo.uid()).isEqualTo("m2jTvAj5kkm");
        assertThat(combo.code()).isEqualTo("BIRTHS");
        assertThat(combo.name()).isEqualTo("Births");
        assertThat(combo.categories() == null).isTrue();
        assertThat(accessCategoryOptionCombos(combo) == null).isTrue();
    }

    @Test
    public void allow_access_to_combo_by_uid_with_category_option_combos() {
        CategoryCombo combo = d2.categoryModule().categoryCombos().withCategoryOptionCombos().uid("m2jTvAj5kkm").blockingGet();
        assertThat(combo.uid()).isEqualTo("m2jTvAj5kkm");
        assertThat(combo.code()).isEqualTo("BIRTHS");
        assertThat(combo.name()).isEqualTo("Births");
        List<CategoryOptionCombo> optionCombos = accessCategoryOptionCombos(combo);
        assertThat(optionCombos == null).isFalse();
        assertThat(optionCombos.size()).isEqualTo(2);
        assertThat(optionCombos.iterator().next().name()).isEqualTo("Trained TBA, At PHU");
    }

    @Test
    public void dont_fail_when_asking_for_combos_without_children_when_not_in_database() {
        CategoryCombo combo = d2.categoryModule().categoryCombos().uid("nonExistentId").blockingGet();
        assertThat(combo == null).isTrue();
    }

    @Test
    public void dont_fail_when_asking_for_combos_with_children_when_not_in_database() {
        CategoryCombo combo = d2.categoryModule().categoryCombos().withCategories().withCategoryOptionCombos()
                .uid("nonExistentId").blockingGet();
        assertThat(combo == null).isTrue();
    }

    @Test
    public void allow_access_to_combo_by_uid_with_sorted_categories() {
        CategoryCombo combo = d2.categoryModule().categoryCombos().withCategories().uid("m2jTvAj5kkm").blockingGet();
        assertThat(combo.uid()).isEqualTo("m2jTvAj5kkm");
        assertThat(combo.code()).isEqualTo("BIRTHS");
        assertThat(combo.name()).isEqualTo("Births");

        List<Category> categories = combo.categories();
        assertThat(categories == null).isFalse();
        assertThat(categories.size()).isEqualTo(2);

        Category category0 = categories.get(0);
        assertThat(category0.uid()).isEqualTo("KfdsGBcoiCa");
        assertThat(category0.code()).isEqualTo("BIRTHS_ATTENDED");

        Category category1 = categories.get(1);
        assertThat(category1.uid()).isEqualTo("cX5k9anHEHd");
        assertThat(category1.code()).isEqualTo("GENDER");
    }

    @Test
    public void allow_access_to_categories_without_children() {
        List<Category> categories = d2.categoryModule().categories().blockingGet();
        assertThat(categories.size()).isEqualTo(4);
    }

    @Test
    public void allow_access_to_category_by_uid_without_children() {
        Category category = d2.categoryModule().categories().uid("vGs6omsRekv").blockingGet();
        assertThat(category.uid()).isEqualTo("vGs6omsRekv");
        assertThat(category.name()).isEqualTo("default");
        assertThat(category.dataDimensionType()).isEqualTo("DISAGGREGATION");
    }

    @Test
    public void allow_access_to_categories_with_category_options() {
        List<Category> categories = d2.categoryModule().categories().withCategoryOptions().blockingGet();
        assertThat(categories.size()).isEqualTo(4);
        for (Category category : categories) {
            assertThat(category.categoryOptions() == null).isFalse();
        }
    }

    @Test
    public void allow_access_to_category_by_uid_with_sorted_category_options() {
        Category category = d2.categoryModule().categories().withCategoryOptions().uid("KfdsGBcoiCa").blockingGet();
        assertThat(category.uid()).isEqualTo("KfdsGBcoiCa");
        assertThat(category.name()).isEqualTo("Births attended by");
        assertThat(category.dataDimensionType()).isEqualTo("DISAGGREGATION");

        List<CategoryOption> categoryOptions = category.categoryOptions();
        assertThat(categoryOptions == null).isFalse();
        assertThat(categoryOptions.size()).isEqualTo(3);

        CategoryOption categoryOption0 = categoryOptions.get(0);
        assertThat(categoryOption0.uid()).isEqualTo("TNYQzTHdoxL");
        assertThat(categoryOption0.code()).isEqualTo("MCH_AIDES");

        CategoryOption categoryOption1 = categoryOptions.get(1);
        assertThat(categoryOption1.uid()).isEqualTo("TXGfLxZlInA");
        assertThat(categoryOption1.code()).isEqualTo("SECHN");

        CategoryOption categoryOption2 = categoryOptions.get(2);
        assertThat(categoryOption2.uid()).isEqualTo("uZUnebiT5DI");
        assertThat(categoryOption2.code()).isEqualTo("TRAINED_TBA");
    }

    @Test
    public void allow_access_to_category_option_combos_without_children() {
        List<CategoryOptionCombo> categoryOptionCombos = d2.categoryModule().categoryOptionCombos().blockingGet();
        assertThat(categoryOptionCombos.size()).isEqualTo(4);
    }

    @Test
    public void allow_access_to_category_option_combos_with_category_options() {
        List<CategoryOptionCombo> categoryOptionCombos = d2.categoryModule().categoryOptionCombos().withCategoryOptions().blockingGet();
        assertThat(categoryOptionCombos.size()).isEqualTo(4);
        for (CategoryOptionCombo categoryOptionCombo : categoryOptionCombos) {
            assertThat(categoryOptionCombo.categoryOptions() == null).isFalse();
        }
    }

    @Test
    public void allow_access_to_category_option_combo_by_uid_without_children() {
        CategoryOptionCombo categoryOptionCombo = d2.categoryModule().categoryOptionCombos().uid("Gmbgme7z9BF").blockingGet();
        assertThat(categoryOptionCombo.uid()).isEqualTo("Gmbgme7z9BF");
        assertThat(categoryOptionCombo.name()).isEqualTo("Trained TBA, At PHU");
    }

    @Test
    public void allow_access_to_category_option_combo_by_uid_with_category_options() {
        CategoryOptionCombo categoryOptionCombo = d2.categoryModule().categoryOptionCombos()
                .withCategoryOptions().uid("Gmbgme7z9BF").blockingGet();
        assertThat(categoryOptionCombo.uid()).isEqualTo("Gmbgme7z9BF");
        assertThat(categoryOptionCombo.name()).isEqualTo("Trained TBA, At PHU");

        List<CategoryOption> categoryOptions = categoryOptionCombo.categoryOptions();
        assertThat(categoryOptions == null).isFalse();
        assertThat(categoryOptions.size()).isEqualTo(2);

        Map<String, CategoryOption> categoryOptionsMap = UidsHelper.mapByUid(categoryOptions);
        CategoryOption categoryOption0 = categoryOptionsMap.get("uZUnebiT5DI");
        assertThat(categoryOption0.uid()).isEqualTo("uZUnebiT5DI");
        assertThat(categoryOption0.name()).isEqualTo("Trained TBA");

        CategoryOption categoryOption1 = categoryOptionsMap.get("Fp4gVHbRvEV");
        assertThat(categoryOption1.uid()).isEqualTo("Fp4gVHbRvEV");
        assertThat(categoryOption1.name()).isEqualTo("At PHU");
    }

    @Test
    public void allow_access_to_category_combos_without_children() {
        List<CategoryOption> categoryOptions = d2.categoryModule().categoryOptions().blockingGet();
        assertThat(categoryOptions.size()).isEqualTo(8);
    }

    @Test
    public void allow_access_to_category_combo_by_uid_without_children() {
        CategoryOption categoryOption = d2.categoryModule().categoryOptions().uid("apsOixVZlf1").blockingGet();
        assertThat(categoryOption.uid()).isEqualTo("apsOixVZlf1");
        assertThat(categoryOption.name()).isEqualTo("Female");
        assertThat(categoryOption.code()).isEqualTo("FMLE");
    }

    private List<CategoryOptionCombo> accessCategoryOptionCombos(CategoryCombo categoryCombo) {
        return CategoryComboInternalAccessor.accessCategoryOptionCombos(categoryCombo);
    }
}