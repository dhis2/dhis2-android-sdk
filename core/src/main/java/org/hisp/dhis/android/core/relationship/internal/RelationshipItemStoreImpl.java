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

package org.hisp.dhis.android.core.relationship.internal;

import android.database.Cursor;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.SQLStatementBuilderImpl;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.StatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.binders.internal.WhereStatementBinder;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo;
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo.Columns;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public final class RelationshipItemStoreImpl extends ObjectWithoutUidStoreImpl<RelationshipItem>
        implements RelationshipItemStore {

    private static final StatementBinder<RelationshipItem> BINDER = (o, w) -> {
        String trackedEntityInstance = o.trackedEntityInstance() == null ? null :
                o.trackedEntityInstance().trackedEntityInstance();
        String enrollment = o.enrollment() == null ? null : o.enrollment().enrollment();
        String event = o.event() == null ? null : o.event().event();

        w.bind(1, UidsHelper.getUidOrNull(o.relationship()));
        w.bind(2, o.relationshipItemType());
        w.bind(3, trackedEntityInstance);
        w.bind(4, enrollment);
        w.bind(5, event);
    };

    private static final WhereStatementBinder<RelationshipItem> WHERE_UPDATE_BINDER = (o, w) -> {
        w.bind(6, UidsHelper.getUidOrNull(o.relationship()));
        w.bind(7, o.relationshipItemType());
    };

    private static final WhereStatementBinder<RelationshipItem> WHERE_DELETE_BINDER = (o, w) -> {
        w.bind(1, UidsHelper.getUidOrNull(o.relationship()));
        w.bind(2, o.relationshipItemType());
    };

    private RelationshipItemStoreImpl(DatabaseAdapter databaseAdapter,
                                      SQLStatementBuilderImpl builder) {
        super(databaseAdapter, builder, BINDER, WHERE_UPDATE_BINDER, WHERE_DELETE_BINDER, RelationshipItem::create);
    }

    @Override
    public List<String> getRelationshipUidsForItems(@NonNull RelationshipItem from, @NonNull RelationshipItem to) {
        List<String> relationships = new ArrayList<>();
        try (Cursor cursor = this.getAllItemsOfSameType(from, to)) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String relationshipInDb = cursor.getString(0);
                    String fromElementUidInDb = cursor.getString(1);
                    String toElementUidInDb = cursor.getString(2);

                    if (from.elementUid().equals(fromElementUidInDb) && to.elementUid().equals(toElementUidInDb)) {
                        relationships.add(relationshipInDb);
                    }
                }
                while (cursor.moveToNext());
            }
        }

        return relationships;
    }

    @Override
    public RelationshipItem getForRelationshipUidAndConstraintType(
            @NonNull String uid,
            @NonNull RelationshipConstraintType constraintType) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP, uid)
                .appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE, constraintType)
                .build();
        return selectOneWhere(whereClause);
    }

    @Override
    public List<String> getRelatedTeiUids(List<String> trackedEntityInstanceUids) {
        String whereFromClause = new WhereClauseBuilder()
                .appendInKeyStringValues(Columns.TRACKED_ENTITY_INSTANCE, trackedEntityInstanceUids)
                .appendKeyStringValue(Columns.RELATIONSHIP_ITEM_TYPE,
                        RelationshipConstraintType.FROM)
                .build();
        List<RelationshipItem> relationshipItems = selectWhere(whereFromClause);
        List<String> relationshipUids = new ArrayList<>();
        for (RelationshipItem relationshipItem : relationshipItems) {
            relationshipUids.add(relationshipItem.relationship().uid());
        }

        String whereToClause = new WhereClauseBuilder()
                .appendInKeyStringValues(RelationshipItemTableInfo.Columns.RELATIONSHIP, relationshipUids)
                .appendKeyStringValue(RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE,
                        RelationshipConstraintType.TO)
                .build();
        List<RelationshipItem> relatedRelationshipItems = selectWhere(whereToClause);
        List<String> relatedTEiUids = new ArrayList<>();
        for (RelationshipItem relationshipItem : relatedRelationshipItems) {
            relatedTEiUids.add(relationshipItem.trackedEntityInstance().trackedEntityInstance());
        }
        return relatedTEiUids;
    }

    private Cursor getAllItemsOfSameType(@NonNull RelationshipItem from, @NonNull RelationshipItem to) {
        String query = "SELECT " + RelationshipItemTableInfo.Columns.RELATIONSHIP + ", " +
                "MAX(CASE WHEN " + RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE + " = 'FROM' " +
                "THEN " + getItemElementColumn(from) + " END) AS fromElementUid, " +
                "MAX(CASE WHEN " + RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE + " = 'TO' " +
                "THEN " + getItemElementColumn(to) + " END) AS toElementUid " +
                "FROM " + RelationshipItemTableInfo.TABLE_INFO.name() +
                " GROUP BY " + RelationshipItemTableInfo.Columns.RELATIONSHIP;

        return this.databaseAdapter.rawQuery(query);
    }

    private String getItemElementColumn(RelationshipItem item) {
        if (item.hasTrackedEntityInstance()) {
            return Columns.TRACKED_ENTITY_INSTANCE;
        } else if (item.hasEnrollment()) {
            return Columns.ENROLLMENT;
        } else {
            return Columns.EVENT;
        }
    }

    public static RelationshipItemStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilderImpl statementBuilder = new SQLStatementBuilderImpl(
                RelationshipItemTableInfo.TABLE_INFO.name(), new RelationshipItemTableInfo.Columns());

        return new RelationshipItemStoreImpl(
                databaseAdapter,
                statementBuilder
        );
    }
}