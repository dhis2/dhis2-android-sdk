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

package org.hisp.dhis.android.core.map.layer;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.MapLayerPositionColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreMapLayerImageryProviderColumnAdapter;
import org.hisp.dhis.android.core.common.BaseObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

import java.util.List;

@AutoValue
public abstract class MapLayer extends BaseObject implements ObjectWithUidInterface {

    @Override
    @NonNull
    public abstract String uid();

    @NonNull
    public abstract String name();

    @NonNull
    public abstract String displayName();

    @NonNull
    public abstract Boolean external();

    @NonNull
    @ColumnAdapter(MapLayerPositionColumnAdapter.class)
    public abstract MapLayerPosition mapLayerPosition();

    @Nullable
    public abstract String style();

    @NonNull
    public abstract String imageUrl();

    @Nullable
    @ColumnAdapter(StringListColumnAdapter.class)
    public abstract List<String> subdomains();

    @Nullable
    public abstract String subdomainPlaceholder();

    @Nullable
    @ColumnAdapter(IgnoreMapLayerImageryProviderColumnAdapter.class)
    public abstract List<MapLayerImageryProvider> imageryProviders();

    public static MapLayer create(Cursor cursor) {
        return AutoValue_MapLayer.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $$AutoValue_MapLayer.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseObject.Builder<Builder> {

        public abstract Builder uid(String uid);

        public abstract Builder name(String name);

        public abstract Builder displayName(String displayName);

        public abstract Builder external(Boolean external);

        public abstract Builder mapLayerPosition(MapLayerPosition mapLayerPosition);

        public abstract Builder style(String style);

        public abstract Builder imageUrl(String imageUrl);

        public abstract Builder subdomains(List<String> subdomains);

        public abstract Builder subdomainPlaceholder(String subdomainPlaceholder);

        public abstract Builder imageryProviders(List<MapLayerImageryProvider> imageryProviders);

        public abstract MapLayer build();
    }
}
