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
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.data.database.DbRelationshipConstraintTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbRelationshipEntityTypeColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
public abstract class RelationshipConstraintModel extends BaseModel {

    public static final String TABLE = "RelationshipConstraint";

    public static class Columns extends BaseModel.Columns {
        public static final String RELATIONSHIP_TYPE = "relationshipType";
        public static final String CONSTRAINT_TYPE = "constraintType";
        public static final String RELATIONSHIP_ENTITY = "relationshipEntity";
        public static final String TRACKED_ENTITY_TYPE = "trackedEntityType";
        public static final String PROGRAM = "program";
        public static final String PROGRAM_STAGE = "programStage";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(), RELATIONSHIP_TYPE, CONSTRAINT_TYPE, RELATIONSHIP_ENTITY,
                    TRACKED_ENTITY_TYPE, PROGRAM, PROGRAM_STAGE);
        }

        @Override
        public String[] whereUpdate() {
            return new String[]{RELATIONSHIP_TYPE, CONSTRAINT_TYPE};
        }
    }

    static RelationshipConstraintModel create(Cursor cursor) {
        return AutoValue_RelationshipConstraintModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_RelationshipConstraintModel.Builder();
    }

    public static final CursorModelFactory<RelationshipConstraintModel> factory
            = new CursorModelFactory<RelationshipConstraintModel>() {
        @Override
        public RelationshipConstraintModel fromCursor(Cursor cursor) {
            return create(cursor);
        }
    };

    @NonNull
    @ColumnName(Columns.RELATIONSHIP_TYPE)
    public abstract String relationshipType();

    @NonNull
    @ColumnName(Columns.CONSTRAINT_TYPE)
    @ColumnAdapter(DbRelationshipConstraintTypeColumnAdapter.class)
    public abstract RelationshipConstraintType constraintType();

    @Nullable
    @ColumnName(Columns.RELATIONSHIP_ENTITY)
    @ColumnAdapter(DbRelationshipEntityTypeColumnAdapter.class)
    public abstract RelationshipEntityType relationshipEntity();


    @Nullable
    @ColumnName(Columns.TRACKED_ENTITY_TYPE)
    public abstract String trackedEntityType();

    @Nullable
    @ColumnName(Columns.PROGRAM)
    public abstract String program();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE)
    public abstract String programStage();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 1, relationshipType());
        sqLiteBind(sqLiteStatement, 2, constraintType());
        sqLiteBind(sqLiteStatement, 3, relationshipEntity());
        sqLiteBind(sqLiteStatement, 4, trackedEntityType());
        sqLiteBind(sqLiteStatement, 5, program());
        sqLiteBind(sqLiteStatement, 6, programStage());
    }

    @Override
    public void bindToUpdateWhereStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 7, relationshipType());
        sqLiteBind(sqLiteStatement, 8, constraintType());
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder relationshipType(String relationshipType);

        public abstract Builder constraintType(RelationshipConstraintType constraintType);

        public abstract Builder relationshipEntity(RelationshipEntityType relationshipEntity);

        public abstract Builder trackedEntityType(String trackedEntityType);

        public abstract Builder program(String program);

        public abstract Builder programStage(String programStage);

        public abstract RelationshipConstraintModel build();
    }
}