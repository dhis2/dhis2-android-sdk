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

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;
import static org.hisp.dhis.android.core.utils.Utils.isNull;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.Store;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class RelationshipStoreImpl extends Store implements RelationshipStore {

    private static final String FIELDS =
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A + ", " +
                    RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B + ", " +
                    RelationshipModel.Columns.RELATIONSHIP_TYPE;

    private static final String INSERT_STATEMENT = "INSERT INTO " +
            RelationshipModel.TABLE + " (" + FIELDS +") " +
            "VALUES(?, ?, ?);";

    private static final String DELETE_STATEMENT = "DELETE FROM " +
            RelationshipModel.TABLE + " WHERE " +
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A + "=?;";

    private static final String QUERY_BY_UID = "SELECT " + FIELDS + " FROM " +
            RelationshipModel.TABLE + " WHERE " +
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A + "=?"
            +" OR "+
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B + "=?"
            + " GROUP BY "+ RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_A
            +"," +
            RelationshipModel.Columns.TRACKED_ENTITY_INSTANCE_B +";";

    private final SQLiteStatement insertStatement;
    private final SQLiteStatement deleteStatement;
    private final DatabaseAdapter databaseAdapter;

    public RelationshipStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
        this.deleteStatement = databaseAdapter.compileStatement(DELETE_STATEMENT);
    }

    @Override
    public long insert(@Nullable String trackedEntityInstanceA,
                       @Nullable String trackedEntityInstanceB,
                       @NonNull String uid) {

        isNull(uid);
        sqLiteBind(insertStatement, 1, trackedEntityInstanceA);
        sqLiteBind(insertStatement, 2, trackedEntityInstanceB);
        sqLiteBind(insertStatement, 3, uid);

        long ret = databaseAdapter.executeInsert(RelationshipModel.TABLE, insertStatement);
        insertStatement.clearBindings();
        return ret;
    }

    @Override
    public int removeOldRelations(String uid) {
        isNull(uid);
        // bind the where argument
        sqLiteBind(deleteStatement, 1, uid);

        // execute and clear bindings
        int delete = databaseAdapter.executeUpdateDelete(RelationshipModel.TABLE, deleteStatement);
        deleteStatement.clearBindings();
        return delete;
    }

    @Override
    public List<Relationship> queryByTrackedEntityInstanceUid(String uid) {
        Cursor cursor = databaseAdapter.query(QUERY_BY_UID, uid, uid);

        List<Relationship> relationships = mapFromCursor(cursor);

        return relationships;
    }

    private List<Relationship> mapFromCursor(Cursor cursor) {

        List<Relationship> relationships = new ArrayList<>();
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    String trackedEntityInstanceA = getStringFromCursor(cursor, 0);
                    String trackedEntityInstanceB = getStringFromCursor(cursor, 1);
                    String uid = getStringFromCursor(cursor, 2);

                    relationships.add(Relationship.builder()
                            .trackedEntityInstanceA(trackedEntityInstanceA)
                            .trackedEntityInstanceB(trackedEntityInstanceB)
                            .relationshipType(uid)
                            .build());

                } while (cursor.moveToNext());
            }

        } finally {
            cursor.close();
        }
        return relationships;
    }
    @Override
    public int delete() {
        return databaseAdapter.delete(RelationshipModel.TABLE);
    }
}
