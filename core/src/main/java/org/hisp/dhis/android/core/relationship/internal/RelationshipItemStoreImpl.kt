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
package org.hisp.dhis.android.core.relationship.internal

import android.database.Cursor
import java.util.*
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementWrapper
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidOrNull
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo

internal class RelationshipItemStoreImpl private constructor(
    databaseAdapter: DatabaseAdapter,
    builder: SQLStatementBuilderImpl
) : ObjectWithoutUidStoreImpl<RelationshipItem>(
    databaseAdapter,
    builder,
    BINDER,
    WHERE_UPDATE_BINDER,
    WHERE_DELETE_BINDER,
    { cursor: Cursor -> RelationshipItem.create(cursor) }
),
    RelationshipItemStore {

    @Suppress("NestedBlockDepth")
    override fun getRelationshipUidsForItems(from: RelationshipItem, to: RelationshipItem): List<String> {
        val relationships: MutableList<String> = ArrayList()

        getAllItemsOfSameType(from, to).use { cursor ->
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val relationshipInDb = cursor.getString(0)
                    val fromElementUidInDb = cursor.getString(1)
                    val toElementUidInDb = cursor.getString(2)
                    if (from.elementUid() == fromElementUidInDb && to.elementUid() == toElementUidInDb) {
                        relationships.add(relationshipInDb)
                    }
                } while (cursor.moveToNext())
            }
        }
        return relationships
    }

    override fun getForRelationshipUidAndConstraintType(
        uid: String,
        constraintType: RelationshipConstraintType
    ): RelationshipItem? {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP, uid)
            .appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE, constraintType)
            .build()
        return selectOneWhere(whereClause)
    }

    override fun getForRelationshipUid(relationshipUid: String): List<RelationshipItem> {
        val whereClause = WhereClauseBuilder()
            .appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP, relationshipUid)
            .build()
        return selectWhere(whereClause)
    }

    override fun getRelatedTeiUids(trackedEntityInstanceUids: List<String>): List<String> {
        val whereFromClause = WhereClauseBuilder()
            .appendInKeyStringValues(
                RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE,
                trackedEntityInstanceUids
            )
            .appendKeyStringValue(
                RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE,
                RelationshipConstraintType.FROM
            )
            .build()

        val relationshipItems = selectWhere(whereFromClause)
        val relationshipUids = relationshipItems.map { it.relationship()!!.uid() }

        val whereToClause = WhereClauseBuilder()
            .appendInKeyStringValues(RelationshipItemTableInfo.Columns.RELATIONSHIP, relationshipUids)
            .appendKeyStringValue(
                RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE,
                RelationshipConstraintType.TO
            )
            .appendIsNotNullValue(RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE)
            .build()

        val relatedRelationshipItems = selectWhere(whereToClause)

        return relatedRelationshipItems.map { it.trackedEntityInstance()!!.trackedEntityInstance() }
    }

    override fun getByItem(item: RelationshipItem): List<RelationshipItem> {
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

    override fun getByEntityUid(entityUid: String): List<RelationshipItem> {
        val clauseBuilder = WhereClauseBuilder().apply {
            appendOrKeyStringValue(RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE, entityUid)
            appendOrKeyStringValue(RelationshipItemTableInfo.Columns.ENROLLMENT, entityUid)
            appendOrKeyStringValue(RelationshipItemTableInfo.Columns.EVENT, entityUid)
        }

        return selectWhere(clauseBuilder.build())
    }

    private fun getAllItemsOfSameType(from: RelationshipItem, to: RelationshipItem): Cursor {
        val query = "SELECT " + RelationshipItemTableInfo.Columns.RELATIONSHIP + ", " +
            "MAX(CASE WHEN " + RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE + " = 'FROM' " +
            "THEN " + from.elementType() + " END) AS fromElementUid, " +
            "MAX(CASE WHEN " + RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE + " = 'TO' " +
            "THEN " + to.elementType() + " END) AS toElementUid " +
            "FROM " + RelationshipItemTableInfo.TABLE_INFO.name() +
            " GROUP BY " + RelationshipItemTableInfo.Columns.RELATIONSHIP
        return databaseAdapter.rawQuery(query)
    }

    companion object {
        private val BINDER = StatementBinder { o: RelationshipItem, w: StatementWrapper ->
            val trackedEntityInstance = if (o.trackedEntityInstance() == null) null else o.trackedEntityInstance()!!
                .trackedEntityInstance()
            val enrollment = if (o.enrollment() == null) null else o.enrollment()!!.enrollment()
            val event = if (o.event() == null) null else o.event()!!.event()
            w.bind(1, getUidOrNull(o.relationship()))
            w.bind(2, o.relationshipItemType())
            w.bind(3, trackedEntityInstance)
            w.bind(4, enrollment)
            w.bind(5, event)
        }
        private val WHERE_UPDATE_BINDER = WhereStatementBinder { o: RelationshipItem, w: StatementWrapper ->
            w.bind(6, getUidOrNull(o.relationship()))
            w.bind(7, o.relationshipItemType())
        }
        private val WHERE_DELETE_BINDER = WhereStatementBinder { o: RelationshipItem, w: StatementWrapper ->
            w.bind(1, getUidOrNull(o.relationship()))
            w.bind(2, o.relationshipItemType())
        }

        @JvmStatic
        fun create(databaseAdapter: DatabaseAdapter): RelationshipItemStore {
            val statementBuilder = SQLStatementBuilderImpl(
                RelationshipItemTableInfo.TABLE_INFO.name(), RelationshipItemTableInfo.Columns()
            )
            return RelationshipItemStoreImpl(
                databaseAdapter,
                statementBuilder
            )
        }
    }
}
