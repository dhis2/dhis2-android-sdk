package org.hisp.dhis.android.sdk.program;

import org.hisp.dhis.java.sdk.models.dataelement.DataElement;
import org.hisp.dhis.java.sdk.models.program.ProgramStage;
import org.hisp.dhis.java.sdk.models.program.ProgramStageDataElement;
import org.hisp.dhis.java.sdk.models.program.ProgramStageSection;
import org.hisp.dhis.java.sdk.program.ProgramStageDataElementService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ProgramStageDataElementScope implements IProgramStageDataElementScope {

    private ProgramStageDataElementService mProgramStageDataElementService;
    
    public ProgramStageDataElementScope(ProgramStageDataElementService programStageDataElementService) {
        this.mProgramStageDataElementService = programStageDataElementService;
    }

    @Override
    public Observable<ProgramStageDataElement> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramStageDataElement>() {
            @Override
            public void call(Subscriber<? super ProgramStageDataElement> subscriber) {
                try {
                    ProgramStageDataElement programStageDataElement = mProgramStageDataElementService.get(id);
                    subscriber.onNext(programStageDataElement);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramStageDataElement>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStageDataElement>>() {
            @Override
            public void call(Subscriber<? super List<ProgramStageDataElement>> subscriber) {
                try {
                    List<ProgramStageDataElement> programRuleActions = mProgramStageDataElementService.list();
                    subscriber.onNext(programRuleActions);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramStageDataElement>> getProgramStageDataElements(final ProgramStage programStage) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStageDataElement>>() {
            @Override
            public void call(Subscriber<? super List<ProgramStageDataElement>> subscriber) {
                try {
                    List<ProgramStageDataElement> programRuleActions = mProgramStageDataElementService.query(programStage);
                    subscriber.onNext(programRuleActions);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<ProgramStageDataElement> getProgramStageDataElements(final ProgramStage programStage, final DataElement dataElement) {
        return Observable.create(new Observable.OnSubscribe<ProgramStageDataElement>() {
            @Override
            public void call(Subscriber<? super ProgramStageDataElement> subscriber) {
                try {
                    ProgramStageDataElement programStageDataElement = mProgramStageDataElementService.query(programStage, dataElement);
                    subscriber.onNext(programStageDataElement);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramStageDataElement>> getProgramStageDataElements(final ProgramStageSection programStageSection) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramStageDataElement>>() {
            @Override
            public void call(Subscriber<? super List<ProgramStageDataElement>> subscriber) {
                try {
                    List<ProgramStageDataElement> programRuleActions = mProgramStageDataElementService.query(programStageSection);
                    subscriber.onNext(programRuleActions);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final ProgramStageDataElement object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramStageDataElementService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final ProgramStageDataElement object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramStageDataElementService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
