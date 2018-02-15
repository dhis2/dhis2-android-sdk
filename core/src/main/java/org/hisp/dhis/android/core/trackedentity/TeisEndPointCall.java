package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.event.Event;

import java.util.Date;

import retrofit2.Response;


public class TeisEndPointCall implements Call<Response<Payload<TrackedEntityInstance>>> {

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final DatabaseAdapter databaseAdapter;
    private final TeiQuery trackerQuery;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;

    private boolean isExecuted;

    public TeisEndPointCall(@NonNull TrackedEntityInstanceService trackedEntityInstanceService,
                            @NonNull DatabaseAdapter databaseAdapter,@NonNull TeiQuery trackerQuery,
                            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler) {

        this.databaseAdapter = databaseAdapter;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackerQuery = trackerQuery;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
    }

    @Override
    public boolean isExecuted() {
        synchronized (this) {
            return isExecuted;
        }
    }

    @Override
    public Response<Payload<TrackedEntityInstance>> call() throws Exception {
        synchronized (this) {
            if (isExecuted) {
                throw new IllegalStateException("Already executed");
            }
            isExecuted = true;
        }

        Response<Payload<TrackedEntityInstance>> response = null;

        return response;
    }


}
