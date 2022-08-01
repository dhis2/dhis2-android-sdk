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

package org.hisp.dhis.android.core.arch.repositories.filters.internal;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator;

import java.util.List;

public final class SubQueryFilterConnector<R extends BaseRepository>
        extends BaseAbstractFilterConnector<R, String> {

    SubQueryFilterConnector(BaseRepositoryFactory<R> repositoryFactory,
                            RepositoryScope scope,
                            String key) {
        super(repositoryFactory, scope, key);
    }

    String wrapValue(String value) {
        return value;
    }

    public R inLinkTable(String linkTable, String linkParent, String linkChild, List<String> children) {
        WhereClauseBuilder clauseBuilder = new WhereClauseBuilder().appendInKeyStringValues(linkChild, children);

        return inTableWhere(linkTable, linkParent, clauseBuilder);
    }

    public R inTableWhere(String linkTable, String linkParent, WhereClauseBuilder clauseBuilder) {
        return newWithWrappedScope(FilterItemOperator.IN, "(" + String.format(
                "SELECT DISTINCT %s FROM %s WHERE %s", linkParent, linkTable, clauseBuilder.build()) + ")");
    }

    public R inTwoLinkTable(String linkTable1, String linkParent1, String linkChild1,
                            String linkTable2, String linkParent2, String linkChild2,
                            List<String> children) {
        WhereClauseBuilder innerClauseBuilder = new WhereClauseBuilder().appendInKeyStringValues(linkChild2, children);
        String innerClause = String.format("SELECT DISTINCT %s FROM %s WHERE %s",
                linkParent2, linkTable2, innerClauseBuilder.build());

        String whereClause = linkChild1 + " IN (" + innerClause + ")";
        return newWithWrappedScope(FilterItemOperator.IN, String.format(
                "( SELECT DISTINCT %s FROM %s WHERE %s )", linkParent1, linkTable1, whereClause
        ));
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    public R withThoseChildrenExactly(String linkTable, String linkParent, String linkChild, List<String> children) {
        RepositoryScope repositoryScope = null;

        for (String child : children) {
            String clause = new WhereClauseBuilder().appendKeyStringValue(linkChild, child).build();
            String value = "(" + String.format("SELECT %s FROM %s WHERE %s ", linkParent, linkTable, clause) + ")";

            repositoryScope = repositoryScope == null ? updatedUnwrappedScope(FilterItemOperator.IN, value) :
                    updatePassedScope(FilterItemOperator.IN, value, repositoryScope);
        }

        return newWithPassedScope(FilterItemOperator.IN, "(" + String.format(
                "SELECT %s FROM %s WHERE 1 GROUP BY %s HAVING COUNT(*) = %s ",
                linkParent, linkTable, linkParent, children.size()) + ")", repositoryScope);
    }

    public R rawSubQuery(FilterItemOperator operator, String subQuery) {
        return newWithWrappedScope(operator, "(" + subQuery + ")");
    }
}