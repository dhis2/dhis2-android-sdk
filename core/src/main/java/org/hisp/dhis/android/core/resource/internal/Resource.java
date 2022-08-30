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

package org.hisp.dhis.android.core.resource.internal;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.ResourceTypeColumnAdapter;
import org.hisp.dhis.android.core.common.BaseObject;
import org.hisp.dhis.android.core.common.CoreObject;

import java.util.Date;

@AutoValue
public abstract class Resource implements CoreObject {

    public enum Type {
        EVENT,
        SYSTEM_INFO,
        ORGANISATION_UNIT,
        PROGRAM,
        OPTION_SET,
        TRACKED_ENTITY_TYPE,
        DATA_SET,
        DATA_ELEMENT,
        CATEGORY_COMBO,
        INDICATOR_TYPE,
        INDICATOR,
        PROGRAM_STAGE,
        RELATIONSHIP_TYPE,
        TRACKED_ENTITY_ATTRIBUTE_RESERVED_VALUE
    }

    @Nullable
    @ColumnAdapter(ResourceTypeColumnAdapter.class)
    public abstract Resource.Type resourceType();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastSynced();

    public static Resource create(Cursor cursor) {
        return $AutoValue_Resource.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $$AutoValue_Resource.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder extends BaseObject.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder resourceType(Resource.Type resourceType);

        public abstract Builder lastSynced(Date lastSynced);

        public abstract Resource build();
    }
}