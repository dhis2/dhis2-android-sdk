/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.organisationunit;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.utils.Utils;

@AutoValue
public abstract class OrganisationUnitGroupModel extends BaseIdentifiableObjectModel {

    public static final String TABLE = "OrganisationUnitGroup";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String SHORT_NAME = "shortName";
        public static final String DISPLAY_SHORT_NAME = "displayShortName";


        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(), SHORT_NAME, DISPLAY_SHORT_NAME);
        }
    }

    public static OrganisationUnitGroupModel create(Cursor cursor) {
        return AutoValue_OrganisationUnitGroupModel.createFromCursor(cursor);
    }

    public static OrganisationUnitGroupModel.Builder builder() {
        return new $$AutoValue_OrganisationUnitGroupModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.SHORT_NAME)
    public abstract String shortName();

    @Nullable
    @ColumnName(Columns.DISPLAY_SHORT_NAME)
    public abstract String displayShortName();


    @AutoValue.Builder
    public static abstract class Builder
            extends BaseIdentifiableObjectModel.Builder<OrganisationUnitGroupModel.Builder> {

        public abstract Builder shortName(@Nullable String shortName);

        public abstract Builder displayShortName(@Nullable String displayShortName);

        public abstract OrganisationUnitGroupModel build();
    }
}
