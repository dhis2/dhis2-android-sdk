package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;

import java.util.Collection;

public class TrackedEntityInstancePersistenceCall extends SyncCall<Void> {

    private final DatabaseAdapter databaseAdapter;
    private final TrackedEntityInstanceHandler trackedEntityInstanceHandler;
    private final Collection<TrackedEntityInstance> trackedEntityInstances;

    private TrackedEntityInstancePersistenceCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull TrackedEntityInstanceHandler trackedEntityInstanceHandler,
            @NonNull Collection<TrackedEntityInstance> trackedEntityInstances) {
        this.databaseAdapter = databaseAdapter;
        this.trackedEntityInstanceHandler = trackedEntityInstanceHandler;
        this.trackedEntityInstances = trackedEntityInstances;
    }

    @Override
    public Void call() throws Exception {
        super.setExecuted();

        Transaction transaction = databaseAdapter.beginNewTransaction();
        trackedEntityInstanceHandler.handleMany(trackedEntityInstances);

        // TODO download enrollments
        // TODO download events
        // TODO get organisation units
        // TODO persist organisationUnits

        transaction.setSuccessful();
        transaction.end();

        return Void.class.getDeclaredConstructor().newInstance();
    }

    public static TrackedEntityInstancePersistenceCall create(DatabaseAdapter databaseAdapter,
                                                              Collection<TrackedEntityInstance>
                                                                      trackedEntityInstances) {
        return new TrackedEntityInstancePersistenceCall(
                databaseAdapter,
                TrackedEntityInstanceHandler.create(databaseAdapter),
                trackedEntityInstances
        );
    }
}
