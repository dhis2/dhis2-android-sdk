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

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.api.Filter;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.resource.ResourceModel;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.utils.HeaderUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import retrofit2.Response;

import static org.hisp.dhis.android.core.utils.CallUtils.buildInFilter;

public class TrackedEntityCall implements Call<Response<Payload<TrackedEntity>>> {

    private final TrackedEntityService service;
    private final SQLiteDatabase database;
    private final TrackedEntityStore store;
    private final ResourceStore resourceStore;
    private final Set<String> uids;
    private Boolean isExecuted = false;

    public TrackedEntityCall(@NonNull Set<String> uids,
                             @NonNull SQLiteDatabase database,
                             @NonNull TrackedEntityStore store,
                             @NonNull ResourceStore resourceStore,
                             @NonNull TrackedEntityService service
    ) {
        this.uids = uids;
        this.database = database;
        this.store = store;
        this.resourceStore = resourceStore;
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
        if (uids == null || uids.isEmpty()) {
            return null;
        }
        Response<Payload<TrackedEntity>> response = null;
        database.beginTransaction();
        try {
            Map<String, String> queryMap = new HashMap<>();
            //TODO: test the resultant url after adding these to the map:
            queryMap.put("id", buildInFilter("id", uids));
            //TODO: abstract this for all calls into utility class ? :
            String updatedDate = getLastUpdated(OrganisationUnit.class.getSimpleName());
            if (updatedDate != null && !updatedDate.isEmpty()) {
                queryMap.put(TrackedEntityModel.Columns.LAST_UPDATED,
                        TrackedEntityModel.Columns.LAST_UPDATED + ":gt:" + updatedDate);
            }

            response = getTrackedEntities(queryMap);

            if (response != null && response.isSuccessful()) {
                for (TrackedEntity trackedEntity : response.body().items()) {
                    persistTrackedEntities(trackedEntity);
                }
                updateInResourceStore(response.headers().getDate(HeaderUtils.DATE),
                        OrganisationUnit.class.getSimpleName());
                database.setTransactionSuccessful();
            }
        } finally {
            database.endTransaction();
        }
        return response;
    }

    private Response<Payload<TrackedEntity>> getTrackedEntities(Map<String, String> queryMap) throws IOException {
        Filter<TrackedEntity> filter = Filter.<TrackedEntity>builder().fields(
                TrackedEntity.uid, TrackedEntity.code, TrackedEntity.name,
                TrackedEntity.displayName, TrackedEntity.created, TrackedEntity.lastUpdated,
                TrackedEntity.shortName, TrackedEntity.displayShortName,
                TrackedEntity.description, TrackedEntity.displayDescription,
                TrackedEntity.deleted
        ).build();
        retrofit2.Call<Payload<TrackedEntity>> call = service.trackedEntities(filter, queryMap, false);
        return call.execute();
    }

    private void persistTrackedEntities(TrackedEntity trackedEntity) {
        if (trackedEntity.deleted()) {
            store.delete(trackedEntity.uid());
        } else {
            int updatedRow = store.update(
                    trackedEntity.uid(),
                    trackedEntity.code(),
                    trackedEntity.name(),
                    trackedEntity.displayName(),
                    trackedEntity.created(),
                    trackedEntity.lastUpdated(),
                    trackedEntity.shortName(),
                    trackedEntity.displayShortName(),
                    trackedEntity.description(),
                    trackedEntity.displayDescription(),
                    trackedEntity.uid()
            );
            if (updatedRow <= 0) {
                store.insert(
                        trackedEntity.uid(),
                        trackedEntity.code(),
                        trackedEntity.name(),
                        trackedEntity.displayName(),
                        trackedEntity.created(),
                        trackedEntity.lastUpdated(),
                        trackedEntity.shortName(),
                        trackedEntity.displayShortName(),
                        trackedEntity.description(),
                        trackedEntity.displayDescription()
                );
            }
        }
    }

    //TODO: use these from the stores when implemented:
    private void updateInResourceStore(Date serverDate, String className) {
        int rowId = resourceStore.update(className, serverDate,
                OrganisationUnit.class.getSimpleName());
        if (rowId <= 0) {
            resourceStore.insert(OrganisationUnit.class.getSimpleName(), serverDate);
        }
    }

    private String getLastUpdated(String className) {
        String lastUpdated = null;
        Cursor cursor = database.query(
                ResourceModel.TABLE,
                new String[]{ResourceModel.Columns.LAST_SYNCED},
                ResourceModel.Columns.RESOURCE_TYPE + "=?",
                new String[]{className},
                null, null, null
        );
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                lastUpdated = cursor.getString(cursor.getColumnIndex(ResourceModel.Columns.LAST_SYNCED));
            }
            cursor.close();
        }
        return lastUpdated;
    }
}
