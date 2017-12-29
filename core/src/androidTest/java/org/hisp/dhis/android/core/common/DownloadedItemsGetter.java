package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.category.Category;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.option.OptionSet;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.trackedentity.TrackedEntity;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValueStoreImpl;
import org.hisp.dhis.android.core.user.User;
import org.hisp.dhis.android.core.user.UserCredentials;
import org.hisp.dhis.android.core.user.UserCredentialsStore;
import org.hisp.dhis.android.core.user.UserCredentialsStoreImpl;
import org.hisp.dhis.android.core.user.UserStoreImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DownloadedItemsGetter {


    public static List<Event> getDownloadedEvents(DatabaseAdapter databaseAdapter) {
        List<Event> downloadedEvents = new ArrayList<>();

        EventStoreImpl eventStore = new EventStoreImpl(databaseAdapter);

        List<Event> downloadedEventsWithoutValues = eventStore.querySingleEvents();

        TrackedEntityDataValueStoreImpl trackedEntityDataValue =
                new TrackedEntityDataValueStoreImpl(databaseAdapter);

        Map<String, List<TrackedEntityDataValue>> downloadedValues =
                trackedEntityDataValue.queryTrackedEntityDataValues();


        for (Event event : downloadedEventsWithoutValues) {
            event = Event.create(
                    event.uid(), event.enrollmentUid(), event.created(), event.lastUpdated(),
                    event.createdAtClient(), event.lastUpdatedAtClient(),
                    event.program(), event.programStage(), event.organisationUnit(),
                    event.eventDate(), event.status(), event.coordinates(), event.completedDate(),
                    event.dueDate(), event.deleted(), downloadedValues.get(event.uid()),
                    event.attributeCategoryOptions(),
                    event.attributeOptionCombo(), event.trackedEntityInstance());

            downloadedEvents.add(event);
        }

        return downloadedEvents;
    }


    public static List<User> getDownloadedUsers(DatabaseAdapter databaseAdapter) {
        List<User> downloadedItems = new ArrayList<>();

        UserStoreImpl store = new UserStoreImpl(databaseAdapter);

        UserCredentialsStore userCredentialsStore = new UserCredentialsStoreImpl(databaseAdapter);


        List<User> downloadedItemsWithoutDependencies = store.queryAll();

        for (User user : downloadedItemsWithoutDependencies) {

            UserCredentials userCredentials = userCredentialsStore.queryByUserUid(user.uid());

            user = User.create(
                    user.uid(), user.code(), user.name(), user.displayName(), user.created(),
                    user.lastUpdated(), user.birthday(), user.education(), user.gender(),
                    user.jobTitle(), user.surname(), user.firstName(), user.introduction(), user.employer(),
                    user.interests(), user.languages(), user.email(), user.phoneNumber(), user.nationality(),
                    userCredentials,
                    user.organisationUnits(), user.teiSearchOrganisationUnits(), user.dataViewOrganisationUnits(), null
                     );

            downloadedItems.add(user);
        }
        return downloadedItems;
    }

    public static List<OrganisationUnit> getDownloadedOrganisationUnits(
            DatabaseAdapter databaseAdapter) {
        return null;
    }

    public static List<Category> getDownloadedCategories(DatabaseAdapter databaseAdapter) {
        return null;
    }

    public static List<CategoryCombo> getDownloadedCategoryCombos(DatabaseAdapter databaseAdapter) {
        return null;
    }

    public static List<Program> getDownloadedPrograms(DatabaseAdapter databaseAdapter) {
        return null;
    }

    public static List<TrackedEntity> getDownloadedTrackedEntities(
            DatabaseAdapter databaseAdapter) {
        return null;
    }

    public static List<OptionSet> getDownloadedOptionSets(DatabaseAdapter databaseAdapter) {
        return null;
    }
}
