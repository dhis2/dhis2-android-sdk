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
package org.hisp.dhis.android.core.arch.db;

import android.content.ContentValues;

import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScopeOrderByItem;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static org.hisp.dhis.android.core.arch.repositories.paging.RepositoryPagingConfig.PAGING_KEY;

public class OrderByClauseBuilder {

    public static String orderByFromItems(LinkedHashSet<RepositoryScopeOrderByItem> orderByItems) {
        List<String> stringList = new ArrayList<>(orderByItems.size());
        for (RepositoryScopeOrderByItem item: orderByItems) {
            stringList.add(item.toSQLString());
        }

        if (!orderByItems.contains(PAGING_KEY)) {
            stringList.add(RepositoryScopeOrderByItem.builder()
                    .column(PAGING_KEY)
                    .direction(RepositoryScope.OrderByDirection.ASC)
                    .build()
                    .toSQLString());
        }
        return Utils.commaAndSpaceSeparatedCollectionValues(stringList);
    }

    public static void addSortingClauses(WhereClauseBuilder whereClauseBuilder,
                                         LinkedHashSet<RepositoryScopeOrderByItem> orderByItems,
                                         ContentValues object,
                                         boolean reversed) {
        boolean hasPagingKey = false;
        for (RepositoryScopeOrderByItem item: orderByItems) {
            addItemOperator(whereClauseBuilder, item, object, reversed);
            if (item.column().equals(PAGING_KEY)) {
                hasPagingKey = true;
            }
        }

        if (!hasPagingKey) {
            RepositoryScopeOrderByItem pagingItem = RepositoryScopeOrderByItem.builder().column(PAGING_KEY)
                    .direction(RepositoryScope.OrderByDirection.ASC).build();
            addItemOperator(whereClauseBuilder, pagingItem, object, reversed);
        }

    }

    private static void addItemOperator(WhereClauseBuilder whereClauseBuilder, RepositoryScopeOrderByItem item,
                                        ContentValues object, boolean reversed) {
        String operator = reversed ? getReversedDirectionOperator(item) : getDirectionOperator(item);
        whereClauseBuilder.appendKeyOperatorValue(item.column(), operator, object.getAsString(item.column()));
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