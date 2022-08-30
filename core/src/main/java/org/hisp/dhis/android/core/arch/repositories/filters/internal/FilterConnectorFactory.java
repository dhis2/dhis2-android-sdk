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

import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeKeyOrderExtractor;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeOrderByItem;

public class FilterConnectorFactory<R extends BaseRepository> {

    private final RepositoryScope scope;
    public final BaseRepositoryFactory<R> repositoryFactory;

    public FilterConnectorFactory(RepositoryScope scope,
                                  BaseRepositoryFactory<R> repositoryFactory) {
        this.scope = scope;
        this.repositoryFactory = repositoryFactory;
    }

    public StringFilterConnector<R> string(String key) {
        return new StringFilterConnector<>(repositoryFactory, scope, key);
    }

    public BaseStringFilterConnector<R> baseString(String key) {
        return new BaseStringFilterConnector<>(repositoryFactory, scope, key);
    }

    public DateFilterConnector<R> date(String key) {
        return new DateTimeFilterConnector<>(repositoryFactory, scope, key);
    }

    public DateFilterConnector<R> simpleDate(String key) {
        return new SimpleDateFilterConnector<>(repositoryFactory, scope, key);
    }

    public BooleanFilterConnector<R> bool(String key) {
        return new BooleanFilterConnector<>(repositoryFactory, scope, key);
    }

    public IntegerFilterConnector<R> integer(String key) {
        return new IntegerFilterConnector<>(repositoryFactory, scope, key);
    }

    public DeletedFilterConnector<R> deleted(String key) {
        return new DeletedFilterConnector<>(repositoryFactory, scope, key);
    }

    public LongFilterConnector<R> longC(String key) {
        return new LongFilterConnector<>(repositoryFactory, scope, key);
    }

    public DoubleFilterConnector<R> doubleC(String key) {
        return new DoubleFilterConnector<>(repositoryFactory, scope, key);
    }

    public <E extends Enum<E>> EnumFilterConnector<R, E> enumC(String key) {
        return new EnumFilterConnector<>(repositoryFactory, scope, key);
    }

    public UnwrappedEqInFilterConnector<R> unwrappedEqIn(String key) {
        return new UnwrappedEqInFilterConnector<>(repositoryFactory, scope, key);
    }

    public SubQueryFilterConnector<R> subQuery(String key) {
        return new SubQueryFilterConnector<>(repositoryFactory, scope, key);
    }

    public ValueSubQueryFilterConnector<R> valueSubQuery(String key, String linkTable,
                                                         String linkParent, String linkChild,
                                                         String dataElementColumn, String dataElementId) {
        return new ValueSubQueryFilterConnector<>(repositoryFactory, scope, key, linkTable, linkParent, linkChild,
                dataElementColumn, dataElementId);
    }

    public R withChild(String child) {
        return repositoryFactory.updated(RepositoryScopeHelper.withChild(scope, child));
    }

    public R withOrderBy(String column, RepositoryScope.OrderByDirection direction) {
        RepositoryScopeOrderByItem item = RepositoryScopeOrderByItem.builder().column(column)
                .direction(direction).build();
        return repositoryFactory.updated(RepositoryScopeHelper.withOrderBy(scope, item));
    }

    public R withExternalOrderBy(String externalTable,
                                 String externalColumn,
                                 String externalLink,
                                 String ownLink,
                                 RepositoryScope.OrderByDirection direction) {
        return withExternalOrderBy(externalTable, externalColumn, externalLink, ownLink, direction, null);
    }

    public R withExternalOrderBy(String externalTable,
                                 String externalColumn,
                                 String externalLink,
                                 String ownLink,
                                 RepositoryScope.OrderByDirection direction,
                                 String additionalWhereClause) {

        String connectedAdditionalWhere = additionalWhereClause == null ? "" :
                " AND " + additionalWhereClause;

        String column = String.format("(SELECT %s FROM %s WHERE %s = %s %s)",
                externalColumn,
                externalTable,
                externalLink,
                ownLink,
                connectedAdditionalWhere);

        RepositoryScopeKeyOrderExtractor extractor = (contentValues, column1) -> {
            String ownLinkKey = contentValues.getAsString(ownLink);
            return String.format("(SELECT %s FROM %s WHERE %s = '%s' %s)",
                    externalColumn,
                    externalTable,
                    externalLink,
                    ownLinkKey,
                    connectedAdditionalWhere);
        };

        RepositoryScopeOrderByItem item = RepositoryScopeOrderByItem.builder().column(column)
                .direction(direction).keyExtractor(extractor).build();
        return repositoryFactory.updated(RepositoryScopeHelper.withOrderBy(scope, item));
    }

    public R withConditionalOrderBy(String conditionalColumn,
                                    String condition,
                                    String ifTrueColumn,
                                    String ifFalseColumn,
                                    RepositoryScope.OrderByDirection direction) {
        String column = String.format("(CASE WHEN %s %s THEN %s ELSE %s END)",
                conditionalColumn,
                condition,
                ifTrueColumn,
                ifFalseColumn);

        RepositoryScopeKeyOrderExtractor extractor = (contentValues, column1) -> {
            String conditionalValue = contentValues.getAsString(conditionalColumn);
            String ifTrueValue = contentValues.getAsString(ifTrueColumn);
            String ifFalseValue = contentValues.getAsString(ifFalseColumn);
            return String.format("(CASE WHEN '%s' %s THEN '%s' ELSE '%s' END)",
                    conditionalValue,
                    condition,
                    ifTrueValue,
                    ifFalseValue);
        };

        RepositoryScopeOrderByItem item = RepositoryScopeOrderByItem.builder().column(column)
                .direction(direction).keyExtractor(extractor).build();
        return repositoryFactory.updated(RepositoryScopeHelper.withOrderBy(scope, item));
    }
}
