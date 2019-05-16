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

import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.calls.processors.CallProcessor;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;

import java.util.Date;
import java.util.List;

class TrackedEntityAttributeReservedValueCallProcessor implements CallProcessor<TrackedEntityAttributeReservedValue> {

    private final DatabaseAdapter databaseAdapter;
    private final SyncHandler<TrackedEntityAttributeReservedValue> handler;
    private final String organisationUnitUid;
    private final Date temporalValidityDate;

    TrackedEntityAttributeReservedValueCallProcessor(DatabaseAdapter databaseAdapter,
                                                     SyncHandler<TrackedEntityAttributeReservedValue> handler,
                                                     OrganisationUnit organisationUnit,
                                                     String pattern) {
            this.databaseAdapter = databaseAdapter;
            this.handler = handler;
            this.organisationUnitUid = organisationUnit == null ? null : organisationUnit.uid();
            this.temporalValidityDate = fillTemporalValidityDate(pattern);
    }

    @Override
    public void process(List<TrackedEntityAttributeReservedValue> objectList) throws D2Error {
        if (objectList != null && !objectList.isEmpty()) {
            D2CallExecutor.create(databaseAdapter).executeD2CallTransactionally(() -> {

                for (TrackedEntityAttributeReservedValue trackedEntityAttributeReservedValue : objectList) {
                    handler.handle(trackedEntityAttributeReservedValue.toBuilder()
                            .organisationUnit(organisationUnitUid)
                            .temporalValidityDate(temporalValidityDate)
                            .build());
                }

                return null;
            });
        }
    }

    private Date fillTemporalValidityDate(String pattern) {
        Date temporalValidityDate;
        try {
            temporalValidityDate = new TrackedEntityAttributeReservedValueValidatorHelper().getExpiryDateCode(pattern);
        } catch (IllegalStateException e) {
            temporalValidityDate = null;
        }

        return temporalValidityDate;
    }
}