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

import androidx.room.RoomRawQuery
import org.hisp.dhis.android.core.arch.db.access.internal.AppDatabase
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemStore
import org.hisp.dhis.android.persistence.common.querybuilders.SQLStatementBuilderImpl
import org.hisp.dhis.android.persistence.common.stores.ObjectWithoutUidStoreImpl
import org.koin.core.annotation.Singleton

@Singleton
internal class RelationshipItemStoreImpl(
    private val appDatabase: AppDatabase,
) : RelationshipItemStore, ObjectWithoutUidStoreImpl<RelationshipItem, RelationshipItemDB>(
    appDatabase.relationshipItemDao(),
    RelationshipItem::toDB,
    SQLStatementBuilderImpl(RelationshipItemTableInfo.TABLE_INFO),
) {

    @Suppress("NestedBlockDepth")
    override suspend fun getRelationshipUidsForItems(from: RelationshipItem, to: RelationshipItem): List<String> {
        val relationshipRows = getAllItemsOfSameType(from.elementType(), to.elementType())

        return relationshipRows.filter {
            it.fromElementUid == from.elementUid() && it.toElementUid == to.elementUid()
        }.map { it.relationship }
    }

    override suspend fun getForRelationshipUidAndConstraintType(
        uid: String,
        constraintType: RelationshipConstraintType,
    ): RelationshipItem? {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP, uid)
            .appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE, constraintType)
            .build()
        return selectOneWhere(whereClause)
    }

    override suspend fun getForRelationshipUid(relationshipUid: String): List<RelationshipItem> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP, relationshipUid)
            .build()
        return selectWhere(whereClause)
    }

    override suspend fun getRelatedTeiUids(trackedEntityInstanceUids: List<String>): List<String> {
        val whereFromClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                trackedEntityInstanceUids,
            )
            .appendKeyStringValue(
                RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE,
                RelationshipConstraintType.FROM,
            )
            .build()

        val relationshipItems = selectWhere(whereFromClause)
        val relationshipUids = relationshipItems.map { it.relationship()!!.uid() }

        val whereToClause = WhereClauseBuilder()
            .appendInKeyStringValues(RelationshipItemTableInfo.Columns.RELATIONSHIP, relationshipUids)
            .appendKeyStringValue(
                RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE,
                RelationshipConstraintType.TO,
            )
            .appendIsNotNullValue(RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE)
            .build()

        val relatedRelationshipItems = selectWhere(whereToClause)

        return relatedRelationshipItems.map { it.trackedEntityInstance()!!.trackedEntityInstance() }
    }

    override suspend fun getByItem(item: RelationshipItem): List<RelationshipItem> {
        val clauseBuilder = WhereClauseBuilder().apply {
            appendKeyStringValue(item.elementType(), item.elementUid())

            item.relationshipItemType()?.let {
                appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE, it.name)
            }

            item.relationship()?.let {
                appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP, it.uid())
            }
        }

        return selectWhere(clauseBuilder.build())
    }

    override suspend fun getByEntityUid(entityUid: String): List<RelationshipItem> {
        val clauseBuilder = WhereClauseBuilder().apply {
            appendOrKeyStringValue(RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE, entityUid)
            appendOrKeyStringValue(RelationshipItemTableInfo.Columns.ENROLLMENT, entityUid)
            appendOrKeyStringValue(RelationshipItemTableInfo.Columns.EVENT, entityUid)
        }

        return selectWhere(clauseBuilder.build())
    }

    internal suspend fun getAllItemsOfSameType(fromType: String, toType: String): List<RelationshipRow> {
        val dao = appDatabase.relationshipItemDao()
        val query = "SELECT " + RelationshipItemTableInfo.Columns.RELATIONSHIP + ", " +
            "MAX(CASE WHEN " + RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE + " = 'FROM' " +
            "THEN " + fromType + " END) AS fromElementUid, " +
            "MAX(CASE WHEN " + RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE + " = 'TO' " +
            "THEN " + toType + " END) AS toElementUid " +
            "FROM " + RelationshipItemTableInfo.TABLE_INFO.name() +
            " GROUP BY " + RelationshipItemTableInfo.Columns.RELATIONSHIP
        return dao.getRelationshipRow(RoomRawQuery(query))
    }
}
