/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.legendset;

import android.database.Cursor;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.category.CategoryCategoryOptionLink;
import org.hisp.dhis.android.core.common.BaseObject;
import org.hisp.dhis.android.core.common.CoreObject;

import androidx.annotation.Nullable;

@AutoValue
public abstract class DataElementLegendSetLink implements CoreObject {

    @Nullable
    public abstract String dataElement();

    @Nullable
    public abstract String legendSet();

    @Nullable
    public abstract Integer sortOrder();

    public static DataElementLegendSetLink create(Cursor cursor) {
        return AutoValue_DataElementLegendSetLink.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_DataElementLegendSetLink.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder extends BaseObject.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder dataElement(String dataElement);

        public abstract Builder legendSet(String legendSet);

        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        public abstract DataElementLegendSetLink build();
    }
}