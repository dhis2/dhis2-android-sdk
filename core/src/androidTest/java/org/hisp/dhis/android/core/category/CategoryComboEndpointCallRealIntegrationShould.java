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

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelStore;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.junit.Before;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class CategoryComboEndpointCallRealIntegrationShould extends AbsStoreTestCase {

    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();
        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());
    }

    //@Test
    public void download_categories_combos_and_relatives() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();

        d2.databaseAdapter().database().setForeignKeyConstraintsEnabled(false);

        assertNotCombosInDB();
        assertTrue(getCategoryCategoryComboLinkModels().isEmpty());

        Callable<List<CategoryCombo>> categoryComboEndpointCall =
                getD2DIComponent(d2).internalModules().category.categoryComboCallFactory.create(
                        new HashSet<>(Lists.newArrayList("bjDvmb4bfuf")));
        List<CategoryCombo> categoryCombos = categoryComboEndpointCall.call();

        assertFalse(categoryCombos.isEmpty());

        downloadCategories();

        assertDataIsProperlyParsedAndInsertedInTheDB();
    }

    private void assertDataIsProperlyParsedAndInsertedInTheDB() {
        assertThereAreCombosInDB();
        assertFalse(getCategoryCategoryComboLinkModels().isEmpty());
        assertThereAreCategoryOptionCombosInDB();
        assertThereAreCategoriesInDB();
    }

    private void downloadCategories() throws Exception {
        getD2DIComponent(d2).internalModules().category.categoryCallFactory.create(
                new HashSet<>(Lists.newArrayList("GLevLNI9wkl"))).call();
    }

    private void assertNotCombosInDB() {
        IdentifiableObjectStore<CategoryCombo> categoryComboStore = CategoryComboStore.create(databaseAdapter());
        List<CategoryCombo> categoryCombos = categoryComboStore.selectAll();
        assertTrue(categoryCombos.isEmpty());
    }

    private void assertThereAreCombosInDB() {
        IdentifiableObjectStore<CategoryCombo> categoryComboStore = CategoryComboStore.create(databaseAdapter());
        List<CategoryCombo> categoryCombos = categoryComboStore.selectAll();
        assertTrue(categoryCombos.size() > 0);
    }

    private List<CategoryCategoryComboLinkModel> getCategoryCategoryComboLinkModels() {
        LinkModelStore<CategoryCategoryComboLinkModel>
                categoryCategoryComboLinkStore = CategoryCategoryComboLinkStore.create(databaseAdapter());
        return categoryCategoryComboLinkStore.selectAll();
    }

    private void assertThereAreCategoryOptionCombosInDB() {
        IdentifiableObjectStore<CategoryOptionCombo> categoryOptionComboStore = CategoryOptionComboStoreImpl.create(databaseAdapter());
        List<CategoryOptionCombo> categoryOptionCombos = categoryOptionComboStore.selectAll();
        assertTrue(categoryOptionCombos.size() > 0);
    }

    private void assertThereAreCategoriesInDB() {
        IdentifiableObjectStore<CategoryOption> categoryOptionStore = CategoryOptionStore.create(databaseAdapter());
        List<String> categoryOptionUids = categoryOptionStore.selectUids();
        assertTrue(categoryOptionUids.size() > 0);
    }
}
