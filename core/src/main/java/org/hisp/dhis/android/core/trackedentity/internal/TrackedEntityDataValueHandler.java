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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;
import org.hisp.dhis.android.core.arch.handlers.internal.ObjectWithoutUidHandlerImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class TrackedEntityDataValueHandler extends ObjectWithoutUidHandlerImpl<TrackedEntityDataValue> {
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;

    TrackedEntityDataValueHandler(TrackedEntityDataValueStore trackedEntityDataValueStore) {
        super(trackedEntityDataValueStore);
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
    }

    public void removeEventDataValues(String eventUid) {
        trackedEntityDataValueStore.deleteByEvent(eventUid);
    }

    @Override
    protected void afterCollectionHandled(Collection<TrackedEntityDataValue> trackedEntityDataValues) {
        removeNotExistingDataValuesInServer(trackedEntityDataValues);
    }

    private void removeNotExistingDataValuesInServer(Collection<TrackedEntityDataValue> trackedEntityDataValues) {
        if (trackedEntityDataValues.isEmpty()) {
            return;
        }

        String eventUid = trackedEntityDataValues.iterator().next().event();

        List<String> dataElementUids = new ArrayList<>();
        for (TrackedEntityDataValue dataValue : trackedEntityDataValues) {
            dataElementUids.add(dataValue.dataElement());
        }

        trackedEntityDataValueStore.deleteByEventAndNotInDataElements(eventUid, dataElementUids);
    }

    public static TrackedEntityDataValueHandler create(DatabaseAdapter databaseAdapter) {
        return new TrackedEntityDataValueHandler(TrackedEntityDataValueStoreImpl.create(databaseAdapter));
    }
}