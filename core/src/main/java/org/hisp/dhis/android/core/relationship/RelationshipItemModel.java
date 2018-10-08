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
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.data.database.DbRelationshipConstraintTypeColumnAdapter;
import org.hisp.dhis.android.core.utils.Utils;

@AutoValue
public abstract class RelationshipItemModel extends BaseModel {
    public static final String TABLE = "RelationshipItem";

    public static class Columns extends BaseModel.Columns {
        public static final String RELATIONSHIP = "relationship";
        public static final String RELATIONSHIP_ITEM_TYPE = "relationshipItemType";
        public static final String TRACKED_ENTITY_INSTANCE = "trackedEntityInstance";
        public static final String ENROLLMENT = "enrollment";
        public static final String EVENT = "event";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    RELATIONSHIP, RELATIONSHIP_ITEM_TYPE, TRACKED_ENTITY_INSTANCE, ENROLLMENT, EVENT);
        }

        @Override
        public String[] whereUpdate() {
            return new String[]{RELATIONSHIP, RELATIONSHIP_ITEM_TYPE};
        }
    }

    public static RelationshipItemModel create(Cursor cursor) {
        return AutoValue_RelationshipItemModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_RelationshipItemModel.Builder();
    }

    @ColumnName(Columns.RELATIONSHIP)
    public abstract String relationship();

    @ColumnName(Columns.RELATIONSHIP_ITEM_TYPE)
    @ColumnAdapter(DbRelationshipConstraintTypeColumnAdapter.class)
    public abstract RelationshipConstraintType relationshipItemType();

    @Nullable
    @ColumnName(Columns.TRACKED_ENTITY_INSTANCE)
    public abstract String trackedEntityInstance();

    @Nullable
    @ColumnName(Columns.ENROLLMENT)
    public abstract String enrollment();

    @Nullable
    @ColumnName(Columns.EVENT)
    public abstract String event();

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder relationship(String relationship);

        public abstract Builder relationshipItemType(RelationshipConstraintType relationshipItemType);

        public abstract Builder trackedEntityInstance(@Nullable String trackedEntityInstance);

        public abstract Builder enrollment(@Nullable String enrollment);

        public abstract Builder event(@Nullable String event);

        public abstract RelationshipItemModel build();
    }
}
