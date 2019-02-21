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
package org.hisp.dhis.android.core.program;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.utils.Utils;

@AutoValue
public abstract class ProgramSectionAttributeLinkModel extends BaseModel {
    public static final String TABLE = "ProgramSectionAttributeLink";

    public static class Columns extends BaseModel.Columns {
        public static final String PROGRAM_SECTION = "programSection";
        public static final String ATTRIBUTE = "attribute";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(), PROGRAM_SECTION, ATTRIBUTE);
        }
    }

    public static ProgramSectionAttributeLinkModel create(Cursor cursor) {
        return AutoValue_ProgramSectionAttributeLinkModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramSectionAttributeLinkModel.Builder();
    }

    @Nullable
    public abstract String programSection();

    @Nullable
    public abstract String attribute();

    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder programSection(String programSection);

        public abstract Builder attribute(String attribute);

        public abstract ProgramSectionAttributeLinkModel build();
    }
}
