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
import org.hisp.dhis.android.core.arch.repositories.collection.CollectionRepositoryFactory;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyWithUidCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeItem;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.BaseNameableObjectModel.Columns;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class CategoryComboCollectionRepository extends ReadOnlyWithUidCollectionRepositoryImpl<CategoryCombo> {

    private FilterConnectorFactory<CategoryComboCollectionRepository> cf;

    private CategoryComboCollectionRepository(final IdentifiableObjectStore<CategoryCombo> store,
                                              final Collection<ChildrenAppender<CategoryCombo>> childrenAppenders,
                                              List<RepositoryScopeItem> scope) {
        super(store, childrenAppenders, scope);
        this.cf = new FilterConnectorFactory<>(scope,
                new CollectionRepositoryFactory<CategoryComboCollectionRepository>() {

                    @Override
                    public CategoryComboCollectionRepository newWithScope(
                            List<RepositoryScopeItem> updatedScope) {
                        return new CategoryComboCollectionRepository(store, childrenAppenders, updatedScope);
                    }
                });
    }

    private CategoryComboCollectionRepository(IdentifiableObjectStore<CategoryCombo> store,
                                              Collection<ChildrenAppender<CategoryCombo>> childrenAppenders) {
        this(store, childrenAppenders, Collections.<RepositoryScopeItem>emptyList());
    }

    public StringFilterConnector<CategoryComboCollectionRepository> byUid() {
        return cf.string(Columns.UID);
    }

    public StringFilterConnector<CategoryComboCollectionRepository> byCode() {
        return cf.string(Columns.CODE);
    }

    public StringFilterConnector<CategoryComboCollectionRepository> byName() {
        return cf.string(Columns.NAME);
    }

    public StringFilterConnector<CategoryComboCollectionRepository> byDisplayName() {
        return cf.string(Columns.DISPLAY_NAME);
    }

    public DateFilterConnector<CategoryComboCollectionRepository> byCreated() {
        return cf.date(Columns.CREATED);
    }

    public DateFilterConnector<CategoryComboCollectionRepository> byLastUpdated() {
        return cf.date(BaseIdentifiableObjectModel.Columns.LAST_UPDATED);
    }

    public BooleanFilterConnector<CategoryComboCollectionRepository> byIsDefault() {
        return cf.bool(CategoryComboFields.IS_DEFAULT);
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
}