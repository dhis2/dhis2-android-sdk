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

package org.hisp.dhis.android.core.category.internal;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLink;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.category.CategoryOption;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;

import java.util.HashSet;
import java.util.List;

import io.reactivex.Single;

public class CategoryComboEndpointCallRealIntegrationShould extends BaseRealIntegrationTest {

    //@Test
    public void download_categories_combos_and_relatives() {
        d2.userModule().logIn(username, password, url).blockingGet();

        d2.databaseAdapter().setForeignKeyConstraintsEnabled(false);

        assertNotCombosInDB();
        assertThat(getCategoryCategoryComboLinks().isEmpty()).isTrue();

        Single<List<CategoryCombo>> categoryComboEndpointCall =
                getD2DIComponent(d2).internalModules().category.categoryComboCall.download(
                        new HashSet<>(Lists.newArrayList("bjDvmb4bfuf")));
        List<CategoryCombo> categoryCombos = categoryComboEndpointCall.blockingGet();

        assertThat(categoryCombos.isEmpty()).isFalse();

        downloadCategories();

        assertDataIsProperlyParsedAndInsertedInTheDB();
    }

    private void assertDataIsProperlyParsedAndInsertedInTheDB() {
        assertThereAreCombosInDB();
        assertThat(getCategoryCategoryComboLinks().isEmpty()).isFalse();
        assertThereAreCategoryOptionCombosInDB();
        assertThereAreCategoriesInDB();
    }

    private void downloadCategories() {
        getD2DIComponent(d2).internalModules().category.categoryCall.download(
                new HashSet<>(Lists.newArrayList("GLevLNI9wkl"))).blockingGet();
    }

    private void assertNotCombosInDB() {
        IdentifiableObjectStore<CategoryCombo> categoryComboStore = CategoryComboStore.create(d2.databaseAdapter());
        List<CategoryCombo> categoryCombos = categoryComboStore.selectAll();
        assertThat(categoryCombos.isEmpty()).isTrue();
    }

    private void assertThereAreCombosInDB() {
        IdentifiableObjectStore<CategoryCombo> categoryComboStore = CategoryComboStore.create(d2.databaseAdapter());
        List<CategoryCombo> categoryCombos = categoryComboStore.selectAll();
        assertThat(categoryCombos.size() > 0).isTrue();
    }

    private List<CategoryCategoryComboLink> getCategoryCategoryComboLinks() {
        LinkStore<CategoryCategoryComboLink>
                categoryCategoryComboLinkStore = CategoryCategoryComboLinkStore.create(d2.databaseAdapter());
        return categoryCategoryComboLinkStore.selectAll();
    }

    private void assertThereAreCategoryOptionCombosInDB() {
        IdentifiableObjectStore<CategoryOptionCombo> categoryOptionComboStore = CategoryOptionComboStoreImpl.create(d2.databaseAdapter());
        List<CategoryOptionCombo> categoryOptionCombos = categoryOptionComboStore.selectAll();
        assertThat(categoryOptionCombos.size() > 0).isTrue();
    }

    private void assertThereAreCategoriesInDB() {
        IdentifiableObjectStore<CategoryOption> categoryOptionStore = CategoryOptionStore.create(d2.databaseAdapter());
        List<String> categoryOptionUids = categoryOptionStore.selectUids();
        assertThat(categoryOptionUids.size() > 0).isTrue();
    }
}
