package org.hisp.dhis.android.sdk.enrollment;

import org.hisp.dhis.java.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.java.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.util.List;

import rx.Observable;

public interface IEnrollmentScope {
    Observable<Boolean> save(Enrollment enrollment);

    Observable<Boolean> remove(Enrollment enrollment);

    Observable<Enrollment> create(OrganisationUnit organisationUnit,
                               TrackedEntityInstance trackedEntityInstance,
                               Program program, boolean followUp, DateTime dateOfEnrollment,
                               DateTime dateOfIncident);

    Observable<Enrollment> get(long id);

    Observable<Enrollment> get(String uid);


    Observable<List<Enrollment>> list();

    Observable<List<Enrollment>> list(Program program, TrackedEntityInstance trackedEntityInstance);

    Observable<List<Enrollment>> list(TrackedEntityInstance trackedEntityInstance);

    Observable<List<Enrollment>> list(Program program, OrganisationUnit organisationUnit);


    Observable<Enrollment> getActiveEnrollment(TrackedEntityInstance trackedEntityInstance,
                                            OrganisationUnit organisationUnit, Program program);


    /**
     * Sends all local enrollment changes to server
     */
    Observable<Boolean> send();


}
