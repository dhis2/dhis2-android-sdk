package org.hisp.dhis.client.sdk.android.trackedentity;

import org.apache.commons.lang3.NotImplementedException;
import org.hisp.dhis.client.sdk.android.api.utils.DefaultOnSubscribe;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInstanceController;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.common.state.State;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntity;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;

public class TrackedEntityInstanceInteractorImpl implements TrackedEntityInstanceInteractor {

    private final TrackedEntityInstanceService trackedEntityInstanceService;
    private final TrackedEntityInstanceController trackedEntityInstanceController;

    public TrackedEntityInstanceInteractorImpl(TrackedEntityInstanceService trackedEntityInstanceService,
                                               TrackedEntityInstanceController trackedEntityInstanceController) {
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackedEntityInstanceController = trackedEntityInstanceController;
    }

    @Override
    public TrackedEntityInstance create(OrganisationUnit organisationUnit, TrackedEntity trackedEntity) {
        return trackedEntityInstanceService.create(organisationUnit, trackedEntity);
    }

    @Override
    public Observable<Boolean> save(final TrackedEntityInstance trackedEntityInstance) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return trackedEntityInstanceService.save(trackedEntityInstance);
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final TrackedEntityInstance trackedEntityInstance) {
        return Observable.create(new DefaultOnSubscribe<Boolean>() {
            @Override
            public Boolean call() {
                return trackedEntityInstanceService.remove(trackedEntityInstance);
            }
        });
    }

    @Override
    public Observable<TrackedEntityInstance> get(final long id) {
        return Observable.create(new DefaultOnSubscribe<TrackedEntityInstance>() {
            @Override
            public TrackedEntityInstance call() {
                return trackedEntityInstanceService.get(id);
            }
        });
    }

    @Override
    public Observable<TrackedEntityInstance> get(final String uid) {
        return Observable.create(new DefaultOnSubscribe<TrackedEntityInstance>() {
            @Override
            public TrackedEntityInstance call() {
                return trackedEntityInstanceService.get(uid);
            }
        });
    }

    @Override
    public Observable<State> get(final TrackedEntityInstance trackedEntityInstance) {
        return Observable.create(new DefaultOnSubscribe<State>() {
            @Override
            public State call() {
                throw new NotImplementedException("Not implemented yet");
//                return trackedEntityInstanceService.get(trackedEntityInstance);
            }
        });
    }

    @Override
    public Observable<Map<Long, State>> map(List<TrackedEntityInstance> trackedEntityInstances) {
        return Observable.create(new DefaultOnSubscribe<Map<Long, State>>() {
            @Override
            public Map<Long, State> call() {
                throw new NotImplementedException("Not implemented yet");
//                return trackedEntityInstanceService.map(events);
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityInstance>> list() {
        return Observable.create(new DefaultOnSubscribe<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() {
                return trackedEntityInstanceService.list();
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityInstance>> list(final OrganisationUnit organisationUnit, final Program program) {
        return Observable.create(new DefaultOnSubscribe<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() {
                throw new NotImplementedException("Not implemented yet");
//                return trackedEntityInstanceService.list(organisationUnit, program);
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityInstance>> listByActions(final Set<Action> actionSet) {
        return Observable.create(new DefaultOnSubscribe<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() {
                throw new NotImplementedException("Not implemented yet");
//                return trackedEntityInstanceService.listByActions(actionSet);
            }
        });
    }

    @Override
    public Observable<List<TrackedEntityInstance>> pull(Set<String> uids) {
        throw new NotImplementedException("Not implemented yet");
//        return pull(SyncStrategy.DEFAULT, uids);

    }

    @Override
    public Observable<List<TrackedEntityInstance>> pull(SyncStrategy strategy, Set<String> uids) {
        throw new NotImplementedException("Not implemented yet");
//        return Observable.create(new DefaultOnSubscribe<List<TrackedEntityInstance>>() {
//            @Override
//            public List<TrackedEntityInstance> call() {
//                trackedEntityInstanceController.pull(strategy, uids);
//                return trackedEntityInstanceService.list(uids);
//            }
//        });
    }

    @Override
    public Observable<List<TrackedEntityInstance>> push(final Set<String> uids) {
        throw new NotImplementedException("Not implemented yet");
//        return Observable.create(new DefaultOnSubscribe<List<TrackedEntityInstance>>() {
//            @Override
//            public List<TrackedEntityInstance> call() {
//                trackedEntityInstanceController.push(uids);
//                return trackedEntityInstanceService.list(uids);
//            }
//        });

    }

    @Override
    public Observable<List<TrackedEntityInstance>> sync(Set<String> uids) {
        return sync(SyncStrategy.DEFAULT, uids);
    }
    //TODO Create sync functionality from interactor to controller level
    @Override
    public Observable<List<TrackedEntityInstance>> sync(final SyncStrategy strategy, final Set<String> uids) {
        return Observable.create(new DefaultOnSubscribe<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() {
//                trackedEntityInstanceController.sync(strategy, uids);
//                return trackedEntityInstanceService.list(uids);
                throw new NotImplementedException("Not implemented yet");
            }
        });
    }
}
