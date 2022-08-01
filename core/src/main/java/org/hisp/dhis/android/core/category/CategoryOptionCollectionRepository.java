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
package org.hisp.dhis.android.core.category;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyNameableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BooleanFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.category.internal.CategoryOptionFields;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class CategoryOptionCollectionRepository
        extends ReadOnlyNameableCollectionRepositoryImpl<CategoryOption, CategoryOptionCollectionRepository> {

    @Inject
    CategoryOptionCollectionRepository(final IdentifiableObjectStore<CategoryOption> store,
                                       final Map<String, ChildrenAppender<CategoryOption>> childrenAppenders,
                                       final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new CategoryOptionCollectionRepository(store, childrenAppenders, s)));
    }

    public DateFilterConnector<CategoryOptionCollectionRepository> byStartDate() {
        return cf.date(CategoryOptionTableInfo.Columns.START_DATE);
    }

    public DateFilterConnector<CategoryOptionCollectionRepository> byEndDate() {
        return cf.date(CategoryOptionTableInfo.Columns.END_DATE);
    }

    public BooleanFilterConnector<CategoryOptionCollectionRepository> byAccessDataWrite() {
        return cf.bool(CategoryOptionTableInfo.Columns.ACCESS_DATA_WRITE);
    }

    public CategoryOptionCollectionRepository byCategoryUid(String categoryUid) {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
                CategoryCategoryOptionLinkTableInfo.TABLE_INFO.name(),
                CategoryCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION,
                CategoryCategoryOptionLinkTableInfo.Columns.CATEGORY,
                Collections.singletonList(categoryUid)
        );
    }

    public CategoryOptionCollectionRepository byCategoryOptionComboUid(String categoryOptionComboUid) {
        return cf.subQuery(IdentifiableColumns.UID).inLinkTable(
                CategoryOptionComboCategoryOptionLinkTableInfo.TABLE_INFO.name(),
                CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION,
                CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION_COMBO,
                Collections.singletonList(categoryOptionComboUid)
        );
    }

    /**
     * This method only return results in versions greater or equal to 2.37.
     * @return Collection repository
     */
    public CategoryOptionCollectionRepository withOrganisationUnits() {
        return cf.withChild(CategoryOptionFields.ORGANISATION_UNITS);
    }
}
