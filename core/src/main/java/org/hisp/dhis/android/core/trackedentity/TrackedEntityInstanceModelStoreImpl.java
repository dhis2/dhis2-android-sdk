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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class TrackedEntityInstanceModelStoreImpl implements TrackedEntityInstanceModelStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " +
            TrackedEntityInstanceModel.TRACKED_ENTITY_INSTANCE + " (" +
            TrackedEntityInstanceModel.Columns.UID + ", " +
            TrackedEntityInstanceModel.Columns.CREATED + ", " +
            TrackedEntityInstanceModel.Columns.LAST_UPDATED + ", " +
            TrackedEntityInstanceModel.Columns.ORGANISATION_UNIT + ", " +
            TrackedEntityInstanceModel.Columns.STATE +
            ") " + "VALUES (?, ?, ?, ?, ?)";

    private final SQLiteDatabase sqLiteDatabase;
    private final SQLiteStatement insertRowStatement;

    public TrackedEntityInstanceModelStoreImpl(SQLiteDatabase database) {
        this.sqLiteDatabase = database;
        this.insertRowStatement = database.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
                       @NonNull String organisationUnit, @Nullable State state) {
        insertRowStatement.clearBindings();

        sqLiteBind(insertRowStatement, 1, uid);
        sqLiteBind(insertRowStatement, 2, created);
        sqLiteBind(insertRowStatement, 3, lastUpdated);
        sqLiteBind(insertRowStatement, 4, organisationUnit);
        sqLiteBind(insertRowStatement, 5, state);

        return insertRowStatement.executeInsert();
    }

    @Override
    public int delete() {
        return sqLiteDatabase.delete(TrackedEntityInstanceModel.TRACKED_ENTITY_INSTANCE, null, null);
    }

    @Override
    public void close() {
        insertRowStatement.close();
    }
}
