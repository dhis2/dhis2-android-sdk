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

package org.hisp.dhis.android.core.category.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.IdentifiableStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStoreImpl;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboTableInfo;

import java.util.List;

import androidx.annotation.NonNull;

public final class CategoryOptionComboStoreImpl extends IdentifiableObjectStoreImpl<CategoryOptionCombo>
        implements CategoryOptionComboStore {

    private CategoryOptionComboStoreImpl(DatabaseAdapter databaseAdapter,
                                         SQLStatementBuilderImpl statementBuilder) {
        super(databaseAdapter, statementBuilder, BINDER, CategoryOptionCombo::create);
    }

    @Override
    public List<CategoryOptionCombo> getForCategoryCombo(String categoryComboUid) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(CategoryOptionComboTableInfo.Columns.CATEGORY_COMBO, categoryComboUid)
                .build();
        return selectWhere(whereClause);
    }

    private static StatementBinder<CategoryOptionCombo> BINDER
            = new IdentifiableStatementBinder<CategoryOptionCombo>() {

        @Override
        public void bindToStatement(@NonNull CategoryOptionCombo o, @NonNull StatementWrapper w) {
            super.bindToStatement(o, w);
            w.bind(7, UidsHelper.getUidOrNull(o.categoryCombo()));
        }
    };

    public static CategoryOptionComboStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilderImpl statementBuilder = new SQLStatementBuilderImpl(CategoryOptionComboTableInfo.TABLE_INFO);
        return new CategoryOptionComboStoreImpl(databaseAdapter, statementBuilder);
    }
}