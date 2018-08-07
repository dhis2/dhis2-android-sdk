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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.CursorModelFactory;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.DbRelationshipConstraintTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbRelationshipEntityTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.ObjectWithUidColumnAdapter;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
@JsonDeserialize(builder = AutoValue_RelationshipConstraint.Builder.class)
public abstract class RelationshipConstraint extends BaseModel {

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid relationshipType();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(DbRelationshipConstraintTypeColumnAdapter.class)
    public abstract RelationshipConstraintType constraintType();

    @Nullable
    @ColumnAdapter(DbRelationshipEntityTypeColumnAdapter.class)
    public abstract RelationshipEntityType relationshipEntity();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid trackedEntityType();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid program();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid programStage();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 1, UidsHelper.getUidOrNull(relationshipType()));
        sqLiteBind(sqLiteStatement, 2, constraintType());
        sqLiteBind(sqLiteStatement, 3, relationshipEntity());
        sqLiteBind(sqLiteStatement, 4, UidsHelper.getUidOrNull(trackedEntityType()));
        sqLiteBind(sqLiteStatement, 5, UidsHelper.getUidOrNull(program()));
        sqLiteBind(sqLiteStatement, 6, UidsHelper.getUidOrNull(programStage()));
    }

    @Override
    public void bindToUpdateWhereStatement(@NonNull SQLiteStatement sqLiteStatement) {
        sqLiteBind(sqLiteStatement, 7, UidsHelper.getUidOrNull(relationshipType()));
        sqLiteBind(sqLiteStatement, 8, constraintType());
    }

    static RelationshipConstraint create(Cursor cursor) {
        return AutoValue_RelationshipConstraint.createFromCursor(cursor);
    }

    public static final CursorModelFactory<RelationshipConstraint> factory
            = new CursorModelFactory<RelationshipConstraint>() {
        @Override
        public RelationshipConstraint fromCursor(Cursor cursor) {
            return create(cursor);
        }
    };


    public static Builder builder() {
        return new AutoValue_RelationshipConstraint.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseModel.Builder<Builder> {

        public abstract Builder relationshipType(ObjectWithUid relationshipType);

        public abstract Builder constraintType(RelationshipConstraintType constraintType);

        public abstract Builder relationshipEntity(RelationshipEntityType relationshipEntity);

        public abstract Builder trackedEntityType(ObjectWithUid trackedEntityType);

        public abstract Builder program(ObjectWithUid program);

        public abstract Builder programStage(ObjectWithUid programStage);

        public abstract RelationshipConstraint build();
    }
}