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

package org.hisp.dhis.client.sdk.android.program;


import org.hisp.dhis.client.sdk.core.program.IProgramController;
import org.hisp.dhis.client.sdk.core.program.IProgramService;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

public class ProgramScope implements IProgramScope {
    private final IProgramService programService;
    private final IProgramController programController;

    public ProgramScope(IProgramService programService, IProgramController programController) {
        this.programService = programService;
        this.programController = programController;
    }

    @Override
    public Observable<Program> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<Program>() {

            @Override
            public void call(Subscriber<? super Program> subscriber) {
                try {
                    Program program = programService.get(id);
                    subscriber.onNext(program);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Program> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<Program>() {

            @Override
            public void call(Subscriber<? super Program> subscriber) {
                try {
                    Program program = programService.get(uid);
                    subscriber.onNext(program);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Program>> list() {
        return Observable.create(new Observable.OnSubscribe<List<Program>>() {

            @Override
            public void call(Subscriber<? super List<Program>> subscriber) {
                try {
                    List<Program> programs = programService.list();
                    subscriber.onNext(programs);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Program>> list(final OrganisationUnit... organisationUnits) {
        return Observable.create(new Observable.OnSubscribe<List<Program>>() {

            @Override
            public void call(Subscriber<? super List<Program>> subscriber) {
                try {
                    List<Program> programs = programService.list(organisationUnits);
                    subscriber.onNext(programs);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Program>> sync() {
        return Observable.create(new Observable.OnSubscribe<List<Program>>() {

            @Override
            public void call(Subscriber<? super List<Program>> subscriber) {
                try {
                    programController.sync();
                    subscriber.onNext(programService.list());
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    // TODO think about adding method to service layer
    // which will get list of uids in and return set of models back
    @Override
    public Observable<List<Program>> sync(final String... uids) {
        throw new UnsupportedOperationException("This method is not implemented at the moment");
    }
}
