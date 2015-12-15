package org.hisp.dhis.android.sdk.program;

import org.hisp.dhis.java.sdk.models.program.Program;
import org.hisp.dhis.java.sdk.models.program.ProgramStage;
import org.hisp.dhis.java.sdk.program.ProgramStageService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ProgramStageScope implements IProgramStageScope {
    private ProgramStageService mProgramStageService;

    public ProgramStageScope(ProgramStageService programStageService) {
        this.mProgramStageService = programStageService;
    }
    @Override
    public Observable<ProgramStage> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<ProgramStage>() {
            @Override
            public void call(Subscriber<? super ProgramStage> subscriber) {
                try {
                    ProgramStage programStage = mProgramStageService.get(uid);
                    subscriber.onNext(programStage);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramStage> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramStage>() {
            @Override
            public void call(Subscriber<? super ProgramStage> subscriber) {
                try {
                    ProgramStage programStage = mProgramStageService.get(id);
                    subscriber.onNext(programStage);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramStage>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStage>>() {
            @Override
            public void call(Subscriber<? super List<ProgramStage>> subscriber) {
                try {
                    List<ProgramStage> programStages = mProgramStageService.list();
                    subscriber.onNext(programStages);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramStage>> list(final Program program) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStage>>() {
            @Override
            public void call(Subscriber<? super List<ProgramStage>> subscriber) {
                try {
                    List<ProgramStage> programStages = mProgramStageService.list(program);
                    subscriber.onNext(programStages);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final ProgramStage object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramStageService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final ProgramStage object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramStageService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
