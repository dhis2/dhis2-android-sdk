/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.arch.repositories.filters.internal

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.BaseAbstractFilterConnector.Companion.escapeQuotes
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator

class ValueSubQueryFilterConnector<R : BaseRepository> internal constructor(
    repositoryFactory: BaseRepositoryFactory<R>,
    scope: RepositoryScope,
    key: String,
    linkTable: String,
    linkParent: String,
    private val linkChild: String,
    private val dataElementColumn: String,
    private val dataElementId: String,
) : BaseSubQueryFilterConnector<R>(repositoryFactory, scope, key, linkTable, linkParent) {
    override fun wrapValue(value: String?): String {
        return "'" + escapeQuotes(value) + "'"
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value equal to the value provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun eq(value: String): R {
        return inLinkTable(FilterItemOperator.EQ, wrapValue(value))
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value lower or equal than the value provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun le(value: String): R {
        return inLinkTable(FilterItemOperator.LE, wrapValue(value))
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value strictly lower than the value provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun lt(value: String): R {
        return inLinkTable(FilterItemOperator.LT, wrapValue(value))
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value strictly greater or equal than the value provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun ge(value: String): R {
        return inLinkTable(FilterItemOperator.GE, wrapValue(value))
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value strictly greater than the value provided.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun gt(value: String): R {
        return inLinkTable(FilterItemOperator.GT, wrapValue(value))
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value included in the list provided.
     * @param values value list to compare with the target field
     * @return the new repository
     */
    fun `in`(values: Collection<String>): R {
        return inLinkTable(FilterItemOperator.IN, "(" + getCommaSeparatedValues(values) + ")")
    }

    /**
     * Returns a new repository whose scope is the one of the current repository plus the new filter being applied.
     * The like filter checks if the given field has a value which contains the value provided. The comparison
     * is case insensitive.
     * @param value value to compare with the target field
     * @return the new repository
     */
    fun like(value: String): R {
        return inLinkTable(FilterItemOperator.LIKE, wrapValue("%$value%"))
    }

    private fun inLinkTable(operator: FilterItemOperator, value: String): R {
        val whereClause = WhereClauseBuilder()
            .appendKeyOperatorValue(linkChild, operator.sqlOperator, value)
            .appendKeyStringValue(dataElementColumn, dataElementId)
            .build()
        return inTableWhere(whereClause)
    }
}
