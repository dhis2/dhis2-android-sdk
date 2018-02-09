/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import retrofit2.Response;

public class TrackedEntityCall implements Call<Response<Payload<TrackedEntity>>> {

    private final TrackedEntityService service;
    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityHandler trackedEntityHandler;
    private final ResourceHandler resourceHandler;
    private final Set<String> uidSet;
    private final Date serverDate;
    private final ResourceModel.Type resourceType = ResourceModel.Type.TRACKED_ENTITY;

    private Boolean isExecuted = false;

    public TrackedEntityCall(@Nullable Set<String> uidSet,
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull TrackedEntityHandler trackedEntityHandler,
            @NonNull ResourceHandler resourceHandler,
            @NonNull TrackedEntityService service,
            @NonNull Date serverDate) {
        this.uidSet = uidSet;
        this.databaseAdapter = databaseAdapter;
        this.trackedEntityHandler = trackedEntityHandler;
        this.resourceHandler = resourceHandler;
        this.service = service;
        this.serverDate = new Date(serverDate.getTime());
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<TrackedEntity>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        if (uidSet.size() > MAX_UIDS) {
            throw new IllegalArgumentException(
                    "Can't handle the amount of tracked entities: " + uidSet.size() + ". " +
                            "Max size is: " + MAX_UIDS);
        }

        String lastUpdated = resourceHandler.getLastUpdated(resourceType);
        Response<Payload<TrackedEntity>> response = getTrackedEntities(lastUpdated);

        Transaction transaction = databaseAdapter.beginNewTransaction();
        try {

            if (response != null && response.isSuccessful()) {
                List<TrackedEntity> trackedEntities = response.body().items();
                int size = trackedEntities.size();

                for (int i = 0; i < size; i++) {
                    TrackedEntity trackedEntity = trackedEntities.get(i);

                    trackedEntityHandler.handleTrackedEntity(trackedEntity);
                }
                resourceHandler.handleResource(
                        resourceType,
                        serverDate
                );
                transaction.setSuccessful();
            }
        } finally {
            transaction.end();
        }
        return response;
    }

    private Response<Payload<TrackedEntity>> getTrackedEntities(String lastUpdated)
            throws IOException {
        return service.trackedEntities(
                Fields.<TrackedEntity>builder().fields(
                        TrackedEntity.uid, TrackedEntity.code, TrackedEntity.name,
                        TrackedEntity.displayName, TrackedEntity.created, TrackedEntity.lastUpdated,
                        TrackedEntity.shortName, TrackedEntity.displayShortName,
                        TrackedEntity.description, TrackedEntity.displayDescription,
                        TrackedEntity.deleted
                ).build(),
                TrackedEntity.uid.in(uidSet),
                TrackedEntity.lastUpdated.gt(lastUpdated),
                false
        ).execute();
    }
}
