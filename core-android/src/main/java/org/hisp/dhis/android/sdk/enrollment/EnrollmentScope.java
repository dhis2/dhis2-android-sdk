package org.hisp.dhis.android.sdk.enrollment;

import org.hisp.dhis.java.sdk.enrollment.EnrollmentController;
import org.hisp.dhis.java.sdk.enrollment.EnrollmentService;
import org.hisp.dhis.java.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.java.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.models.trackedentity.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class EnrollmentScope implements IEnrollmentScope {
    private final EnrollmentService mEnrollmentService;
    private final EnrollmentController mEnrollmentController;

    public EnrollmentScope(EnrollmentService enrollmentService, EnrollmentController enrollmentController) {
        this.mEnrollmentService = enrollmentService;
        this.mEnrollmentController = enrollmentController;
    }

    @Override
    public Observable<Boolean> save(final Enrollment enrollment) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mEnrollmentService.save(enrollment);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final Enrollment enrollment) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mEnrollmentService.remove(enrollment);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Enrollment> create(final OrganisationUnit organisationUnit, final TrackedEntityInstance trackedEntityInstance, final Program program, final boolean followUp, final DateTime dateOfEnrollment, final DateTime dateOfIncident) {
        return Observable.create(new Observable.OnSubscribe<Enrollment>() {
            @Override
            public void call(Subscriber<? super Enrollment> subscriber) {
                try {
                    Enrollment enrollment = mEnrollmentService.create(organisationUnit, trackedEntityInstance, program, followUp, dateOfEnrollment, dateOfIncident);
                    subscriber.onNext(enrollment);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Enrollment> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<Enrollment>() {
            @Override
            public void call(Subscriber<? super Enrollment> subscriber) {
                try {
                    Enrollment enrollment = mEnrollmentService.get(id);
                    subscriber.onNext(enrollment);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Enrollment> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<Enrollment>() {
            @Override
            public void call(Subscriber<? super Enrollment> subscriber) {
                try {
                    Enrollment enrollment = mEnrollmentService.get(uid);
                    subscriber.onNext(enrollment);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Enrollment>> list() {
        return Observable.create(new Observable.OnSubscribe<List<Enrollment>>() {
            @Override
            public void call(Subscriber<? super List<Enrollment>> subscriber) {
                try {
                    List<Enrollment> enrollments = mEnrollmentService.list();
                    subscriber.onNext(enrollments);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Enrollment>> list(final Program program, final TrackedEntityInstance trackedEntityInstance) {
        return Observable.create(new Observable.OnSubscribe<List<Enrollment>>() {
            @Override
            public void call(Subscriber<? super List<Enrollment>> subscriber) {
                try {
                    List<Enrollment> enrollments = mEnrollmentService.list(program, trackedEntityInstance);
                    subscriber.onNext(enrollments);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Enrollment>> list(final TrackedEntityInstance trackedEntityInstance) {
        return Observable.create(new Observable.OnSubscribe<List<Enrollment>>() {
            @Override
            public void call(Subscriber<? super List<Enrollment>> subscriber) {
                try {
                    List<Enrollment> enrollments = mEnrollmentService.list(trackedEntityInstance);
                    subscriber.onNext(enrollments);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Enrollment>> list(final Program program, final OrganisationUnit organisationUnit) {
        return Observable.create(new Observable.OnSubscribe<List<Enrollment>>() {
            @Override
            public void call(Subscriber<? super List<Enrollment>> subscriber) {
                try {
                    List<Enrollment> enrollments = mEnrollmentService.list(program, organisationUnit);
                    subscriber.onNext(enrollments);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Enrollment> getActiveEnrollment(final TrackedEntityInstance trackedEntityInstance, final OrganisationUnit organisationUnit, final Program program) {
        return Observable.create(new Observable.OnSubscribe<Enrollment>() {
            @Override
            public void call(Subscriber<? super Enrollment> subscriber) {
                try {
                    Enrollment enrollment = mEnrollmentService.getActiveEnrollment(trackedEntityInstance, organisationUnit, program);
                    subscriber.onNext(enrollment);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> send() {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    mEnrollmentController.sync();
//                    bool status = mEnrollmentController.sync() subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

}
