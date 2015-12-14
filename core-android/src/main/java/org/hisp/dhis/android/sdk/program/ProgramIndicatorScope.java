package org.hisp.dhis.android.sdk.program;

import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.models.program.ProgramIndicator;
import org.hisp.dhis.java.sdk.program.ProgramIndicatorService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ProgramIndicatorScope implements IProgramIndicatorScope {

    private ProgramIndicatorService mProgramIndicatorService;

    public ProgramIndicatorScope(ProgramIndicatorService mProgramIndicatorService) {
        this.mProgramIndicatorService = mProgramIndicatorService;
    }

    @Override
    public Observable<ProgramIndicator> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<ProgramIndicator>() {
            @Override
            public void call(Subscriber<? super ProgramIndicator> subscriber) {
                try {
                    ProgramIndicator programIndicator = mProgramIndicatorService.get(uid);
                    subscriber.onNext(programIndicator);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramIndicator> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramIndicator>() {
            @Override
            public void call(Subscriber<? super ProgramIndicator> subscriber) {
                try {
                    ProgramIndicator programIndicator = mProgramIndicatorService.get(id);
                    subscriber.onNext(programIndicator);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramIndicator>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramIndicator>>() {
            @Override
            public void call(Subscriber<? super List<ProgramIndicator>> subscriber) {
                try {
                    List<ProgramIndicator> programIndicators = mProgramIndicatorService.list();
                    subscriber.onNext(programIndicators);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramIndicator>> list(final Program program) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramIndicator>>() {
            @Override
            public void call(Subscriber<? super List<ProgramIndicator>> subscriber) {
                try {
                    List<ProgramIndicator> programIndicators = mProgramIndicatorService.query(program);
                    subscriber.onNext(programIndicators);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final ProgramIndicator object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramIndicatorService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final ProgramIndicator object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramIndicatorService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
