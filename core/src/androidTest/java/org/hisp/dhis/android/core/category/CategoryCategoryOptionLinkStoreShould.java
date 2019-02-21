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

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class CategoryCategoryOptionLinkStoreShould extends AbsStoreTestCase {

    private LinkModelStore<CategoryCategoryOptionLinkModel> store;
    private Category newCategory;
    private CategoryOption newCategoryOption;
    private CategoryCategoryOptionLinkModel newCategoryCategoryOptionLinkModel;
    private long lastInsertedID;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        store = CategoryCategoryOptionLinkStore.create(databaseAdapter());

    }

    @Test
    public void insert_a_category_option_link() throws Exception {
        givenACategory();
        givenACategoryOption();
        givenACategoryOptionLinkModel();

        whenInsertNewCategory();
        whenInsertNewOption();
        whenInsertNewCategoryOptionLink();

        thenAssertLastInsertedIDIsOne();
    }

    private void givenACategory() {
        Date today = new Date();

        newCategory = Category.builder()
                .uid("KfdsGBcoiCa")
                .code("BIRTHS_ATTENDED")
                .created(today)
                .name("Births attended by")
                .displayName("Births attended by")
                .categoryOptions(new ArrayList<>())
                .dataDimensionType("DISAGGREGATION").build();
    }

    private void givenACategoryOption() {
        Date today = new Date();

        newCategoryOption = CategoryOption.builder()
                .uid("TNYQzTHdoxL")
                .code("MCH_AIDES")
                .created(today)
                .name("MCH Aides")
                .shortName("MCH Aides")
                .displayName("MCH Aides")
                .build();
    }

    private void givenACategoryOptionLinkModel() {
        newCategoryCategoryOptionLinkModel = CategoryCategoryOptionLinkModel.builder()
                .option("TNYQzTHdoxL")
                .category("KfdsGBcoiCa")
                .build();
    }

    private void whenInsertNewCategoryOptionLink() {
        lastInsertedID = store.insert(newCategoryCategoryOptionLinkModel);
    }

    private void whenInsertNewCategory() {
        IdentifiableObjectStore<Category> categoryStore = CategoryStore.create(databaseAdapter());
        categoryStore.insert(newCategory);
    }

    private void whenInsertNewOption() {
        IdentifiableObjectStore<CategoryOption> optionStore = CategoryOptionStore.create(databaseAdapter());
        optionStore.insert(newCategoryOption);
    }

    private void thenAssertLastInsertedIDIsOne(){
        assertEquals(lastInsertedID, 1);
    }
}