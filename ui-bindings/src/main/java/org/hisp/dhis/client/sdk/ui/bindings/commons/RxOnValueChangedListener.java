package org.hisp.dhis.client.sdk.ui.bindings.commons;

import org.hisp.dhis.client.sdk.ui.models.FormEntity;
import org.hisp.dhis.client.sdk.ui.models.OnFormEntityChangeListener;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

public class RxOnValueChangedListener
        implements Observable.OnSubscribe<FormEntity>, OnFormEntityChangeListener {
    private OnFormEntityChangeListener onFormEntityChangedListener;

    public RxOnValueChangedListener() {
        // explicit empty constructor
    }

    @Override
    public void call(final Subscriber<? super FormEntity> subscriber) {
        onFormEntityChangedListener = new OnFormEntityChangeListener() {

            @Override
            public void onFormEntityChanged(FormEntity formEntity) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(formEntity);
                }
            }
        };

        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                // removing reference to listener
                // in order not to leak anything
                onFormEntityChangedListener = null;
            }
        });
    }

    @Override
    public void onFormEntityChanged(FormEntity formEntity) {
        if (onFormEntityChangedListener != null) {
            onFormEntityChangedListener.onFormEntityChanged(formEntity);
        }
    }
}
