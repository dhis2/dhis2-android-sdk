/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.core.persistence.models.flow;

import com.raizlabs.android.dbflow.annotation.Table;

import org.hisp.dhis.android.sdk.core.persistence.models.common.meta.DbDhis;
import org.hisp.dhis.android.sdk.models.trackedentity.TrackedEntity;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = DbDhis.NAME)
public final class TrackedEntity$Flow extends BaseIdentifiableObject$Flow {

    public TrackedEntity$Flow() {
        // empty constructor
    }

    public static TrackedEntity toModel(TrackedEntity$Flow trackedEntityFlow) {
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

    public static TrackedEntity$Flow fromModel(TrackedEntity trackedEntity) {
        if (trackedEntity == null) {
            return null;
        }

        TrackedEntity$Flow trackedEntityFlow = new TrackedEntity$Flow();
        trackedEntityFlow.setId(trackedEntity.getId());
        trackedEntityFlow.setUId(trackedEntity.getUId());
        trackedEntityFlow.setCreated(trackedEntity.getCreated());
        trackedEntityFlow.setLastUpdated(trackedEntity.getLastUpdated());
        trackedEntityFlow.setName(trackedEntity.getName());
        trackedEntityFlow.setDisplayName(trackedEntity.getDisplayName());
        trackedEntityFlow.setAccess(trackedEntity.getAccess());
        return trackedEntityFlow;
    }

    public static List<TrackedEntity> toModels(List<TrackedEntity$Flow> trackedEntityFlows) {
        List<TrackedEntity> trackedEntities = new ArrayList<>();

        if (trackedEntityFlows != null && !trackedEntityFlows.isEmpty()) {
            for (TrackedEntity$Flow trackedEntityFlow : trackedEntityFlows) {
                trackedEntities.add(toModel(trackedEntityFlow));
            }
        }

        return trackedEntities;
    }

    public static List<TrackedEntity$Flow> fromModels(List<TrackedEntity> trackedEntities) {
        List<TrackedEntity$Flow> trackedEntityFlows = new ArrayList<>();

        if (trackedEntities != null && !trackedEntities.isEmpty()) {
            for (TrackedEntity trackedEntity : trackedEntities) {
                trackedEntityFlows.add(fromModel(trackedEntity));
            }
        }

        return trackedEntityFlows;
    }
}
