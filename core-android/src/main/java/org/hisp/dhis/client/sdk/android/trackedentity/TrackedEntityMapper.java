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

import org.hisp.dhis.client.sdk.android.common.base.AbsMapper;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityFlow;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;

public class TrackedEntityMapper extends AbsMapper<TrackedEntity, TrackedEntityFlow> {

    public TrackedEntityMapper() {
        // empty constructor
    }

    @Override
    public TrackedEntityFlow mapToDatabaseEntity(TrackedEntity trackedEntity) {
        if (trackedEntity == null) {
            return null;
        }

        TrackedEntityFlow trackedEntityFlow = new TrackedEntityFlow();
        trackedEntityFlow.setId(trackedEntity.getId());
        trackedEntityFlow.setUId(trackedEntity.getUId());
        trackedEntityFlow.setCreated(trackedEntity.getCreated());
        trackedEntityFlow.setLastUpdated(trackedEntity.getLastUpdated());
        trackedEntityFlow.setName(trackedEntity.getName());
        trackedEntityFlow.setDisplayName(trackedEntity.getDisplayName());
        trackedEntityFlow.setAccess(trackedEntity.getAccess());
        return trackedEntityFlow;
    }

    @Override
    public TrackedEntity mapToModel(TrackedEntityFlow trackedEntityFlow) {
        if (trackedEntityFlow == null) {
            return null;
        }

        TrackedEntity trackedEntity = new TrackedEntity();
        trackedEntity.setId(trackedEntityFlow.getId());
        trackedEntity.setUId(trackedEntityFlow.getUId());
        trackedEntity.setCreated(trackedEntityFlow.getCreated());
        trackedEntity.setLastUpdated(trackedEntityFlow.getLastUpdated());
        trackedEntity.setName(trackedEntityFlow.getName());
        trackedEntity.setDisplayName(trackedEntityFlow.getDisplayName());
        trackedEntity.setAccess(trackedEntityFlow.getAccess());
        return trackedEntity;
    }

    @Override
    public Class<TrackedEntity> getModelTypeClass() {
        return TrackedEntity.class;
    }

    @Override
    public Class<TrackedEntityFlow> getDatabaseEntityTypeClass() {
        return TrackedEntityFlow.class;
    }
}
