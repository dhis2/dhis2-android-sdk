package org.hisp.dhis.client.sdk.android.dataelement;


import org.hisp.dhis.client.sdk.core.dataelement.DataElementService;
import org.hisp.dhis.client.sdk.models.dataelement.DataElement;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class DataElementScope implements IDataElementScope {
    private final DataElementService mDataElementService;

    public DataElementScope(DataElementService dataElementService) {
        this.mDataElementService = dataElementService;
    }

    @Override
    public Observable<DataElement> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<DataElement>() {
            @Override
            public void call(Subscriber subscriber) {
                try {
                    DataElement dataElement = mDataElementService.get(uid);
                    subscriber.onNext(dataElement);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<DataElement> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<DataElement>() {
            @Override
            public void call(Subscriber subscriber) {
                try {
                    DataElement dataElement = mDataElementService.get(id);
                    subscriber.onNext(dataElement);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<DataElement>> list() {
        return Observable.create(new Observable.OnSubscribe<List<DataElement>>() {
            @Override
            public void call(Subscriber<? super List<DataElement>> subscriber) {
                try {
                    List<DataElement> dataElements = mDataElementService.list();
                    subscriber.onNext(dataElements);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final DataElement object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mDataElementService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final DataElement object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mDataElementService.remove(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
