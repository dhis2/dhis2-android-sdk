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

import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.utils.HeaderUtils;

import java.util.Set;

import retrofit2.Response;

public class TrackedEntityCall implements Call<Response<Payload<TrackedEntity>>> {

    private final TrackedEntityService service;
    private final DatabaseAdapter database;
    private final TrackedEntityHandler handler;
    private final ResourceHandler resourceHandler;
    private final Set<String> uidSet;
    private Boolean isExecuted = false;

    public TrackedEntityCall(@Nullable Set<String> uidSet,
                             @NonNull DatabaseAdapter database,
                             @NonNull TrackedEntityHandler handler,
                             @NonNull ResourceHandler resourceHandler,
                             @NonNull TrackedEntityService service) {
        this.uidSet = uidSet;
        this.database = database;
        this.handler = handler;
        this.resourceHandler = resourceHandler;
        this.service = service;
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
                throw new IllegalStateException("AlreadyExecuted");
            }
            isExecuted = true;
        }
        Response<Payload<TrackedEntity>> response = null;
        Transaction transaction = database.beginNewTransaction();
        try {
            response = service.trackedEntities(
                    Fields.<TrackedEntity>builder().fields(
                            TrackedEntity.uid, TrackedEntity.code, TrackedEntity.name,
                            TrackedEntity.displayName, TrackedEntity.created, TrackedEntity.lastUpdated,
                            TrackedEntity.shortName, TrackedEntity.displayShortName,
                            TrackedEntity.description, TrackedEntity.displayDescription,
                            TrackedEntity.deleted
                    ).build(),
                    TrackedEntity.uid.in(uidSet),
                    TrackedEntity.lastUpdated.gt(resourceHandler.getLastUpdated(TrackedEntity.class.getSimpleName())),
                    false
            ).execute();

            if (response != null && response.isSuccessful()) {
                for (TrackedEntity trackedEntity : response.body().items()) {
                    handler.handleTrackedEntity(trackedEntity);
                }
                resourceHandler.handleResource(
                        ResourceModel.Type.TRACKED_ENTITY,
                        response.headers().getDate(HeaderUtils.DATE)
                );
                transaction.setSuccessful();
            }
        } finally {
            transaction.end();
        }
        return response;
    }
}
