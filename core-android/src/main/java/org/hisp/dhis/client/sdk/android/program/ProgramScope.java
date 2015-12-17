package org.hisp.dhis.client.sdk.android.program;


import org.hisp.dhis.client.sdk.core.program.IProgramService;
import org.hisp.dhis.client.sdk.models.program.Program;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ProgramScope implements IProgramScope {

    private final IProgramService mProgramService;

    public ProgramScope(IProgramService mProgramService) {
        this.mProgramService = mProgramService;
    }

    @Override
    public Observable<Boolean> save(final Program program) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramService.save(program);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final Program program) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramService.remove(program);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Program> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<Program>() {
            @Override
            public void call(Subscriber<? super Program> subscriber) {
                try {
                    Program program = mProgramService.get(id);
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
                    Program program = mProgramService.get(uid);
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
                    List<Program> programs = mProgramService.list();
                    subscriber.onNext(programs);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
