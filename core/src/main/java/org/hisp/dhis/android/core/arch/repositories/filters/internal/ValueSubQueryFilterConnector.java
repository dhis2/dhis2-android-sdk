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

import java.util.Collection;

import static org.hisp.dhis.android.core.arch.repositories.filters.internal.BaseAbstractFilterConnector.escapeQuotes;

public final class ValueSubQueryFilterConnector<R extends BaseRepository> extends BaseSubQueryFilterConnector<R> {

    private final String linkChild;
    private final String dataElementColumn;
    private final String dataElementId;

    ValueSubQueryFilterConnector(BaseRepositoryFactory<R> repositoryFactory,
                                 RepositoryScope scope,
                                 String key,
                                 String linkTable,
                                 String linkParent,
                                 String linkChild,
                                 String dataElementColumn,
                                 String dataElementId) {
        super(repositoryFactory, scope, key, linkTable, linkParent);
        this.linkChild = linkChild;
        this.dataElementColumn = dataElementColumn;
        this.dataElementId = dataElementId;
    }

    String wrapValue(String value) {
        return "'" + escapeQuotes(value) + "'";
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value equal to the value provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R eq(String value) {
        return inLinkTable(FilterItemOperator.EQ, wrapValue(value));
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value lower or equal than the value provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R le(String value) {
        return inLinkTable(FilterItemOperator.LE, wrapValue(value));
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value strictly lower than the value provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R lt(String value) {
        return inLinkTable(FilterItemOperator.LT, wrapValue(value));
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value strictly greater or equal than the value provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R ge(String value) {
        return inLinkTable(FilterItemOperator.GE, wrapValue(value));
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value strictly greater than the value provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R gt(String value) {
        return inLinkTable(FilterItemOperator.GT, wrapValue(value));
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value included in the list provided.
     * @param values value list to compare with the target field
     * @return the new repository
     */
    public R in(Collection<String> values) {
        return inLinkTable(FilterItemOperator.IN, "(" + getCommaSeparatedValues(values) + ")");
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value which contains the value provided. The comparison
     * is case insensitive.
     * @param value value to compare with the target field
     * @return the new repository
     */
    public R like(String value) {
        return inLinkTable(FilterItemOperator.LIKE, wrapValue("%" + value + "%"));
    }

    private R inLinkTable(FilterItemOperator operator, String value) {
        WhereClauseBuilder clauseBuilder = new WhereClauseBuilder()
                .appendKeyOperatorValue(linkChild, operator.getSqlOperator(), value)
                .appendKeyStringValue(dataElementColumn, dataElementId);

        return inTableWhere(clauseBuilder);
    }
}