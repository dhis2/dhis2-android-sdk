/* * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.*/

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.EndpointListCall;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;

public final class TrackedEntityAttributeReservedValueEndpointCall extends
        EndpointListCall<TrackedEntityAttributeReservedValue, TrackedEntityAttributeReservedValueModel,
                                TrackedEntityAttributeReservedValueQuery> {
    private final TrackedEntityAttributeReservedValueService service;

    private TrackedEntityAttributeReservedValueEndpointCall(
            GenericCallData data, TrackedEntityAttributeReservedValueService service,
            GenericHandler<TrackedEntityAttributeReservedValue, TrackedEntityAttributeReservedValueModel> handler,
            TrackedEntityAttributeReservedValueQuery query, String trackedEntityAttributePattern) {
        super(data, handler, ResourceModel.Type.TRACKED_ENTITY_ATTRIBUTE_RESERVED_VALUE,
                new TrackedEntityAttributeReservedValueModelBuilder(
                        query.organisationUnit(), trackedEntityAttributePattern), query);
        this.service = service;
    }

    @Override
    protected Call<List<TrackedEntityAttributeReservedValue>> getList(
            TrackedEntityAttributeReservedValueQuery query, String lastUpdated) throws IOException {
        return service.generateAndReserve(
                query.trackedEntityAttributeUid(),
                query.numberToReserve(),
                query.organisationUnit().code());
    }

    public interface Factory {
        TrackedEntityAttributeReservedValueEndpointCall create(
                GenericCallData data, String trackedEntityAttributeUid, Integer numberToReserve,
                OrganisationUnitModel organisationUnit);
    }

    public static final TrackedEntityAttributeReservedValueEndpointCall.Factory FACTORY =
            new TrackedEntityAttributeReservedValueEndpointCall.Factory() {
        @Override
        public TrackedEntityAttributeReservedValueEndpointCall create(
                GenericCallData data, String trackedEntityAttributeUid, Integer numberToReserve,
                OrganisationUnitModel organisationUnit) {
            String trackedEntityAttributePattern;
            try {
                trackedEntityAttributePattern = new TrackedEntityAttributeStoreImpl(data.databaseAdapter())
                        .getPattern(trackedEntityAttributeUid);
            } catch (Exception e) {
                trackedEntityAttributePattern = "";
            }

            return new TrackedEntityAttributeReservedValueEndpointCall(data, data.retrofit().create(
                    TrackedEntityAttributeReservedValueService.class),
                    TrackedEntityAttributeReservedValueHandler.create(data.databaseAdapter()),
                    TrackedEntityAttributeReservedValueQuery.create(trackedEntityAttributeUid, numberToReserve,
                            organisationUnit),
                    trackedEntityAttributePattern);
        }
    };
}