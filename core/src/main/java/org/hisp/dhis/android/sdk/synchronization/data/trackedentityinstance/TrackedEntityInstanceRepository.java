package org.hisp.dhis.android.sdk.synchronization.data.trackedentityinstance;

import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.network.response.ImportSummary2;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance.ITrackedEntityInstanceRepository;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackedEntityInstanceRepository  implements ITrackedEntityInstanceRepository {
    TrackedEntityInstanceLocalDataSource mLocalDataSource;
    TrackedEntityInstanceRemoteDataSource mRemoteDataSource;

    public TrackedEntityInstanceRepository(
            TrackedEntityInstanceLocalDataSource localDataSource,
            TrackedEntityInstanceRemoteDataSource remoteDataSource) {
        mLocalDataSource = localDataSource;
        mRemoteDataSource = remoteDataSource;
    }
    @Override
    public void save(TrackedEntityInstance trackedEntityInstance) {
        mLocalDataSource.save(trackedEntityInstance);
    }

    @Override
    public ImportSummary sync(TrackedEntityInstance trackedEntityInstance) {
        ImportSummary importSummary = mRemoteDataSource.save(trackedEntityInstance);

        if (importSummary.isSuccessOrOK()) {
            updateTrackedEntityInstanceTimestamp(trackedEntityInstance);
        }

        return importSummary;
    }

    @Override
    public List<ImportSummary2> sync(List<TrackedEntityInstance> trackedEntityInstanceList) {

        List<ImportSummary2> importSummaries = mRemoteDataSource.save(trackedEntityInstanceList);

        Map<String, TrackedEntityInstance> trackedEntityInstanceMap =
                TrackedEntityInstance.toMap(trackedEntityInstanceList);

        if (importSummaries != null) {
            DateTime dateTime = mRemoteDataSource.getServerTime();
            for (ImportSummary2 importSummary2 : importSummaries) {
                if (importSummary2.isSuccessOrOK()) {
                    System.out.println("IMPORT SUMMARY(teibatch): " + importSummary2.getDescription() + importSummary2.getHref() +" "+ importSummary2.getReference());
                    TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceMap.get(importSummary2.getReference());
                    if (trackedEntityInstance != null) {
                        updateTrackedEntityInstanceTimestamp(trackedEntityInstance, dateTime.toString(), dateTime.toString());
                    }
                }
            }
        }
        return importSummaries;
    }

    private void updateTrackedEntityInstanceTimestamp(TrackedEntityInstance trackedEntityInstance) {
        TrackedEntityInstance remoteTrackedEntityInstance = mRemoteDataSource.getTrackedEntityInstance(trackedEntityInstance.getTrackedEntityInstance());
        if(trackedEntityInstance.getRelationships()!=null && trackedEntityInstance.getRelationships().size()==0){
            //Restore relations before save.
            trackedEntityInstance.setRelationships(null);
            trackedEntityInstance.getRelationships();
        }
        updateTrackedEntityInstanceTimestamp(trackedEntityInstance, remoteTrackedEntityInstance.getCreated(), remoteTrackedEntityInstance.getLastUpdated());
    }

    private void updateTrackedEntityInstanceTimestamp(TrackedEntityInstance trackedEntityInstance, String createdDate, String lastUpdated) {
        trackedEntityInstance.setCreated(createdDate);
        trackedEntityInstance.setLastUpdated(lastUpdated);

        mLocalDataSource.save(trackedEntityInstance);
    }

    @Override
    public TrackedEntityInstance getTrackedEntityInstance(String trackedEntityInstanceUid) {
        return mLocalDataSource.getTrackedEntityInstance(trackedEntityInstanceUid);
    }

    @Override
    public List<TrackedEntityInstance> getAllLocalTeis() {
        return mLocalDataSource.getAllLocalTeis();
    }
}