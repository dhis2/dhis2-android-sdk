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

package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

public class TrackedEntityAttributeValueStoreImpl implements TrackedEntityAttributeValueStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " +
            TrackedEntityAttributeValueModel.TABLE + " (" +
            TrackedEntityAttributeValueModel.Columns.STATE + ", " +
            TrackedEntityAttributeValueModel.Columns.VALUE + ", " +
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_ATTRIBUTE + ", " +
            TrackedEntityAttributeValueModel.Columns.TRACKED_ENTITY_INSTANCE + ") " +
            "VALUES (?, ?, ?, ?)";
    
    private static final String QUERY_STATEMENT = "SELECT " +
            "  TrackedEntityAttributeValue.trackedEntityAttribute, " +
            "  TrackedEntityAttributeValue.value " +
            "FROM (TrackedEntityAttributeValue " +
            "  INNER JOIN TrackedEntityInstance " +
            "    ON TrackedEntityAttributeValue.trackedEntityInstance = TrackedEntityInstance.uid) " +
            "WHERE TrackedEntityInstance.state = 'TO_POST' OR TrackedEntityInstance.state = 'TO_UPDATE';";

    private final SQLiteStatement insertRowStatement;
    private final DatabaseAdapter databaseAdapter;

    public TrackedEntityAttributeValueStoreImpl(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        this.insertRowStatement = databaseAdapter.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull State state,
                       @Nullable String value,
                       @NonNull String trackedEntityAttribute,
                       @NonNull String trackedEntityInstance) {
        sqLiteBind(insertRowStatement, 1, state);
        sqLiteBind(insertRowStatement, 2, value);
        sqLiteBind(insertRowStatement, 3, trackedEntityAttribute);
        sqLiteBind(insertRowStatement, 4, trackedEntityInstance);

        long insert = databaseAdapter.executeInsert(TrackedEntityAttributeValueModel.TABLE, insertRowStatement);
        insertRowStatement.clearBindings();
        
        return insert;
    }

    @Override
    public Map<String, List<TrackedEntityAttributeValue>> query() {
        return null;
    }

}
