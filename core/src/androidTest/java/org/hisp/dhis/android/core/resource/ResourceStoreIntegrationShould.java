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

package org.hisp.dhis.android.core.resource;

import androidx.test.runner.AndroidJUnit4;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.DatabaseAdapterFactory;
import org.hisp.dhis.android.core.data.database.ObjectWithoutUidStoreAbstractIntegrationShould;
import org.hisp.dhis.android.core.data.resource.ResourceSamples;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ResourceStoreIntegrationShould extends ObjectWithoutUidStoreAbstractIntegrationShould<Resource> {

    private ResourceStore store;

    public ResourceStoreIntegrationShould() {
        super(ResourceStoreImpl.create(DatabaseAdapterFactory.get(false)), ResourceTableInfo.TABLE_INFO,
                DatabaseAdapterFactory.get(false));
        this.store = ResourceStoreImpl.create(DatabaseAdapterFactory.get(false));
    }

    @Override
    protected Resource buildObject() {
        return ResourceSamples.getResource();
    }

    @Override
    protected Resource buildObjectToUpdate() {
        return ResourceSamples.getResource().toBuilder()
                .lastSynced(new Date())
                .build();
    }

    @Override
    protected Resource buildObjectWithId() {
        return ResourceSamples.getResource().toBuilder()
                .id(1L)
                .build();
    }

    @Test
    public void return_last_updated() {
        store.insert(ResourceSamples.getResource());
        String lastUpdated = store.getLastUpdated(Resource.Type.PROGRAM);

        assertThat(lastUpdated).isEqualTo(BaseIdentifiableObject.DATE_FORMAT
                .format(ResourceSamples.getResource().lastSynced()));
    }

    @Test
    public void delete_resource() {
        store.insert(ResourceSamples.getResource());

        String lastUpdatedBefore = store.getLastUpdated(Resource.Type.PROGRAM);
        assertThat(lastUpdatedBefore).isNotNull();

        store.deleteResource(Resource.Type.PROGRAM);

        String lastUpdatedAfter = store.getLastUpdated(Resource.Type.PROGRAM);
        assertThat(lastUpdatedAfter).isNull();
    }
}