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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.ModelBuilder;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.util.Date;

public class TrackedEntityAttributeReservedValueModelBuilder
        implements ModelBuilder<TrackedEntityAttributeReservedValue, TrackedEntityAttributeReservedValue> {

    private final TrackedEntityAttributeReservedValue.Builder builder;

    TrackedEntityAttributeReservedValueModelBuilder(OrganisationUnit organisationUnit, String pattern) {
        this.builder = TrackedEntityAttributeReservedValue.builder()
                .organisationUnit(organisationUnit == null ? null : organisationUnit.uid());
        fillTemporalValidityDate(pattern);
    }

    @Override
    public TrackedEntityAttributeReservedValue buildModel(TrackedEntityAttributeReservedValue reservedValue) {
        return builder
                .ownerObject(reservedValue.ownerObject())
                .ownerUid(reservedValue.ownerUid())
                .key(reservedValue.key())
                .value(reservedValue.value())
                .created(reservedValue.created())
                .expiryDate(reservedValue.expiryDate())
                .build();
    }

    private void fillTemporalValidityDate(String pattern) {
        Date temporalValidityDate;
        try {
            temporalValidityDate = new TrackedEntityAttributeReservedValueValidatorHelper()
                    .getExpiryDateCode(pattern);
        } catch (IllegalStateException e) {
            temporalValidityDate = null;
        }

        this.builder.temporalValidityDate(temporalValidityDate);
    }
}