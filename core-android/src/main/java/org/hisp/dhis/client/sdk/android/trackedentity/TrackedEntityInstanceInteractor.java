package org.hisp.dhis.client.sdk.android.trackedentity;

import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.common.state.State;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;

public interface TrackedEntityInstanceInteractor {
    TrackedEntityInstance create(OrganisationUnit organisationUnit, TrackedEntity trackedEntity);

    Observable<Boolean> save(TrackedEntityInstance trackedEntityInstance);

    Observable<Boolean> remove(TrackedEntityInstance trackedEntityInstance);

    Observable<TrackedEntityInstance> get(long id);

    Observable<TrackedEntityInstance> get(String uid);

    Observable<State> get(TrackedEntityInstance trackedEntityInstance);

    Observable<Map<Long, State>> map(List<TrackedEntityInstance> trackedEntityInstances);

    Observable<List<TrackedEntityInstance>> list();

    Observable<List<TrackedEntityInstance>> list(OrganisationUnit organisationUnit, Program program);

    Observable<List<TrackedEntityInstance>> listByActions(Set<Action> actionSet);

    Observable<List<TrackedEntityInstance>> pull(Set<String> uids);

    Observable<List<TrackedEntityInstance>> pull(SyncStrategy strategy, Set<String> uids);

    Observable<List<TrackedEntityInstance>> push(Set<String> uids);

    Observable<List<TrackedEntityInstance>> sync(Set<String> uids);

    Observable<List<TrackedEntityInstance>> sync(SyncStrategy strategy, Set<String> uids);
}
