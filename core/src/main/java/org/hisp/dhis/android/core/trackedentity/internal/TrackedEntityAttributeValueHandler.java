/*
 *  Copyright (c) 2004-2021, University of Oslo
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
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.ObjectWithoutUidHandlerImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class TrackedEntityAttributeValueHandler extends ObjectWithoutUidHandlerImpl<TrackedEntityAttributeValue> {
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;

    TrackedEntityAttributeValueHandler(TrackedEntityAttributeValueStore trackedEntityAttributeValueStore) {
        super(trackedEntityAttributeValueStore);
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
    }

    @Override
    protected void afterCollectionHandled(Collection<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
        removeNotExistingAttributeValuesInServer(trackedEntityAttributeValues);
    }

    private void removeNotExistingAttributeValuesInServer(
            Collection<TrackedEntityAttributeValue> trackedEntityAttributeValues) {
        if (trackedEntityAttributeValues.isEmpty()) {
            return;
        }

        String teiUid = trackedEntityAttributeValues.iterator().next().trackedEntityInstance();

        List<String> attributeUids = new ArrayList<>();
        for (TrackedEntityAttributeValue value : trackedEntityAttributeValues) {
            attributeUids.add(value.trackedEntityAttribute());
        }

        trackedEntityAttributeValueStore.deleteByInstanceAndNotInAttributes(teiUid, attributeUids);
    }

    public static HandlerWithTransformer<TrackedEntityAttributeValue> create(DatabaseAdapter databaseAdapter) {
        return new TrackedEntityAttributeValueHandler(TrackedEntityAttributeValueStoreImpl.create(databaseAdapter));
    }
}