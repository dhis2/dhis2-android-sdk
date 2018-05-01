/*
 * Copyright (c) 2017, University of Oslo
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
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;

import java.util.List;

public final class TrackedEntityAttributeReservedValueManager {

    private final ObjectWithoutUidStore<TrackedEntityAttributeReservedValueModel> store;
    private final IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;
    private final GenericCallData data;

    private TrackedEntityAttributeReservedValueManager(GenericCallData data) {
        this.data = data;
        this.store = TrackedEntityAttributeReservedValueStore.create(data.databaseAdapter());
        this.organisationUnitStore = OrganisationUnitStore.create(data.databaseAdapter());
    }

    public String getValue(String attribute, String organisationUnitUid) throws RuntimeException {
        List<TrackedEntityAttributeReservedValueModel> reservedValues = getReservedValues(attribute,
                organisationUnitUid);
        TrackedEntityAttributeReservedValueModel reservedValue = null;

        try {
            reservedValue = reservedValues.iterator().next();
        } catch (Exception e) {
            throw new RuntimeException("There are no reserved values");
        }

        deleteReservedValue(reservedValue);

        try {
            syncReservedValues(attribute, organisationUnitUid);
        } catch (Exception e) {
            throw new RuntimeException("Synchronization was not successful");
        }

        return reservedValue.value();
    }

    List<TrackedEntityAttributeReservedValueModel> getReservedValues(String attributeUid, String orgUnitUid) {
        return this.store.selectWhere(TrackedEntityAttributeReservedValueModel.factory, attributeUid, orgUnitUid);
    }

    private void deleteReservedValue(TrackedEntityAttributeReservedValueModel reservedValueModel) {
        store.deleteWhere(reservedValueModel);
    }

    private void syncReservedValues(String attribute, String organisationUnitUid) throws Exception {
        List<TrackedEntityAttributeReservedValueModel> reservedValues = getReservedValues(attribute,
                organisationUnitUid);

        for (TrackedEntityAttributeReservedValueModel reservedValue : reservedValues) {
            if (reservedValue.expiryDate().getTime() < System.currentTimeMillis()) {
                deleteReservedValue(reservedValue);
            }
        }

        reservedValues = getReservedValues(attribute, organisationUnitUid);
        if (reservedValues.size() < 50) {
            fillReservedValues(attribute, organisationUnitUid, reservedValues.size());
        }
    }

    private void fillReservedValues(String attribute, String organisationUnitUid, Integer size) throws Exception {
        Integer numberToReserve = 100 - size;
        OrganisationUnitModel organisationUnitModel =
                this.organisationUnitStore.selectByUid(organisationUnitUid, OrganisationUnitModel.factory);
        TrackedEntityAttributeReservedValueEndpointCall.FACTORY.create(
                data, attribute, numberToReserve, organisationUnitModel).call();
    }

    public static TrackedEntityAttributeReservedValueManager create(GenericCallData data) {
        return new TrackedEntityAttributeReservedValueManager(data);
    }
}