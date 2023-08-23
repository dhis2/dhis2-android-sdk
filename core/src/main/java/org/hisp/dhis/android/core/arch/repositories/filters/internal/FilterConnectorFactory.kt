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

import android.content.ContentValues
import org.hisp.dhis.android.core.arch.repositories.collection.BaseRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseRepositoryFactory
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope.OrderByDirection
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeKeyOrderExtractor
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeOrderByItem

@SuppressWarnings("TooManyFunctions")

class FilterConnectorFactory<R : BaseRepository>(
    private val scope: RepositoryScope,
    val repositoryFactory: BaseRepositoryFactory<R>
) {
    fun string(key: String): StringFilterConnector<R> {
        return StringFilterConnector(repositoryFactory, scope, key)
    }

    fun baseString(key: String): BaseStringFilterConnector<R> {
        return BaseStringFilterConnector(repositoryFactory, scope, key)
    }

    fun date(key: String): DateFilterConnector<R> {
        return DateTimeFilterConnector(repositoryFactory, scope, key)
    }

    fun simpleDate(key: String): DateFilterConnector<R> {
        return SimpleDateFilterConnector(repositoryFactory, scope, key)
    }

    fun bool(key: String): BooleanFilterConnector<R> {
        return BooleanFilterConnector(repositoryFactory, scope, key)
    }

    fun integer(key: String): IntegerFilterConnector<R> {
        return IntegerFilterConnector(repositoryFactory, scope, key)
    }

    fun deleted(key: String): DeletedFilterConnector<R> {
        return DeletedFilterConnector(repositoryFactory, scope, key)
    }

    fun longC(key: String): LongFilterConnector<R> {
        return LongFilterConnector(repositoryFactory, scope, key)
    }

    fun doubleC(key: String): DoubleFilterConnector<R> {
        return DoubleFilterConnector(repositoryFactory, scope, key)
    }

    fun <E : Enum<E>> enumC(key: String): EnumFilterConnector<R, E> {
        return EnumFilterConnector(repositoryFactory, scope, key)
    }

    fun unwrappedEqIn(key: String): UnwrappedEqInFilterConnector<R> {
        return UnwrappedEqInFilterConnector(repositoryFactory, scope, key)
    }

    fun subQuery(key: String): SubQueryFilterConnector<R> {
        return SubQueryFilterConnector(repositoryFactory, scope, key)
    }

    @SuppressWarnings("LongParameterList")
    fun valueSubQuery(
        key: String,
        linkTable: String,
        linkParent: String,
        linkChild: String,
        dataElementColumn: String,
        dataElementId: String
    ): ValueSubQueryFilterConnector<R> {
        return ValueSubQueryFilterConnector(
            repositoryFactory, scope, key, linkTable, linkParent, linkChild, dataElementColumn, dataElementId
        )
    }

    fun withChild(child: String): R {
        return repositoryFactory.updated(RepositoryScopeHelper.withChild(scope, child))
    }

    fun withOrderBy(column: String, direction: OrderByDirection): R {
        val item = RepositoryScopeOrderByItem.builder().column(column).direction(direction).build()
        return repositoryFactory.updated(RepositoryScopeHelper.withOrderBy(scope, item))
    }

    @SuppressWarnings("LongParameterList")
    @JvmOverloads
    fun withExternalOrderBy(
        externalTable: String,
        externalColumn: String,
        externalLink: String,
        ownLink: String,
        direction: OrderByDirection,
        additionalWhereClause: String? = null
    ): R {
        val connectedAdditionalWhere = if (additionalWhereClause == null) "" else " AND $additionalWhereClause"
        val column = String.format(
            "(SELECT %s FROM %s WHERE %s = %s %s)",
            externalColumn,
            externalTable,
            externalLink,
            ownLink,
            connectedAdditionalWhere
        )
        val extractor = RepositoryScopeKeyOrderExtractor { contentValues: ContentValues, column1: String ->
            val ownLinkKey = contentValues.getAsString(ownLink)
            String.format(
                "(SELECT %s FROM %s WHERE %s = '%s' %s)",
                externalColumn,
                externalTable,
                externalLink,
                ownLinkKey,
                connectedAdditionalWhere
            )
        }
        val item =
            RepositoryScopeOrderByItem.builder().column(column).direction(direction).keyExtractor(extractor).build()
        return repositoryFactory.updated(RepositoryScopeHelper.withOrderBy(scope, item))
    }

    fun withConditionalOrderBy(
        conditionalColumn: String?,
        condition: String?,
        ifTrueColumn: String?,
        ifFalseColumn: String?,
        direction: OrderByDirection?
    ): R {
        val column = String.format(
            "(CASE WHEN %s %s THEN %s ELSE %s END)", conditionalColumn, condition, ifTrueColumn, ifFalseColumn
        )
        val extractor = RepositoryScopeKeyOrderExtractor { contentValues: ContentValues, column1: String ->
            val conditionalValue = contentValues.getAsString(conditionalColumn)
            val ifTrueValue = contentValues.getAsString(ifTrueColumn)
            val ifFalseValue = contentValues.getAsString(ifFalseColumn)
            String.format(
                "(CASE WHEN '%s' %s THEN '%s' ELSE '%s' END)", conditionalValue, condition, ifTrueValue, ifFalseValue
            )
        }
        val item =
            RepositoryScopeOrderByItem.builder().column(column).direction(direction).keyExtractor(extractor).build()
        return repositoryFactory.updated(RepositoryScopeHelper.withOrderBy(scope, item))
    }
}
