package org.hisp.dhis.android.sdk.synchronization.data.trackedentityinstance;


import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.network.response.ImportSummary2;
import org.hisp.dhis.android.sdk.persistence.models.Enrollment;
import org.hisp.dhis.android.sdk.persistence.models.ImportSummary;
import org.hisp.dhis.android.sdk.persistence.models.Relationship;
import org.hisp.dhis.android.sdk.persistence.models.TrackedEntityInstance;
import org.hisp.dhis.android.sdk.synchronization.domain.trackedentityinstance.ITrackedEntityInstanceRepository;
import org.joda.time.DateTime;

import java.util.ArrayList;
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
        DateTime serverDate = mRemoteDataSource.getServerTime();
        Map<String, TrackedEntityInstance> relatedTeis = new HashMap<String,
                TrackedEntityInstance>();
        relatedTeis = getRecursiveRelationatedTeis(trackedEntityInstance, relatedTeis);
        relatedTeis.put(trackedEntityInstance.getUid(), trackedEntityInstance);
        if(relatedTeis.size()>1) {
            pushTeiWithoutRelationFirst(relatedTeis, serverDate);
            trackedEntityInstance.setCreated(serverDate.toString());
            trackedEntityInstance.setCreatedAtClient(serverDate.toString());
            trackedEntityInstance.setFromServer(true);
            return mRemoteDataSource.save(trackedEntityInstance);
        }else {
            return mRemoteDataSource.save(trackedEntityInstance);
        }

    }

    @Override
    public List<ImportSummary2> sync(List<TrackedEntityInstance> relatedTeis) {
        return mRemoteDataSource.save(relatedTeis);
    }


    @Override
    public Map<String, TrackedEntityInstance> getRecursiveRelationatedTeis(
            TrackedEntityInstance trackedEntityInstance,
            Map<String, TrackedEntityInstance> relatedTeiList) {
        if (trackedEntityInstance.getRelationships() != null
                && trackedEntityInstance.getRelationships().size() > 0) {
            for (Relationship relationship : trackedEntityInstance.getRelationships()) {
                if (relationship.getTrackedEntityInstanceB().equals(
                        trackedEntityInstance.getUid())) {
                    String target = relationship.getTrackedEntityInstanceA();
                    relatedTeiList = addRelatedNotPushedTeis(relatedTeiList, target);
                } else if (relationship.getTrackedEntityInstanceA().equals(
                        trackedEntityInstance.getUid())) {
                    String target = relationship.getTrackedEntityInstanceB();
                    relatedTeiList = addRelatedNotPushedTeis(relatedTeiList, target);
                }
            }
        }
        return relatedTeiList;
    }

    @Override
    public List<Enrollment> getEnrollments(long localId) {
       return mLocalDataSource.getEnrollments(localId);
    }

    private Map<String, TrackedEntityInstance> addRelatedNotPushedTeis(
            Map<String, TrackedEntityInstance> relatedTeiList, String target) {
        TrackedEntityInstance relatedTrackedEntityInstance =
                TrackerController.getTrackedEntityInstance(target);
        if (!relatedTrackedEntityInstance.isFromServer()
                && relatedTrackedEntityInstance.getCreated() == null) {
            if (!relatedTeiList.containsKey(relatedTrackedEntityInstance.getUid())) {
                relatedTeiList.put(relatedTrackedEntityInstance.getUid(),
                        relatedTrackedEntityInstance);
                relatedTeiList = getRecursiveRelationatedTeis(relatedTrackedEntityInstance,
                        relatedTeiList);
            }
        }
        return relatedTeiList;
    }



    private void pushTeiWithoutRelationFirst(
            Map<String, TrackedEntityInstance> trackedEntityInstances, DateTime serverDate) {
        List<TrackedEntityInstance> trackerEntityInstancesWithRelations = new ArrayList<>();
        if (trackedEntityInstances.size() > 0) {
            for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances.values()) {
                trackerEntityInstancesWithRelations.add(trackedEntityInstance);
                //set relationships as null
                trackedEntityInstance.setRelationships(new ArrayList<Relationship>());
                mRemoteDataSource.save(trackedEntityInstance);
            }
            for (TrackedEntityInstance trackedEntityInstance :
                    trackerEntityInstancesWithRelations) {
                if (trackedEntityInstance.getRelationships().size() > 0) {
                    trackedEntityInstance.setFromServer(false);
                    mRemoteDataSource.save(trackedEntityInstance);
                    trackedEntityInstance.setCreated(serverDate.toString());
                    trackedEntityInstance.setLastUpdated(serverDate.toString());
                    trackedEntityInstance.save();
                }
            }
        }
    }
}