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

import org.hisp.dhis.client.sdk.android.api.modules.MapperModule;
import org.hisp.dhis.client.sdk.android.common.base.AbsMapper;
import org.hisp.dhis.client.sdk.android.common.base.IMapper;
import org.hisp.dhis.client.sdk.android.flow.Event$Flow;
import org.hisp.dhis.client.sdk.android.flow.TrackedEntityDataValue$Flow;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

public class TrackedEntityDataValueMapper extends AbsMapper<TrackedEntityDataValue,
        TrackedEntityDataValue$Flow> {

    @Override
    public TrackedEntityDataValue$Flow mapToDatabaseEntity(TrackedEntityDataValue trackedEntityDataValue) {
        if (trackedEntityDataValue == null) {
            return null;
        }

        TrackedEntityDataValue$Flow trackedEntityDataValueFlow = new TrackedEntityDataValue$Flow();
        trackedEntityDataValueFlow.setId(trackedEntityDataValue.getId());
        trackedEntityDataValueFlow.setEvent(MapperModule.getInstance().getEventMapper().mapToDatabaseEntity(trackedEntityDataValue.getEvent()));
        trackedEntityDataValueFlow.setDataElement(trackedEntityDataValue.getDataElement());
        trackedEntityDataValueFlow.setProvidedElsewhere(trackedEntityDataValue.isProvidedElsewhere());
        trackedEntityDataValueFlow.setStoredBy(trackedEntityDataValue.getStoredBy());
        trackedEntityDataValueFlow.setValue(trackedEntityDataValue.getValue());
        return trackedEntityDataValueFlow;
    }

    @Override
    public TrackedEntityDataValue mapToModel(TrackedEntityDataValue$Flow trackedEntityDataValueFlow) {
        if (trackedEntityDataValueFlow == null) {
            return null;
        }

        TrackedEntityDataValue trackedEntityDataValue = new TrackedEntityDataValue();
        trackedEntityDataValue.setId(trackedEntityDataValueFlow.getId());
        trackedEntityDataValue.setEvent(MapperModule.getInstance().getEventMapper().mapToModel(trackedEntityDataValueFlow.getEvent()));
        trackedEntityDataValue.setDataElement(trackedEntityDataValueFlow.getDataElement());
        trackedEntityDataValue.setProvidedElsewhere(trackedEntityDataValueFlow.isProvidedElsewhere());
        trackedEntityDataValue.setStoredBy(trackedEntityDataValueFlow.getStoredBy());
        trackedEntityDataValue.setValue(trackedEntityDataValueFlow.getValue());
        return trackedEntityDataValue;
    }

    @Override
    public Class<TrackedEntityDataValue> getModelTypeClass() {
        return TrackedEntityDataValue.class;
    }

    @Override
    public Class<TrackedEntityDataValue$Flow> getDatabaseEntityTypeClass() {
        return TrackedEntityDataValue$Flow.class;
    }
}
