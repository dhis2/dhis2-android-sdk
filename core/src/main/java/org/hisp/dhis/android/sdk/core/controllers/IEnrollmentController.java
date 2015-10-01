package org.hisp.dhis.android.sdk.core.controllers;

import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.android.sdk.models.trackedentityinstance.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.util.List;

public interface IEnrollmentController {
    public void sync() throws APIException;
    public Enrollment sync(String uid, boolean getEvents) throws APIException;
    public List<Enrollment> sync(TrackedEntityInstance trackedEntityInstance) throws APIException;
    void sendEnrollmentChanges(List<Enrollment> enrollments, boolean sendEvents) throws APIException;
}
