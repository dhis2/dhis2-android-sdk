/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import android.support.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.data.database.IdentifiableDataObjectStoreAbstractIntegrationShould;
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityInstanceSamples;
import org.hisp.dhis.android.core.period.FeatureType;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstanceStoreIntegrationShould extends
        IdentifiableDataObjectStoreAbstractIntegrationShould<TrackedEntityInstance> {

    public TrackedEntityInstanceStoreIntegrationShould() {
        super(TrackedEntityInstanceStoreImpl.create(DatabaseAdapterFactory.get(false)),
                TrackedEntityInstanceTableInfo.TABLE_INFO, DatabaseAdapterFactory.get(false));
    }

    @Override
    protected TrackedEntityInstance buildObject() {
        return TrackedEntityInstanceSamples.get();
    }

    @Override
    protected TrackedEntityInstance buildObjectWithId() {
        return TrackedEntityInstanceSamples.get().toBuilder()
                .id(1L)
                .build();
    }

    @Override
    protected TrackedEntityInstance buildObjectToUpdate() {
        return TrackedEntityInstanceSamples.get().toBuilder()
                .featureType(FeatureType.POINT)
                .build();
    }

    @Override
    protected TrackedEntityInstance buildObjectWithToDeleteState() {
        return TrackedEntityInstanceSamples.get().toBuilder()
                .state(State.TO_DELETE)
                .build();
    }

    @Override
    protected TrackedEntityInstance buildObjectWithSyncedState() {
        return TrackedEntityInstanceSamples.get().toBuilder()
                .state(State.SYNCED)
                .build();
    }
}