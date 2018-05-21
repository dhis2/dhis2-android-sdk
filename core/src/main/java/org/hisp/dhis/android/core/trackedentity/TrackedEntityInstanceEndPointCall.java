package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.util.Log;

import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;

import retrofit2.Response;
import retrofit2.Retrofit;

public class TrackedEntityInstanceEndPointCall extends SyncCall<Response<TrackedEntityInstance>> {

    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final String trackedEntityInstanceUid;

    TrackedEntityInstanceEndPointCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull String trackedEntityInstanceUid) {
        this.databaseAdapter = databaseAdapter;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;

        if (trackedEntityInstanceUid == null || trackedEntityInstanceUid.isEmpty()) {
            throw new IllegalArgumentException(
                    "trackedEntityInstanceUid is required to realize a request");
        }

        this.trackedEntityInstanceUid = trackedEntityInstanceUid;
    }

    @Override
    public Response<TrackedEntityInstance> call() throws Exception {
        super.setExecuted();

        Response<TrackedEntityInstance> response =
                trackedEntityInstanceService.trackedEntityInstance(trackedEntityInstanceUid,
                        TrackedEntityInstance.allFields, true).execute();

        if (response == null || !response.isSuccessful()) {
            return response;
        }

        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            TrackedEntityInstance trackedEntityInstance = response.body();
            trackedEntityInstanceHandler.handle(trackedEntityInstance);
            transaction.setSuccessful();

        } catch (SQLiteConstraintException sql) {
            // This catch is necessary to ignore events with bad foreign keys exception
            // More info: If the foreign key have the flag
            // DEFERRABLE INITIALLY DEFERRED this exception will be throw in transaction
            // .end()
            // And the rollback will be executed only when the database is closed.
            // It is a reported as unfixed bug: https://issuetracker.google
            // .com/issues/37001653
            Log.d(this.getClass().getSimpleName(), sql.getMessage());
        } finally {
            transaction.end();
        }

        return response;
    }

    public static TrackedEntityInstanceEndPointCall create(DatabaseAdapter databaseAdapter,
                                                           Retrofit retrofit,
                                                           String trackedEntityInstanceUid) {
        return new TrackedEntityInstanceEndPointCall(
                databaseAdapter,
                retrofit.create(TrackedEntityInstanceService.class),
                TrackedEntityInstanceHandler.create(databaseAdapter),
                trackedEntityInstanceUid
        );
    }
}
