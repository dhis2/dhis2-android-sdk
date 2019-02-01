package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.responses.HttpMessageResponse;
import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleaner;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import java.net.HttpRetryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Call;

@Reusable
public final class TrackedEntityInstanceListDownloadAndPersistCallFactory {

    private final ForeignKeyCleaner foreignKeyCleaner;
    private final TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory
            relationshipsCallFactory;
    private final TrackedEntityInstancePersistenceCallFactory persistenceCallFactory;
    private final D2CallExecutor d2CallExecutor;
    private final APICallExecutor apiCallExecutor;
    private final DHISVersionManager versionManager;
    private final TrackedEntityInstanceService trackedEntityInstanceService;


    @Inject
    TrackedEntityInstanceListDownloadAndPersistCallFactory(
            ForeignKeyCleaner foreignKeyCleaner,
            TrackedEntityInstanceRelationshipDownloadAndPersistCallFactory relationshipsCallFactory,
            TrackedEntityInstancePersistenceCallFactory persistenceCallFactory,
            D2CallExecutor d2CallExecutor,
            APICallExecutor apiCallExecutor,
            DHISVersionManager versionManager,
            TrackedEntityInstanceService trackedEntityInstanceService) {
        this.foreignKeyCleaner = foreignKeyCleaner;
        this.relationshipsCallFactory = relationshipsCallFactory;
        this.persistenceCallFactory = persistenceCallFactory;
        this.d2CallExecutor = d2CallExecutor;
        this.apiCallExecutor = apiCallExecutor;
        this.versionManager = versionManager;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
    }

    public Callable<List<TrackedEntityInstance>> getCall(final Collection<String> trackedEntityInstanceUids) {
        return new Callable<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() throws Exception {
                return downloadAndPersist(trackedEntityInstanceUids, null);
            }
        };
    }

    public Callable<List<TrackedEntityInstance>> getCall(final Collection<String> trackedEntityInstanceUids,
                                                         final String program) {
        return new Callable<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() throws Exception {
                return downloadAndPersist(trackedEntityInstanceUids, program);
            }
        };
    }

    private List<TrackedEntityInstance> downloadAndPersist(final Collection<String> trackedEntityInstanceUids,
                                                           final String program) throws D2Error {

        return d2CallExecutor.executeD2CallTransactionally(new Callable<List<TrackedEntityInstance>>() {
            @Override
            public List<TrackedEntityInstance> call() throws Exception {
                if (trackedEntityInstanceUids == null) {
                    throw new IllegalArgumentException("UID list is null");
                }

                List<TrackedEntityInstance> teis = new ArrayList<>();

                for (String uid : trackedEntityInstanceUids) {
                    List<TrackedEntityInstance> teiList;
                    if (program == null) {
                        Call<Payload<TrackedEntityInstance>> teiCall =
                                trackedEntityInstanceService.getTrackedEntityInstance(uid,
                                        TrackedEntityInstanceFields.allFields, true);
                        teiList = apiCallExecutor.executePayloadCall(teiCall);
                    } else {
                        teiList = downloadGlassAware(uid, program);
                    }

                    teis.addAll(teiList);
                }

                d2CallExecutor.executeD2Call(persistenceCallFactory.getCall(teis));

                if (!versionManager.is2_29()) {
                    d2CallExecutor.executeD2Call(relationshipsCallFactory.getCall());
                }

                foreignKeyCleaner.cleanForeignKeyErrors();

                return teis;
            }
        });
    }

    private List<TrackedEntityInstance> downloadGlassAware(String uid, String program) throws D2Error {
        try {
            TrackedEntityInstance tei = apiCallExecutor.executeObjectCallWithErrorCatcher(getTeiByProgram(uid, program),
                    new TrackedEntityInstanceCallErrorCatcher());

            return Collections.singletonList(tei);
        }
        catch (D2Error d2Error) {
            if (!d2Error.errorCode().equals(D2ErrorCode.OWNERSHIP_ACCESS_DENIED)) {
                return Collections.emptyList();
            }

            HttpMessageResponse breakGlassResponse = apiCallExecutor.executeObjectCall(
                    trackedEntityInstanceService.breakGlass(uid, program, "Android sync download"));

            if (!breakGlassResponse.httpStatusCode().equals(200)) {
                return Collections.emptyList();
            }

            return Collections.singletonList(apiCallExecutor.executeObjectCall(getTeiByProgram(uid, program)));
        }
    }

    private Call<TrackedEntityInstance> getTeiByProgram(String uid, String program) {
        return trackedEntityInstanceService.getTrackedEntityInstanceByProgram(uid, program,
                TrackedEntityInstanceFields.allFields, true);
    }
}
