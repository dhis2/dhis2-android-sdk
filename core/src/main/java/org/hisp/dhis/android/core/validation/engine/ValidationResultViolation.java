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

package org.hisp.dhis.android.core.validation.engine;

import com.google.auto.value.AutoValue;
import com.google.common.collect.Sets;

import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.validation.ValidationRule;

import java.util.Set;

@AutoValue
public abstract class ValidationResultViolation {

    public abstract ValidationRule validationRule();

    public abstract String period();

    public abstract String organisationUnitUid();

    public abstract String attributeOptionComboUid();

    public abstract ValidationResultSideEvaluation leftSideEvaluation();

    public abstract ValidationResultSideEvaluation rightSideEvaluation();

    public Set<DataElementOperand> dataElementUids() {
        return Sets.union(
                leftSideEvaluation().dataElementUids(),
                rightSideEvaluation().dataElementUids()
        );
    }

    public static Builder builder() {
        return new AutoValue_ValidationResultViolation.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder validationRule(ValidationRule validationRule);

        public abstract Builder period(String period);

        public abstract Builder organisationUnitUid(String organisationUnitUid);

        public abstract Builder attributeOptionComboUid(String attributeOptionComboUid);

        public abstract Builder leftSideEvaluation(ValidationResultSideEvaluation leftSideEvaluation);

        public abstract Builder rightSideEvaluation(ValidationResultSideEvaluation rightSideEvaluation);

        public abstract ValidationResultViolation build();
    }
}