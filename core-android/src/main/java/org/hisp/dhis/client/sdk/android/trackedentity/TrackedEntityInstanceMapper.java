/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.android.common.AbsMapper;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityInstanceFlow;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

public class TrackedEntityInstanceMapper extends AbsMapper<TrackedEntityInstance,
        TrackedEntityInstanceFlow> {

    @Override
    public TrackedEntityInstanceFlow mapToDatabaseEntity(TrackedEntityInstance trackedEntityInstance) {
        if (trackedEntityInstance == null) {
            return null;
        }

        TrackedEntityInstanceFlow trackedEntityInstanceFlow = new TrackedEntityInstanceFlow();
        trackedEntityInstanceFlow.setId(trackedEntityInstance.getId());
        trackedEntityInstanceFlow.setTrackedEntityInstanceUid(trackedEntityInstance.getTrackedEntityInstanceUid());
        trackedEntityInstanceFlow.setTrackedEntity(trackedEntityInstance.getTrackedEntity());
        trackedEntityInstanceFlow.setOrgUnit(trackedEntityInstance.getOrgUnit());
        trackedEntityInstanceFlow.setCreated(trackedEntityInstance.getCreated());
        trackedEntityInstanceFlow.setLastUpdated(trackedEntityInstance.getLastUpdated());
        return trackedEntityInstanceFlow;
    }

    @Override
    public TrackedEntityInstance mapToModel(TrackedEntityInstanceFlow trackedEntityInstanceFlow) {
        if (trackedEntityInstanceFlow == null) {
            return null;
        }

        TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setId(trackedEntityInstanceFlow.getId());
        trackedEntityInstance.setTrackedEntityInstanceUid(trackedEntityInstanceFlow.getTrackedEntityInstanceUid());
        trackedEntityInstance.setTrackedEntity(trackedEntityInstanceFlow.getTrackedEntity());
        trackedEntityInstance.setOrgUnit(trackedEntityInstanceFlow.getOrgUnit());
        trackedEntityInstance.setCreated(trackedEntityInstanceFlow.getCreated());
        trackedEntityInstance.setLastUpdated(trackedEntityInstanceFlow.getLastUpdated());
        return trackedEntityInstance;
    }

    @Override
    public Class<TrackedEntityInstance> getModelTypeClass() {
        return TrackedEntityInstance.class;
    }

    @Override
    public Class<TrackedEntityInstanceFlow> getDatabaseEntityTypeClass() {
        return TrackedEntityInstanceFlow.class;
    }
}
