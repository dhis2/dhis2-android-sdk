package org.hisp.dhis.android.sdk.constant;


import org.hisp.dhis.java.sdk.constant.ConstantService;
import org.hisp.dhis.java.sdk.models.constant.Constant;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class ConstantScope implements IConstantScope {

    private ConstantService mConstantService;

    public ConstantScope(ConstantService constantService) {
        this.mConstantService= constantService;
    }
    @Override
    public Observable<Constant> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<Constant>() {
            @Override
            public void call(Subscriber subscriber) {
                try {
                    Constant constant = mConstantService.get(uid);
                    subscriber.onNext(constant);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Constant> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<Constant>() {
            @Override
            public void call(Subscriber subscriber) {
                try {
                    Constant constant = mConstantService.get(id);
                    subscriber.onNext(constant);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Constant>> list() {
        return Observable.create(new Observable.OnSubscribe<List<Constant>>() {
            @Override
            public void call(Subscriber<? super List<Constant>> subscriber) {
                try {
                    List<Constant> constants = mConstantService.list();
                    subscriber.onNext(constants);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final Constant object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mConstantService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final Constant object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mConstantService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }


}
