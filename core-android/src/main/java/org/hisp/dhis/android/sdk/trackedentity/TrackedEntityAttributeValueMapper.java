/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.trackedentity;

import org.hisp.dhis.android.sdk.common.base.AbsMapper;
import org.hisp.dhis.android.sdk.common.base.IMapper;
import org.hisp.dhis.android.sdk.flow.TrackedEntityAttributeValue$Flow;
import org.hisp.dhis.android.sdk.flow.TrackedEntityInstance$Flow;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityInstance;

public class TrackedEntityAttributeValueMapper extends AbsMapper<TrackedEntityAttributeValue,
        TrackedEntityAttributeValue$Flow> {

    private final IMapper<TrackedEntityInstance, TrackedEntityInstance$Flow> trackedEntityInstanceMapper;

    public TrackedEntityAttributeValueMapper(IMapper<TrackedEntityInstance, TrackedEntityInstance$Flow> trackedEntityInstanceMapper) {
        this.trackedEntityInstanceMapper = trackedEntityInstanceMapper;
    }

    @Override
    public TrackedEntityAttributeValue$Flow mapToDatabaseEntity(TrackedEntityAttributeValue trackedEntityAttributeValue) {
        if (trackedEntityAttributeValue == null) {
            return null;
        }

        TrackedEntityAttributeValue$Flow trackedEntityAttributeValueFlow = new TrackedEntityAttributeValue$Flow();
        trackedEntityAttributeValueFlow.setId(trackedEntityAttributeValue.getId());
        trackedEntityAttributeValueFlow.setTrackedEntityAttributeUId(trackedEntityAttributeValue.getTrackedEntityAttributeUId());
        trackedEntityAttributeValueFlow.setTrackedEntityInstance(trackedEntityInstanceMapper.mapToDatabaseEntity(trackedEntityAttributeValue.getTrackedEntityInstance()));
        trackedEntityAttributeValueFlow.setValue(trackedEntityAttributeValue.getValue());
        return trackedEntityAttributeValueFlow;
    }

    @Override
    public TrackedEntityAttributeValue mapToModel(TrackedEntityAttributeValue$Flow trackedEntityAttributeValueFlow) {
        if (trackedEntityAttributeValueFlow == null) {
            return null;
        }

        TrackedEntityAttributeValue trackedEntityAttributeValue = new TrackedEntityAttributeValue();
        trackedEntityAttributeValue.setId(trackedEntityAttributeValueFlow.getId());
        trackedEntityAttributeValue.setTrackedEntityAttributeUId(trackedEntityAttributeValueFlow.getTrackedEntityAttributeUId());
        trackedEntityAttributeValue.setTrackedEntityInstance(trackedEntityInstanceMapper.mapToModel(trackedEntityAttributeValueFlow.getTrackedEntityInstance()));
        trackedEntityAttributeValue.setValue(trackedEntityAttributeValueFlow.getValue());
        return trackedEntityAttributeValue;
    }

    @Override
    public Class<TrackedEntityAttributeValue> getModelTypeClass() {
        return TrackedEntityAttributeValue.class;
    }

    @Override
    public Class<TrackedEntityAttributeValue$Flow> getDatabaseEntityTypeClass() {
        return TrackedEntityAttributeValue$Flow.class;
    }
}
