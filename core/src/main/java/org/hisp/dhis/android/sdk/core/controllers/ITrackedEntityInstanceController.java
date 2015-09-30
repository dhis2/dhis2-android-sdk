package org.hisp.dhis.android.sdk.core.controllers;

import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.android.sdk.models.trackedentityattributevalue.TrackedEntityAttributeValue;
import org.hisp.dhis.android.sdk.models.trackedentityinstance.TrackedEntityInstance;

import java.util.List;

public interface ITrackedEntityInstanceController {
    public void sync() throws APIException;
    public TrackedEntityInstance sync(String uid, boolean getEnrollments) throws APIException;
    public void sync(List<TrackedEntityInstance> trackedEntityInstances, boolean getEnrollments) throws APIException;
    public List<TrackedEntityInstance> queryServerTrackedEntityInstances(String organisationUnitUid,
                                                                         String programUid,
                                                                         String queryString,
                                                                         TrackedEntityAttributeValue... params) throws APIException;
}
