package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.EnrollmentHandler;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.EventHandler;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.resource.ResourceHandler;
import org.hisp.dhis.android.core.resource.ResourceStore;
import org.hisp.dhis.android.core.resource.ResourceStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceStoreImpl;

public class HandlerFactory {

    public static TrackedEntityInstanceHandler createTrackedEntityInstanceHandler(
            DatabaseAdapter databaseAdapter) {

        TrackedEntityInstanceStore trackedEntityInstanceStore =
                new TrackedEntityInstanceStoreImpl(databaseAdapter);

        TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler =
                createTrackedEntityAttributeValueHandler(databaseAdapter);

        EnrollmentHandler enrollmentHandler = createEnrollmentHandler(databaseAdapter);

        TrackedEntityInstanceHandler trackedEntityInstanceHandler =
                new TrackedEntityInstanceHandler(
                        trackedEntityInstanceStore,
                        trackedEntityAttributeValueHandler,
                        enrollmentHandler);

        return trackedEntityInstanceHandler;
    }

    public static TrackedEntityAttributeValueHandler createTrackedEntityAttributeValueHandler(
            DatabaseAdapter databaseAdapter) {

        TrackedEntityAttributeValueStore entityAttributeValueStore =
                new TrackedEntityAttributeValueStoreImpl(databaseAdapter);

        TrackedEntityAttributeValueHandler trackedEntityAttributeValueHandler =
                new TrackedEntityAttributeValueHandler(entityAttributeValueStore);

        return trackedEntityAttributeValueHandler;
    }

    public static EnrollmentHandler createEnrollmentHandler(
            DatabaseAdapter databaseAdapter) {

        EnrollmentStore enrollmentStore =
                new EnrollmentStoreImpl(databaseAdapter);

        EventHandler eventHandler = createEventHandler(databaseAdapter);

        EnrollmentHandler enrollmentHandler =
                new EnrollmentHandler(enrollmentStore, eventHandler);


        return enrollmentHandler;
    }

    public static EventHandler createEventHandler(DatabaseAdapter databaseAdapter) {

        TrackedEntityDataValueHandler trackedEntityDataValueHandler =
                createTrackedEntityDataValueHandler(databaseAdapter);

        EventStore eventStore = new EventStoreImpl(databaseAdapter);

        EventHandler eventHandler = new EventHandler(eventStore, trackedEntityDataValueHandler);

        return eventHandler;
    }

    public static TrackedEntityDataValueHandler createTrackedEntityDataValueHandler
            (DatabaseAdapter databaseAdapter) {

        TrackedEntityDataValueStore trackedEntityDataValueStore =
                new TrackedEntityDataValueStoreImpl(databaseAdapter);

        TrackedEntityDataValueHandler trackedEntityDataValueHandler =
                new TrackedEntityDataValueHandler(trackedEntityDataValueStore);

        return trackedEntityDataValueHandler;
    }

    public static ResourceHandler createResourceHandler
            (DatabaseAdapter databaseAdapter) {

        ResourceStore resourceStore =
                new ResourceStoreImpl(databaseAdapter);

        ResourceHandler resourceHandler = new ResourceHandler(resourceStore);

        return resourceHandler;
    }
}
