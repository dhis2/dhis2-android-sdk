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

import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.ProgramItemHeaderColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.QuickActionListColumnAdapter;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

import java.util.List;

@AutoValue
public abstract class ProgramConfigurationSetting implements CoreObject, ObjectWithUidInterface {

    @Nullable
    public abstract String uid();

    @Nullable
    public abstract Boolean completionSpinner();

    @Nullable
    public abstract Boolean optionalSearch();

    @Nullable
    public abstract Boolean disableReferrals();

    @Nullable
    public abstract Boolean disableCollapsibleSections();

    @Nullable
    @ColumnAdapter(ProgramItemHeaderColumnAdapter.class)
    public abstract ProgramItemHeader itemHeader();

    @Nullable
    public abstract Integer minimumLocationAccuracy();

    @Nullable
    public abstract Boolean disableManualLocation();

    @Nullable
    @ColumnAdapter(QuickActionListColumnAdapter.class)
    public abstract List<QuickAction> quickActions();

    public static ProgramConfigurationSetting create(Cursor cursor) {
        return AutoValue_ProgramConfigurationSetting.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_ProgramConfigurationSetting.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder id(Long id);

        public abstract Builder uid(String uid);

        public abstract Builder completionSpinner(Boolean completionSpinner);

        public abstract Builder optionalSearch(Boolean optionalSearch);

        public abstract Builder disableReferrals(Boolean disableReferrals);

        public abstract Builder disableCollapsibleSections(Boolean disableCollapsibleSections);

        public abstract Builder itemHeader(ProgramItemHeader itemHeader);

        public abstract Builder minimumLocationAccuracy(Integer minimumLocationAccuracy);

        public abstract Builder disableManualLocation(Boolean disableManualLocation);

        public abstract Builder quickActions(List<QuickAction> quickActions);

        public abstract ProgramConfigurationSetting build();
    }
}
