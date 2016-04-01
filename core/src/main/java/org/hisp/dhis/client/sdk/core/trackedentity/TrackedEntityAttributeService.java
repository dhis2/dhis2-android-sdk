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

package org.hisp.dhis.client.sdk.core.trackedentity;

import org.hisp.dhis.client.sdk.core.common.persistence.IIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;

import java.util.List;

public class TrackedEntityAttributeService implements ITrackedEntityAttributeService {
    private ITrackedEntityAttributeStore trackedEntityAttributeStore;

    public TrackedEntityAttributeService(ITrackedEntityAttributeStore trackedEntityAttributeStore) {
        this.trackedEntityAttributeStore = trackedEntityAttributeStore;
    }

    @Override
    public TrackedEntityAttribute get(long id) {
        return trackedEntityAttributeStore.queryById(id);
    }

    @Override
    public TrackedEntityAttribute get(String uid) {
        return trackedEntityAttributeStore.queryByUid(uid);
    }

    @Override
    public List<TrackedEntityAttribute> list() {
        return trackedEntityAttributeStore.queryAll();

    }

    @Override
    public boolean remove(TrackedEntityAttribute object) {
        Preconditions.isNull(object, "Object must not be null");
        return trackedEntityAttributeStore.delete(object);
    }

    @Override
    public boolean save(TrackedEntityAttribute object) {
        Preconditions.isNull(object, "Object must not be null");
        return trackedEntityAttributeStore.save(object);
    }
}
