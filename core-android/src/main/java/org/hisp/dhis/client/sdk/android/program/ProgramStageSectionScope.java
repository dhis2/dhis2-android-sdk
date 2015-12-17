package org.hisp.dhis.client.sdk.android.program;


import org.hisp.dhis.client.sdk.core.program.ProgramStageSectionService;
import org.hisp.dhis.client.sdk.models.program.ProgramStage;
import org.hisp.dhis.client.sdk.models.program.ProgramStageSection;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ProgramStageSectionScope implements IProgramStageSectionScope {

    private ProgramStageSectionService mProgramStageSectionService;

    public ProgramStageSectionScope(ProgramStageSectionService programStageSectionService) {
        this.mProgramStageSectionService = programStageSectionService;
    }
    @Override
    public Observable<ProgramStageSection> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<ProgramStageSection>() {
            @Override
            public void call(Subscriber<? super ProgramStageSection> subscriber) {
                try {
                    ProgramStageSection programStageSection = mProgramStageSectionService.get(uid);
                    subscriber.onNext(programStageSection);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramStageSection> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramStageSection>() {
            @Override
            public void call(Subscriber<? super ProgramStageSection> subscriber) {
                try {
                    ProgramStageSection programStageSection  = mProgramStageSectionService.get(id);
                    subscriber.onNext(programStageSection);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramStageSection>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStageSection>>() {
            @Override
            public void call(Subscriber<? super List<ProgramStageSection>> subscriber) {
                try {
                    List<ProgramStageSection> programStageSections = mProgramStageSectionService.list();
                    subscriber.onNext(programStageSections);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramStageSection>> list(final ProgramStage programStage) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStageSection>>() {
            @Override
            public void call(Subscriber<? super List<ProgramStageSection>> subscriber) {
                try {
                    List<ProgramStageSection> programStageSections = mProgramStageSectionService.list(programStage);
                    subscriber.onNext(programStageSections);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final ProgramStageSection object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramStageSectionService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final ProgramStageSection object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramStageSectionService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
