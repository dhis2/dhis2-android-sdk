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
package org.hisp.dhis.android.core.resource.internal;

import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.VisibleForTesting;

@Singleton
public class ResourceHandler {
    private final ResourceStore resourceStore;
    private Date serverDate;

    @Inject
    public ResourceHandler(ResourceStore resourceStore) {
        this.resourceStore = resourceStore;
    }

    public void setServerDate(Date serverDate) {
        this.serverDate = new Date(serverDate.getTime());
    }

    public Date getServerDate() {
        return new Date(this.serverDate.getTime());
    }

    public void handleResource(Resource.Type resourceType) {
        if (resourceType == null || serverDate == null) {
            return;
        }

        Resource resource = Resource.builder()
                .resourceType(resourceType)
                .lastSynced(serverDate)
                .build();

        resourceStore.updateOrInsertWhere(resource);
    }

    /**
     * A wrapper to expose resourceStore.getLastUpdated(str).
     *
     * @param type Type of the resource.
     * @return a string representing the last synched date
     */
    public String getLastUpdated(Resource.Type type) {
        return resourceStore.getLastUpdated(type);
    }

    @VisibleForTesting
    public static ResourceHandler create(DatabaseAdapter databaseAdapter) {
        return new ResourceHandler(ResourceStoreImpl.create(databaseAdapter));
    }
}