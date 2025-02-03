/*
 *  Copyright (c) 2004-2023, University of Oslo
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

package org.hisp.dhis.android.core.relationship;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.AccessColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreRelationshipConstraintAdapter;
import org.hisp.dhis.android.core.arch.helpers.AccessHelper;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;

@AutoValue
public abstract class RelationshipType extends BaseIdentifiableObject implements CoreObject {

    @Nullable
    public abstract String fromToName();

    @Nullable
    public abstract String toFromName();

    @Nullable
    @ColumnAdapter(IgnoreRelationshipConstraintAdapter.class)
    public abstract RelationshipConstraint fromConstraint();

    @Nullable
    @ColumnAdapter(IgnoreRelationshipConstraintAdapter.class)
    public abstract RelationshipConstraint toConstraint();

    @Nullable
    public abstract Boolean bidirectional();

    @ColumnAdapter(AccessColumnAdapter.class)
    public abstract Access access();

    public static Builder builder() {
        return new $$AutoValue_RelationshipType.Builder();
    }

    public static RelationshipType create(Cursor cursor) {
        return AutoValue_RelationshipType.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseIdentifiableObject.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder fromToName(String fromToName);

        public abstract Builder toFromName(String toFromName);

        public abstract Builder fromConstraint(RelationshipConstraint fromConstraint);

        public abstract Builder toConstraint(RelationshipConstraint toConstraint);

        public abstract Builder bidirectional(Boolean bidirectional);

        public abstract Builder access(Access access);

        abstract RelationshipType autoBuild();

        // Auxiliary fields to access values
        abstract Boolean bidirectional();
        abstract Access access();

        public RelationshipType build() {
            if (bidirectional() == null) {
                bidirectional(false);                                   // Since 2.32
            }

            try {
                access();
            } catch (IllegalStateException e) {
                access(AccessHelper.createForDataWrite(true));                // Since 2.30
            }
            return autoBuild();
        }
    }
}
