/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Arrays;
import java.util.Collection;

final class CategoryComboCollectionRepository extends ReadOnlyIdentifiableCollectionRepositoryImpl<CategoryCombo> {

    private final IdentifiableObjectStore<CategoryCombo> store;
    private final String scope;

    CategoryComboCollectionRepository(IdentifiableObjectStore<CategoryCombo> store,
                                      Collection<ChildrenAppender<CategoryCombo>> childrenAppenders) {
        this(store, childrenAppenders,"");
    }

    private CategoryComboCollectionRepository(IdentifiableObjectStore<CategoryCombo> store,
                                              Collection<ChildrenAppender<CategoryCombo>> childrenAppenders,
                                              String scope) {
        super(store, childrenAppenders);
        this.store = store;
        this.scope = scope;
    }

    static CategoryComboCollectionRepository create(DatabaseAdapter databaseAdapter) {
        return new CategoryComboCollectionRepository(
                CategoryComboStore.create(databaseAdapter),
                Arrays.asList(
                        CategoryCategoryComboChildrenAppender.create(databaseAdapter),
                        CategoryOptionComboChildrenAppender.create(databaseAdapter)
                )
        );
    }

    public CategoryComboCollectionRepositoryBuilder byName() {
        return new CategoryComboCollectionRepositoryBuilder(this, scope + "byName");
    }

    public CategoryComboCollectionRepositoryBuilder byCode() {
        return new CategoryComboCollectionRepositoryBuilder(this, scope + "byCode");
    }

    CategoryComboCollectionRepository newWithUpdatedScope(String updatedScope) {
        return new CategoryComboCollectionRepository(store, childrenAppenders, updatedScope);
    }

    public String getScope() {
        return scope;
    }
}
