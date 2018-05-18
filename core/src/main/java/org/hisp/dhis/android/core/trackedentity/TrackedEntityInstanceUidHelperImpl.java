package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitModel;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class TrackedEntityInstanceUidHelperImpl implements TrackedEntityInstanceUidHelper {

    private final IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore;

    TrackedEntityInstanceUidHelperImpl(
            @NonNull IdentifiableObjectStore<OrganisationUnitModel> organisationUnitStore) {
        this.organisationUnitStore = organisationUnitStore;
    }

    public Set<String> getMissingOrganisationUnitUids(Collection<TrackedEntityInstance> trackedEntityInstances) {
        Set<String> uids = new HashSet<>();
        for (TrackedEntityInstance tei: trackedEntityInstances) {
            if (tei.organisationUnit() != null) {
                uids.add(tei.organisationUnit());
            }

            List<Enrollment> enrollments = tei.enrollments();
            if (enrollments != null) {
                for (Enrollment enrollment: enrollments) {
                    if (enrollment.organisationUnit() != null) {
                        uids.add(enrollment.organisationUnit());

                        List<Event> events = enrollment.events();

                        if (events != null) {
                            for (Event event: events) {
                                if (event.organisationUnit() != null) {
                                    uids.add(event.organisationUnit());
                                }
                            }
                        }
                    }
                }
            }
        }
        uids.removeAll(organisationUnitStore.selectUids());
        return uids;
    }

    public static TrackedEntityInstanceUidHelperImpl create(DatabaseAdapter databaseAdapter) {
        return new TrackedEntityInstanceUidHelperImpl(OrganisationUnitStore.create(databaseAdapter));
    }
}
