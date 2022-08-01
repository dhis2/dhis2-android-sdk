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

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyIdentifiableCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboFields;
import org.hisp.dhis.android.core.category.internal.CategoryOptionComboStore;
import org.hisp.dhis.android.core.common.IdentifiableColumns;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;

@Reusable
public final class CategoryOptionComboCollectionRepository
        extends ReadOnlyIdentifiableCollectionRepositoryImpl<CategoryOptionCombo,
                CategoryOptionComboCollectionRepository> {

    @Inject
    CategoryOptionComboCollectionRepository(
            final CategoryOptionComboStore store,
            final Map<String, ChildrenAppender<CategoryOptionCombo>> childrenAppenders,
            final RepositoryScope scope) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new CategoryOptionComboCollectionRepository(store, childrenAppenders, s)));
    }

    public StringFilterConnector<CategoryOptionComboCollectionRepository> byCategoryComboUid() {
        return cf.string(CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO);
    }

    public CategoryOptionComboCollectionRepository byCategoryOptions(List<String> categoryOptionUids) {
        return cf.subQuery(IdentifiableColumns.UID).withThoseChildrenExactly(
                CategoryOptionComboCategoryOptionLinkTableInfo.TABLE_INFO.name(),
                CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION_COMBO,
                CategoryOptionComboCategoryOptionLinkTableInfo.Columns.CATEGORY_OPTION,
                categoryOptionUids);
    }

    public CategoryOptionComboCollectionRepository withCategoryOptions() {
        return cf.withChild(CategoryOptionComboFields.CATEGORY_OPTIONS);
    }
}