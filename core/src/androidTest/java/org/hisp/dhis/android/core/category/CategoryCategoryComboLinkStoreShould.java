/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.category;

import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CategoryCategoryComboLinkStoreShould extends AbsStoreTestCase {

    private LinkModelStore<CategoryCategoryComboLink> store;
    private Category newCategory;
    private CategoryCombo newCategoryCombo;
    private CategoryCategoryComboLink newCategoryCategoryComboLink;
    private long lastInsertedID;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = CategoryCategoryComboLinkStore.create(databaseAdapter());

    }

    @Test
    public void insert_a_category_combo_link() throws Exception {
        givenACategory();
        givenACategoryCombo();
        givenACategoryComboLinkModel();

        whenInsertNewCategory();
        whenInsertNewCategoryCombo();
        whenInsertNewCategoryComboLink();

        thenAssertLastInsertedIDIsOne();
    }

    private void givenACategory() {
        newCategory = generateCategory();
    }

    private Category generateCategory(){
        Date today = new Date();
        return Category.builder()
                .uid("KfdsGBcoiCa")
                .code("BIRTHS_ATTENDED")
                .created(today)
                .name("Births attended by")
                .displayName("Births attended by")
                .dataDimensionType("DISAGGREGATION").build();
    }

    private void givenACategoryCombo() {
        Date today = new Date();

        newCategoryCombo = CategoryCombo.builder()
                .uid("m2jTvAj5kkm")
                .code("BIRTHS")
                .created(today)
                .name("Births")
                .displayName("Births")
                .categories(generateAListOfCategories())
                .build();
    }

    private void givenACategoryComboLinkModel(){
        newCategoryCategoryComboLink = CategoryCategoryComboLink.builder()
                .category("KfdsGBcoiCa")
                .categoryCombo("m2jTvAj5kkm")
                .build();
    }

    private void whenInsertNewCategory() {
        IdentifiableObjectStore<Category> categoryStore = CategoryStore.create(databaseAdapter());
        categoryStore.insert(newCategory);
    }

    private void whenInsertNewCategoryCombo() {
        IdentifiableObjectStore<CategoryCombo> comboStore = CategoryComboStore.create(databaseAdapter());
        comboStore.insert(newCategoryCombo);
    }

    private void whenInsertNewCategoryComboLink() {
        lastInsertedID = store.insert(newCategoryCategoryComboLink);
    }

    private List<Category> generateAListOfCategories() {
        List<Category> list = new ArrayList<>();
        list.add(generateCategory());
        return list;
    }

    private void thenAssertLastInsertedIDIsOne() {
        assertEquals(lastInsertedID, 1);
    }
}