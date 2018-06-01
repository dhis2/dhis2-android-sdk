package org.hisp.dhis.android.core.event;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.SyncCall;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.data.database.Transaction;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkModel;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStore;
import org.hisp.dhis.android.core.user.UserOrganisationUnitLinkStoreInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Retrofit;

public final class EventWithLimitCall extends SyncCall<List<Event>> {
    private final int eventLimit;
    private final boolean limitByOrgUnit;
    private final DatabaseAdapter databaseAdapter;
    private final Retrofit retrofit;
    private final UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore;
    private final D2CallException.Builder httpExceptionBuilder;

    private EventWithLimitCall(
            @NonNull DatabaseAdapter databaseAdapter,
            @NonNull Retrofit retrofit,
            @NonNull UserOrganisationUnitLinkStoreInterface userOrganisationUnitLinkStore,
            int eventLimit,
            boolean limitByOrgUnit) {
        this.databaseAdapter = databaseAdapter;
        this.retrofit = retrofit;
        this.userOrganisationUnitLinkStore = userOrganisationUnitLinkStore;
        this.eventLimit = eventLimit;
        this.limitByOrgUnit = limitByOrgUnit;
        this.httpExceptionBuilder = D2CallException.builder().isHttpError(true).errorDescription("Events call failed");
    }

    @Override
    public List<Event> call() throws D2CallException {
        this.setExecuted();
        Transaction transaction = databaseAdapter.beginNewTransaction();

        try {
            List<Event> events = getEvents();

            transaction.setSuccessful();

            return events;

        } catch (Exception e) {
            throw httpExceptionBuilder.originalException(e).build();
        } finally {
            transaction.end();
        }
    }

    private List<Event> getEvents() throws D2CallException {
        Set<String> organisationUnitUids;
        List<Event> events = new ArrayList<>();
        EventQuery.Builder eventQueryBuilder = EventQuery.Builder.create();
        int pageSize = eventQueryBuilder.build().getPageSize();
        int numPages = (int) Math.ceil((double) eventLimit / pageSize);

        if (limitByOrgUnit) {
            organisationUnitUids = getOrgUnitUids();
        } else {
            organisationUnitUids = userOrganisationUnitLinkStore.queryRootOrganisationUnitUids();
            eventQueryBuilder.withOuMode(OuMode.DESCENDANTS);
        }

        for (String orgUnitUid : organisationUnitUids) {
            eventQueryBuilder.withOrgUnit(orgUnitUid);
            events.addAll(getEventsWithPaging(eventQueryBuilder, pageSize, numPages));
        }

        return events;
    }

    private List<Event> getEventsWithPaging(EventQuery.Builder eventQueryBuilder, int pageSize, int numPages)
            throws D2CallException {
        List<Event> events = new ArrayList<>();

        for (int page = 1; page <= numPages; page++) {
            if (page == numPages) {
                eventQueryBuilder.withPage(page).withPageLimit(eventLimit - ((page - 1) * pageSize));
            }

            if (!limitByOrgUnit && eventQueryBuilder.pageLimit + events.size() > eventLimit) {
                eventQueryBuilder.withPageLimit(eventLimit - events.size());
            }

            eventQueryBuilder.withPage(page);
            List<Event> pageEvents = EventEndpointCall.create(
                    retrofit, eventQueryBuilder.build()).call();

            events.addAll(pageEvents);

            if (pageEvents.size() < pageSize) {
                break;
            }
        }

        EventPersistenceCall.create(databaseAdapter, retrofit, events).call();

        return events;
    }

    private Set<String> getOrgUnitUids() {
        Set<UserOrganisationUnitLinkModel> userOrganisationUnitLinks = userOrganisationUnitLinkStore.selectAll(
                UserOrganisationUnitLinkModel.factory);

        Set<String> organisationUnitUids = new HashSet<>();

        for (UserOrganisationUnitLinkModel linkModel: userOrganisationUnitLinks) {
            if (linkModel.organisationUnitScope().equals(
                    OrganisationUnitModel.Scope.SCOPE_DATA_CAPTURE.name())) {
                organisationUnitUids.add(linkModel.organisationUnit());
            }
        }

        return organisationUnitUids;
    }

    public static EventWithLimitCall create(DatabaseAdapter databaseAdapter, Retrofit retrofit,
                                            int eventLimit, boolean limitByOrgUnit) {
        return new EventWithLimitCall(
                databaseAdapter,
                retrofit,
                UserOrganisationUnitLinkStore.create(databaseAdapter),
                eventLimit,
                limitByOrgUnit
        );
    }
}
