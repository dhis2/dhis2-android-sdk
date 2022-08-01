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
package org.hisp.dhis.android.core.arch.db.querybuilders.internal;

import android.content.ContentValues;

import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeOrderByItem;

import java.util.ArrayList;
import java.util.List;

public final class OrderByClauseBuilder {

    public static String orderByFromItems(List<RepositoryScopeOrderByItem> orderByItems, String pagingKey) {
        List<String> stringList = new ArrayList<>(orderByItems.size());
        boolean hasPagingKey = false;
        for (RepositoryScopeOrderByItem item: orderByItems) {
            stringList.add(item.toSQLString());
            if (item.column().equals(pagingKey)) {
                hasPagingKey = true;
            }
        }

        if (!hasPagingKey) {
            stringList.add(RepositoryScopeOrderByItem.builder()
                    .column(pagingKey)
                    .direction(RepositoryScope.OrderByDirection.ASC)
                    .build()
                    .toSQLString());
        }
        return CollectionsHelper.commaAndSpaceSeparatedCollectionValues(stringList);
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    public static void addSortingClauses(WhereClauseBuilder whereClauseBuilder,
                                         List<RepositoryScopeOrderByItem> orderByItems,
                                         ContentValues object,
                                         boolean reversed,
                                         String pagingKey) {
        boolean hasPagingKey = false;
        List<RepositoryScopeOrderByItem> items = new ArrayList<>();

        for (RepositoryScopeOrderByItem item: orderByItems) {
            items.add(item);
            if (item.column().equals(pagingKey)) {
                hasPagingKey = true;
            }
        }
        if (!hasPagingKey) {
            items.add(RepositoryScopeOrderByItem.builder().column(pagingKey)
                    .direction(RepositoryScope.OrderByDirection.ASC).build());
        }

        WhereClauseBuilder wrapperClause = new WhereClauseBuilder();
        do {
            List<RepositoryScopeOrderByItem> nextIterationItems = new ArrayList<>();
            WhereClauseBuilder subWhereClause = new WhereClauseBuilder();
            for (int i = 0; i < items.size(); i++) {
                RepositoryScopeOrderByItem item = items.get(i);
                if (i == items.size() - 1) {
                    addItemInequality(subWhereClause, item, object, reversed);
                } else {
                    addItemEquality(subWhereClause, item, object);
                    nextIterationItems.add(item);
                }
            }
            wrapperClause.appendOrComplexQuery(subWhereClause.build());
            items = nextIterationItems;
        }
        while (!items.isEmpty());

        whereClauseBuilder.appendComplexQuery(wrapperClause.build());
    }

    private static void addItemInequality(WhereClauseBuilder whereClauseBuilder, RepositoryScopeOrderByItem item,
                                          ContentValues object, boolean reversed) {
        String operator = reversed ? getReversedDirectionOperator(item) : getDirectionOperator(item);
        addItemOperator(whereClauseBuilder, item, object, operator);
    }

    private static void addItemEquality(WhereClauseBuilder whereClauseBuilder, RepositoryScopeOrderByItem item,
                                        ContentValues object) {
        String operator = "=";
        addItemOperator(whereClauseBuilder, item, object, operator);    }

    private static void addItemOperator(WhereClauseBuilder whereClauseBuilder, RepositoryScopeOrderByItem item,
                                        ContentValues object, String operator) {
        String key = item.getKey(object);
        whereClauseBuilder.appendKeyOperatorValue(item.column(), operator, key);
    }

    private static String getDirectionOperator(RepositoryScopeOrderByItem item) {
        if (item.direction() == RepositoryScope.OrderByDirection.ASC) {
            return ">";
        } else {
            return "<";
        }
    }

    private static String getReversedDirectionOperator(RepositoryScopeOrderByItem item) {
        if (item.direction() == RepositoryScope.OrderByDirection.ASC) {
            return "<";
        } else {
            return ">";
        }
    }

    private OrderByClauseBuilder() {
    }
}