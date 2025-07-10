/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.persistence.relationship

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.hisp.dhis.android.persistence.common.querybuilders.IdentifiableDeletableDataObjectSQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.IdentifiableDeletableDataObjectStoreImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class RelationshipStoreImpl(
    private val databaseAdapter: DatabaseAdapter
) : RelationshipStore, IdentifiableDeletableDataObjectStoreImpl<Relationship, RelationshipDB>(
    { databaseAdapter.getCurrentDatabase()?.relationshipDao()!! },
    Relationship::toDB,
    IdentifiableDeletableDataObjectSQLStatementBuilderImpl(RelationshipTableInfo.TABLE_INFO),
) {
    override suspend fun getRelationshipsByItem(relationshipItem: RelationshipItem): List<Relationship> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(
                "RelationshipItem." + relationshipItem.elementType(),
                relationshipItem.elementUid(),
            )
            .build()

        val queryStatement = "SELECT DISTINCT Relationship.* " +
            "FROM (Relationship INNER JOIN RelationshipItem " +
            "ON Relationship.uid = RelationshipItem.relationship) " +
            "WHERE " + whereClause + ";"

        return selectWhere(queryStatement)
    }

    override suspend fun getRelationshipsByItem(
        relationshipItem: RelationshipItem,
        type: RelationshipConstraintType?,
    ): List<Relationship> {
        val relationshipTable = RelationshipTableInfo.TABLE_INFO.name()
        val itemTable = RelationshipItemTableInfo.TABLE_INFO.name()

        val builder = WhereClauseBuilder()
            .appendKeyStringValue(
                "$itemTable.${relationshipItem.elementType()}",
                relationshipItem.elementUid(),
            )

        val whereClause =
            if (type == null) {
                builder.build()
            } else {
                builder.appendKeyStringValue(
                    "$itemTable.${RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE}",
                    type.name,
                ).build()
            }

        val queryStatement = "SELECT DISTINCT $relationshipTable.* " +
            "FROM ($relationshipTable INNER JOIN $itemTable " +
            "ON $relationshipTable.${RelationshipTableInfo.Columns.UID} = " +
            "$itemTable.${RelationshipItemTableInfo.Columns.RELATIONSHIP}) " +
            "WHERE " + whereClause + ";"

        return selectWhere(queryStatement)
    }
}
