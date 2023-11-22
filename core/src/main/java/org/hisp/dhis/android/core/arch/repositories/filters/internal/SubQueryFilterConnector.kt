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
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator

class SubQueryFilterConnector<R : BaseRepository> internal constructor(
    repositoryFactory: BaseRepositoryFactory<R>,
    scope: RepositoryScope,
    key: String,
) : BaseAbstractFilterConnector<R, String>(
    repositoryFactory,
    scope,
    key,
) {
    override fun wrapValue(value: String?): String? {
        return value
    }

    fun inLinkTable(
        linkTable: String,
        linkParent: String,
        linkChild: String,
        children: List<String>,
    ): R {
        val clauseBuilder = WhereClauseBuilder().appendInKeyStringValues(linkChild, children)
        return inTableWhere(linkTable, linkParent, clauseBuilder)
    }

    fun inTableWhere(
        linkTable: String,
        linkParent: String,
        clauseBuilder: WhereClauseBuilder,
    ): R {
        return newWithWrappedScope(
            FilterItemOperator.IN,
            "(SELECT DISTINCT $linkParent FROM $linkTable WHERE ${clauseBuilder.build()})",
        )
    }

    @SuppressWarnings("LongParameterList")
    fun inTwoLinkTable(
        linkTable1: String,
        linkParent1: String,
        linkChild1: String,
        linkTable2: String,
        linkParent2: String,
        linkChild2: String,
        children: List<String>,
    ): R {
        val innerClauseBuilder = WhereClauseBuilder().appendInKeyStringValues(linkChild2, children)
        val innerClause = "SELECT DISTINCT $linkParent2 FROM $linkTable2 WHERE ${innerClauseBuilder.build()}"
        val whereClause = "$linkChild1 IN ($innerClause)"
        return newWithWrappedScope(
            FilterItemOperator.IN,
            "(SELECT DISTINCT $linkParent1 FROM $linkTable1 WHERE $whereClause)",
        )
    }

    fun withThoseChildrenExactly(
        linkTable: String,
        linkParent: String,
        linkChild: String,
        children: List<String>,
    ): R {
        val repositoryScope = children.foldRight(scope) { child: String, scope: RepositoryScope ->
            val clause = WhereClauseBuilder().appendKeyStringValue(linkChild, child).build()
            val subClause = "(SELECT $linkParent FROM $linkTable WHERE $clause)"

            updatePassedScope(FilterItemOperator.IN, subClause, scope)
        }

        val exactCountSubClause =
            "(SELECT $linkParent FROM $linkTable WHERE 1 GROUP BY $linkParent HAVING COUNT(*) = ${children.size})"

        return newWithPassedScope(FilterItemOperator.IN, exactCountSubClause, repositoryScope)
    }

    fun rawSubQuery(operator: FilterItemOperator, subQuery: String): R {
        return newWithWrappedScope(operator, "($subQuery)")
    }
}
