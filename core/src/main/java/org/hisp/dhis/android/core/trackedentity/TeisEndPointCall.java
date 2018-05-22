package org.hisp.dhis.android.core.trackedentity;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.util.Log;

import org.hisp.dhis.android.core.common.GenericCallData;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.utils.Utils;

import java.util.List;

import retrofit2.Response;

public final class TeisEndPointCall extends SyncCall<Response<Payload<TrackedEntityInstance>>> {

    private final GenericCallData genericCallData;
    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final TeiQuery trackerQuery;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;

    private TeisEndPointCall(
            @NonNull GenericCallData genericCallData,
            @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
            @NonNull TeiQuery trackerQuery,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler) {
        this.genericCallData = genericCallData;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackerQuery = trackerQuery;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
    }

    @Override
    public Response<Payload<TrackedEntityInstance>> call() throws Exception {
        super.setExecuted();

        Integer teisToRequest = Math.min(trackerQuery.getPageLimit(), trackerQuery.getPageSize());
        Response<Payload<TrackedEntityInstance>> response = trackedEntityInstanceService.getTrackedEntityInstances(
                Utils.joinCollectionWithSeparator(trackerQuery.getOrgUnits(), ";"),
                trackerQuery.getOuMode().name(), TrackedEntityInstance.allFields, Boolean.TRUE,
                trackerQuery.getPage(), teisToRequest)
                .execute();

        if (response.isSuccessful() && !response.body().items().isEmpty()) {
            List<TrackedEntityInstance> trackedEntityInstances = response.body().items();
            persistTeis(trackedEntityInstances);
        }

        return response;
    }

    private void persistTeis(List<TrackedEntityInstance> trackedEntityInstances) {
        Transaction transaction = genericCallData.databaseAdapter().beginNewTransaction();
        try {
            trackedEntityInstanceHandler.handleMany(trackedEntityInstances);
            transaction.setSuccessful();
        } catch (SQLiteConstraintException sql) {
                /* This catch is necessary to ignore events with bad foreign keys exception
                More info: If the foreign key have the flag DEFERRABLE INITIALLY DEFERRED this exception will be
                throw in transaction.end()
                And the rollback will be executed only when the database is closed.
                It is a reported as unfixed bug: https://issuetracker.google.com/issues/37001653 */
            Log.d(this.getClass().getSimpleName(), sql.getMessage());
        } finally {
            transaction.end();
        }
    }

    public static TeisEndPointCall create(GenericCallData genericCallData, TeiQuery teiQuery) {
        return new TeisEndPointCall(
                genericCallData,
                genericCallData.retrofit().create(TrackedEntityInstanceService.class),
                teiQuery,
                TrackedEntityInstanceHandler.create(genericCallData.databaseAdapter()));
    }
}