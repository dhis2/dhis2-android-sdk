/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.android.enrollment;

import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentController;
import org.hisp.dhis.client.sdk.core.enrollment.EnrollmentService;
import org.hisp.dhis.client.sdk.models.enrollment.Enrollment;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;
import org.joda.time.DateTime;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class EnrollmentScopeImpl implements EnrollmentScope {
    private final EnrollmentService mEnrollmentService;
    private final EnrollmentController mEnrollmentController;

    public EnrollmentScopeImpl(EnrollmentService enrollmentService, EnrollmentController
            enrollmentController) {
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
    public Observable<Enrollment> create(final OrganisationUnit organisationUnit, final
    TrackedEntityInstance trackedEntityInstance, final Program program, final boolean followUp,
                                         final DateTime dateOfEnrollment, final DateTime
                                                     dateOfIncident) {
        return Observable.create(new Observable.OnSubscribe<Enrollment>() {
            @Override
            public void call(Subscriber<? super Enrollment> subscriber) {
                try {
                    Enrollment enrollment = mEnrollmentService.create(organisationUnit,
                            trackedEntityInstance, program, followUp, dateOfEnrollment,
                            dateOfIncident);
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
    public Observable<List<Enrollment>> list(final Program program, final TrackedEntityInstance
            trackedEntityInstance) {
        return Observable.create(new Observable.OnSubscribe<List<Enrollment>>() {
            @Override
            public void call(Subscriber<? super List<Enrollment>> subscriber) {
                try {
                    List<Enrollment> enrollments = mEnrollmentService.list(program,
                            trackedEntityInstance);
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
    public Observable<List<Enrollment>> list(final Program program, final OrganisationUnit
            organisationUnit) {
        return Observable.create(new Observable.OnSubscribe<List<Enrollment>>() {
            @Override
            public void call(Subscriber<? super List<Enrollment>> subscriber) {
                try {
                    List<Enrollment> enrollments = mEnrollmentService.list(program,
                            organisationUnit);
                    subscriber.onNext(enrollments);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Enrollment> getActiveEnrollment(final TrackedEntityInstance
                                                                  trackedEntityInstance, final
    OrganisationUnit organisationUnit, final Program program) {
        return Observable.create(new Observable.OnSubscribe<Enrollment>() {
            @Override
            public void call(Subscriber<? super Enrollment> subscriber) {
                try {
                    Enrollment enrollment = mEnrollmentService.getActiveEnrollment
                            (trackedEntityInstance, organisationUnit, program);
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
//                    bool status = mEnrollmentController.pull() subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

}
