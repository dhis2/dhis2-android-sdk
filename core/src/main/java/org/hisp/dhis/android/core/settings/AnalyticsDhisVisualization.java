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

package org.hisp.dhis.android.core.settings;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.AnalyticsDhisVisualizationScopeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.AnalyticsDhisVisualizationTypeColumnAdapter;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

@AutoValue
public abstract class AnalyticsDhisVisualization implements CoreObject, ObjectWithUidInterface {

    @Nullable
    public abstract String scopeUid();

    @Nullable
    public abstract String groupUid();

    @Nullable
    public abstract String groupName();

    @Nullable
    @ColumnAdapter(AnalyticsDhisVisualizationScopeColumnAdapter.class)
    public abstract AnalyticsDhisVisualizationScope scope();

    @NonNull
    public abstract String uid();

    @Nullable
    public abstract String name();

    @Nullable
    public abstract String timestamp();

    @NonNull
    @ColumnAdapter(AnalyticsDhisVisualizationTypeColumnAdapter.class)
    public abstract AnalyticsDhisVisualizationType type();

    public static AnalyticsDhisVisualization create(Cursor cursor) {
        return AutoValue_AnalyticsDhisVisualization.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_AnalyticsDhisVisualization.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(Long id);

        public abstract Builder scopeUid(String scopeUid);

        public abstract Builder groupUid(String groupUid);

        public abstract Builder groupName(String groupName);

        public abstract Builder scope(AnalyticsDhisVisualizationScope scope);

        public abstract Builder uid(String uid);

        public abstract Builder name(String name);

        public abstract Builder timestamp(String timestamp);

        public abstract Builder type(AnalyticsDhisVisualizationType type);

        abstract public AnalyticsDhisVisualization build();
    }
}
