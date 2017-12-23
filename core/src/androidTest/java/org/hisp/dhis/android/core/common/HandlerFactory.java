package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.category.CategoryComboHandler;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkStore;
import org.hisp.dhis.android.core.category.CategoryCategoryComboLinkStoreImpl;
import org.hisp.dhis.android.core.category.CategoryComboStore;
import org.hisp.dhis.android.core.category.CategoryComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryHandler;
import org.hisp.dhis.android.core.category.CategoryOptionComboHandler;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryLinkStore;
import org.hisp.dhis.android.core.category.CategoryOptionComboCategoryLinkStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionComboStore;
import org.hisp.dhis.android.core.category.CategoryOptionComboStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionHandler;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkStore;
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLinkStoreImpl;
import org.hisp.dhis.android.core.category.CategoryOptionStore;
import org.hisp.dhis.android.core.category.CategoryOptionStoreImpl;
import org.hisp.dhis.android.core.category.CategoryStore;
import org.hisp.dhis.android.core.category.CategoryStoreImpl;
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

    public static CategoryOptionHandler createCategoryOptionHandler(
            DatabaseAdapter databaseAdapter) {
        CategoryOptionStore categoryOptionStore = new CategoryOptionStoreImpl(databaseAdapter);

        CategoryOptionHandler categoryOptionHandler = new CategoryOptionHandler(
                categoryOptionStore);

        return categoryOptionHandler;
    }

    public static CategoryHandler createCategoryHandler(DatabaseAdapter databaseAdapter) {
        CategoryStore categoryStore = new CategoryStoreImpl(databaseAdapter);
        CategoryOptionHandler categoryOptionHandler = createCategoryOptionHandler(databaseAdapter);
        CategoryCategoryOptionLinkStore
                categoryCategoryOptionLinkStore = new CategoryCategoryOptionLinkStoreImpl(
                databaseAdapter);

        CategoryHandler categoryHandler = new CategoryHandler(categoryStore, categoryOptionHandler,
                categoryCategoryOptionLinkStore);

        return categoryHandler;
    }

    public static CategoryComboHandler createCategoryComboHandler(DatabaseAdapter databaseAdapter) {
        CategoryCategoryComboLinkStore
                categoryCategoryComboLinkStore = new CategoryCategoryComboLinkStoreImpl(
                databaseAdapter);

        CategoryOptionComboStore optionComboStore = new CategoryOptionComboStoreImpl(
                databaseAdapter);
        CategoryOptionComboHandler optionComboHandler = new CategoryOptionComboHandler(
                optionComboStore);

        CategoryComboStore store = new CategoryComboStoreImpl(databaseAdapter);

        CategoryOptionComboCategoryLinkStore
                categoryComboOptionLinkCategoryStore = new CategoryOptionComboCategoryLinkStoreImpl(
                databaseAdapter);

        CategoryComboHandler categoryComboHandler = new CategoryComboHandler(store,
                categoryComboOptionLinkCategoryStore, categoryCategoryComboLinkStore,
                optionComboHandler);

        return categoryComboHandler;
    }
}
