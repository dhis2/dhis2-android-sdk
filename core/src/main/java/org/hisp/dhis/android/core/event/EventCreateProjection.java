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

package org.hisp.dhis.android.core.event;

import com.google.auto.value.AutoValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@AutoValue
public abstract class EventCreateProjection {

    @Nullable
    public abstract String enrollment();

    @NonNull
    public abstract String program();

    @NonNull
    public abstract String programStage();

    @NonNull
    public abstract String organisationUnit();

    @Nullable
    public abstract String attributeOptionCombo();

    public static EventCreateProjection create(
            String enrollment,
            String program,
            String programStage,
            String organisationUnit,
            String attributeOptionCombo) {
        return builder()
                .enrollment(enrollment)
                .program(program)
                .programStage(programStage)
                .organisationUnit(organisationUnit)
                .attributeOptionCombo(attributeOptionCombo)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_EventCreateProjection.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder enrollment(String enrollment);

        public abstract Builder program(String program);

        public abstract Builder programStage(String programStage);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder attributeOptionCombo(String attributeOptionCombo);

        public abstract EventCreateProjection build();
    }
}