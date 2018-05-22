package org.hisp.dhis.android.core.trackedentity;

import java.util.Collection;
import java.util.Set;

interface TrackedEntityInstanceUidHelper {
    Set<String> getMissingOrganisationUnitUids(Collection<TrackedEntityInstance> trackedEntityInstances);
}