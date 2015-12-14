package org.hisp.dhis.android.sdk.optionset;

import org.hisp.dhis.java.sdk.models.optionset.Option;
import org.hisp.dhis.java.sdk.models.optionset.OptionSet;
import org.hisp.dhis.java.sdk.optionset.OptionSetService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class OptionSetScope implements IOptionSetScope {

    private OptionSetService mOptionSetService;

    public OptionSetScope(OptionSetService optionSetService) {
        this.mOptionSetService = optionSetService;
    }
    @Override
    public Observable<OptionSet> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<OptionSet>() {
            @Override
            public void call(Subscriber<? super OptionSet> subscriber) {
                try {
                    OptionSet optionSet = mOptionSetService.get(uid);
                    subscriber.onNext(optionSet);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<OptionSet> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<OptionSet>() {
            @Override
            public void call(Subscriber<? super OptionSet> subscriber) {
                try {
                    OptionSet optionSet = mOptionSetService.get(id);
                    subscriber.onNext(optionSet);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<OptionSet>> list() {
        return Observable.create(new Observable.OnSubscribe<List<OptionSet>>() {
            @Override
            public void call(Subscriber<? super List<OptionSet>> subscriber) {
                try {
                    List<OptionSet> optionSets = mOptionSetService.list();
                    subscriber.onNext(optionSets);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Option>> list(final OptionSet optionSet) {
        return Observable.create(new Observable.OnSubscribe<List<Option>>() {
            @Override
            public void call(Subscriber<? super List<Option>> subscriber) {
                try {
                    List<Option> options = mOptionSetService.list(optionSet);
                    subscriber.onNext(options);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final OptionSet optionSet) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mOptionSetService.save(optionSet);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final OptionSet optionSet) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mOptionSetService.remove(optionSet);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
