package org.hisp.dhis.android.sdk.core.controllers;

import org.hisp.dhis.android.sdk.core.network.APIException;
import org.hisp.dhis.android.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.android.sdk.models.event.Event;
import org.joda.time.DateTime;

import java.util.List;

public interface IEventController {
    public void sync() throws APIException;
    public void sync(String organisationUnitUid, String programUid, DateTime serverDateTime) throws APIException;
    public void sync(Enrollment enrollment) throws APIException;
    public void getEventsDataFromServer(Enrollment enrollment) throws APIException;
    public void sendEventChanges(List<Event> events) throws APIException;
}
