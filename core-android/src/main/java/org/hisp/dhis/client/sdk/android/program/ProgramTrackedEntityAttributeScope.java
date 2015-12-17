package org.hisp.dhis.client.sdk.android.program;


import org.hisp.dhis.client.sdk.core.program.ProgramTrackedEntityAttributeService;
import org.hisp.dhis.client.sdk.models.program.Program;
import org.hisp.dhis.client.sdk.models.program.ProgramTrackedEntityAttribute;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ProgramTrackedEntityAttributeScope implements IProgramTrackedEntityAttributeScope {
    private ProgramTrackedEntityAttributeService mProgramTrackedEntityAttributeService;

    public ProgramTrackedEntityAttributeScope(ProgramTrackedEntityAttributeService programTrackedEntityAttributeService) {
        mProgramTrackedEntityAttributeService = programTrackedEntityAttributeService;
    }

    @Override
    public Observable<ProgramTrackedEntityAttribute> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<ProgramTrackedEntityAttribute>() {
            @Override
            public void call(Subscriber<? super ProgramTrackedEntityAttribute> subscriber) {
                try {
                    ProgramTrackedEntityAttribute programTrackedEntityAttribute = mProgramTrackedEntityAttributeService.get(id);
                    subscriber.onNext(programTrackedEntityAttribute);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramTrackedEntityAttribute>> list() {
        return Observable.create(new Observable.OnSubscribe<List<ProgramTrackedEntityAttribute>>() {
            @Override
            public void call(Subscriber<? super List<ProgramTrackedEntityAttribute>> subscriber) {
                try {
                    List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = mProgramTrackedEntityAttributeService.list();
                    subscriber.onNext(programTrackedEntityAttributes);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<ProgramTrackedEntityAttribute>> list(final Program program) {
        return Observable.create(new Observable.OnSubscribe<List<ProgramTrackedEntityAttribute>>() {
            @Override
            public void call(Subscriber<? super List<ProgramTrackedEntityAttribute>> subscriber) {
                try {
                    List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes = mProgramTrackedEntityAttributeService.list(program);
                    subscriber.onNext(programTrackedEntityAttributes);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final ProgramTrackedEntityAttribute object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramTrackedEntityAttributeService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final ProgramTrackedEntityAttribute object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mProgramTrackedEntityAttributeService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
