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

package org.hisp.dhis.android.core.maintenance;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.D2ErrorCodeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.D2ErrorComponentColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreExceptionAdapter;
import org.hisp.dhis.android.core.common.BaseObject;
import org.hisp.dhis.android.core.common.CoreObject;

import java.util.Date;

@AutoValue
public abstract class D2Error extends Exception implements CoreObject {

    @Nullable
    public abstract String url();

    @Nullable
    @ColumnAdapter(D2ErrorComponentColumnAdapter.class)
    public abstract D2ErrorComponent errorComponent();

    @NonNull
    @ColumnAdapter(D2ErrorCodeColumnAdapter.class)
    public abstract D2ErrorCode errorCode();

    @NonNull
    public abstract String errorDescription();

    @Nullable
    public abstract Integer httpErrorCode();

    @Nullable
    @ColumnAdapter(IgnoreExceptionAdapter.class)
    public abstract Exception originalException();

    @Nullable
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @NonNull
    public static D2Error create(Cursor cursor) {
        return AutoValue_D2Error.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new AutoValue_D2Error.Builder();
    }

    public abstract Builder toBuilder();

    public boolean isOffline() {
        return errorCode() == D2ErrorCode.SOCKET_TIMEOUT || errorCode() == D2ErrorCode.UNKNOWN_HOST;
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseObject.Builder<Builder> {

        public abstract Builder url(String url);

        public abstract Builder errorComponent(D2ErrorComponent errorComponent);

        public abstract Builder errorCode(D2ErrorCode errorCode);

        public abstract Builder errorDescription(String description);

        public abstract Builder httpErrorCode(Integer httpErrorCode);

        public abstract Builder originalException(Exception originalException);

        public abstract Builder created(Date created);

        abstract D2Error autoBuild();

        public D2Error build() {
            this.created(new Date());
            return autoBuild();
        }
    }
}