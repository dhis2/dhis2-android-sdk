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

import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityDataValueService;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityDataValue;

import java.util.List;

import rx.Observable;

public class TrackedEntityDataValueScopeImpl implements TrackedEntityDataValueScope {
    private final TrackedEntityDataValueService mTrackedEntityDataValueService;

    public TrackedEntityDataValueScopeImpl(TrackedEntityDataValueService entityDataValueService) {
        this.mTrackedEntityDataValueService = entityDataValueService;
    }

    @Override
    public Observable<TrackedEntityDataValue> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<TrackedEntityDataValue>() {
            @Override
            public TrackedEntityDataValue call() {
                return mTrackedEntityDataValueService.get(id);
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityDataValue>> list() {
        return Observable.create(new DefaultOnSubscribe<List<TrackedEntityDataValue>>() {
            @Override
            public List<TrackedEntityDataValue> call() {
                return mTrackedEntityDataValueService.list();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final TrackedEntityDataValue object) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return mTrackedEntityDataValueService.save(object);
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final TrackedEntityDataValue object) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return mTrackedEntityDataValueService.remove(object);
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityDataValue>> list(final Event event) {
        return Observable.create(new DefaultOnSubscribe<List<TrackedEntityDataValue>>() {
            @Override
            public List<TrackedEntityDataValue> call() {
                return mTrackedEntityDataValueService.list(event);
            }
        });
    }

    @Override
    public Observable<TrackedEntityDataValue> get(
            final Event event, final DataElement dataElement) {
        return Observable.create(new DefaultOnSubscribe<TrackedEntityDataValue>() {
            @Override
            public TrackedEntityDataValue call() {
                return mTrackedEntityDataValueService.get(event, dataElement);
            }
        });
    }
}
