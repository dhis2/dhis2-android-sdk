/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

package org.hisp.dhis.android.core.relationship;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.binders.StatementBinder;
import org.hisp.dhis.android.core.arch.db.binders.WhereStatementBinder;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStoreImpl;
import org.hisp.dhis.android.core.common.SQLStatementBuilder;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

final class RelationshipItemStoreImpl extends ObjectWithoutUidStoreImpl<RelationshipItem>
        implements RelationshipItemStore {

    private RelationshipItemStoreImpl(DatabaseAdapter databaseAdapter,
                                      SQLStatementBuilder builder) {
        super(databaseAdapter, databaseAdapter.compileStatement(builder.insert()),
                databaseAdapter.compileStatement(builder.updateWhere()), builder, BINDER, WHERE_UPDATE_BINDER, FACTORY);
    }

    @Override
    public List<String> getRelationshipUidsForItems(@NonNull RelationshipItem from, @NonNull RelationshipItem to) {
        Cursor cursor = this.getAllItemsOfSameType(from, to);
        List<String> relationships = new ArrayList<>();
        try {
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String relationshipInDb = cursor.getString(0);
                    String fromElementUidInDb = cursor.getString(1);
                    String toElementUidInDb = cursor.getString(2);

                    if (from.elementUid().equals(fromElementUidInDb) && to.elementUid().equals(toElementUidInDb)) {
                        relationships.add(relationshipInDb);
                    }
                }
                while(cursor.moveToNext());
            }
        } finally {
            cursor.close();
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

    private Cursor getAllItemsOfSameType(@NonNull RelationshipItem from, @NonNull RelationshipItem to) {
        String query = "SELECT " + RelationshipItemTableInfo.Columns.RELATIONSHIP + ", " +
                "MAX(CASE WHEN " + RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE + " = 'FROM' " +
                "THEN " + getItemElementColumn(from) + " END) AS fromElementUid, " +
                "MAX(CASE WHEN " + RelationshipItemTableInfo.Columns.RELATIONSHIP_ITEM_TYPE + " = 'TO' " +
                "THEN " + getItemElementColumn(to) + " END) AS toElementUid " +
                "FROM " + RelationshipItemTableInfo.TABLE_INFO.name() +
                " GROUP BY " + RelationshipItemTableInfo.Columns.RELATIONSHIP;

        return this.databaseAdapter.query(query);
    }

    private String getItemElementColumn(RelationshipItem item) {
        if (item.hasTrackedEntityInstance()) {
            return RelationshipItemFields.TRACKED_ENTITY_INSTANCE;
        } else if (item.hasEnrollment()) {
            return RelationshipItemFields.ENROLLMENT;
        } else {
            return RelationshipItemFields.EVENT;
        }
    }

    private static final StatementBinder<RelationshipItem> BINDER = new StatementBinder<RelationshipItem>() {
        @Override
        public void bindToStatement(@NonNull RelationshipItem o, @NonNull SQLiteStatement sqLiteStatement) {
            String trackedEntityInstance = o.trackedEntityInstance() == null ? null :
                    o.trackedEntityInstance().trackedEntityInstance();
            String enrollment = o.enrollment() == null ? null : o.enrollment().enrollment();
            String event = o.event() == null ? null : o.event().event();

            sqLiteBind(sqLiteStatement, 1, UidsHelper.getUidOrNull( o.relationship()));
            sqLiteBind(sqLiteStatement, 2, o.relationshipItemType());
            sqLiteBind(sqLiteStatement, 3, trackedEntityInstance);
            sqLiteBind(sqLiteStatement, 4, enrollment);
            sqLiteBind(sqLiteStatement, 5, event);
        }
    };


    private static final WhereStatementBinder<RelationshipItem> WHERE_UPDATE_BINDER
            = new WhereStatementBinder<RelationshipItem>() {
        @Override
        public void bindToUpdateWhereStatement(@NonNull RelationshipItem o,
                                               @NonNull SQLiteStatement sqLiteStatement) {
            sqLiteBind(sqLiteStatement, 6, UidsHelper.getUidOrNull(o.relationship()));
            sqLiteBind(sqLiteStatement, 7, o.relationshipItemType());
        }
    };

    private static final CursorModelFactory<RelationshipItem> FACTORY
            = new CursorModelFactory<RelationshipItem>() {
        @Override
        public RelationshipItem fromCursor(Cursor cursor) {
            return RelationshipItem.create(cursor);
        }
    };

    public static RelationshipItemStore create(DatabaseAdapter databaseAdapter) {
        SQLStatementBuilder statementBuilder = new SQLStatementBuilder(
                RelationshipItemTableInfo.TABLE_INFO.name(), new RelationshipItemTableInfo.Columns());

        return new RelationshipItemStoreImpl(
                databaseAdapter,
                statementBuilder
        );
    }
}