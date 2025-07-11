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

package org.hisp.dhis.android.core.indicator;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreObjectWithUidListColumnAdapter;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;

import java.util.List;

@AutoValue
public abstract class Indicator extends BaseNameableObject
        implements CoreObject, ObjectWithStyle<Indicator, Indicator.Builder> {

    @Nullable
    public abstract Boolean annualized();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid indicatorType();

    @Nullable
    public abstract String numerator();

    @Nullable
    public abstract String numeratorDescription();

    @Nullable
    public abstract String denominator();

    @Nullable
    public abstract String denominatorDescription();

    @Nullable
    public abstract String url();

    @Nullable
    public abstract Integer decimals();

    @Nullable
    @ColumnAdapter(IgnoreObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> legendSets();

    public static Builder builder() {
        return new $$AutoValue_Indicator.Builder();
    }

    public static Indicator create(Cursor cursor) {
        return $AutoValue_Indicator.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseNameableObject.Builder<Builder>
            implements ObjectWithStyle.Builder<Indicator, Indicator.Builder> {
        public abstract Builder id(Long id);

        public abstract Builder annualized(Boolean annualized);

        public abstract Builder indicatorType(ObjectWithUid indicatorType);

        public abstract Builder numerator(String numerator);

        public abstract Builder numeratorDescription(String numeratorDescription);

        public abstract Builder denominator(String denominator);

        public abstract Builder denominatorDescription(String denominatorDescription);

        public abstract Builder legendSets(List<ObjectWithUid> legendSets);

        public abstract Builder url(String url);

        public abstract Builder decimals(Integer decimals);

        abstract Indicator autoBuild();

        // Auxiliary fields
        abstract ObjectStyle style();

        public Indicator build() {
            try {
                style();
            } catch (IllegalStateException e) {
                style(ObjectStyle.builder().build());
            }

            return autoBuild();
        }

    }
}