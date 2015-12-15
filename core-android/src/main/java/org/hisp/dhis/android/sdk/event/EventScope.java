package org.hisp.dhis.android.sdk.event;

import org.hisp.dhis.java.sdk.event.IEventController;
import org.hisp.dhis.java.sdk.event.IEventService;
import org.hisp.dhis.java.sdk.models.event.Event;
import org.hisp.dhis.java.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.java.sdk.models.program.Program;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class EventScope implements IEventScope {

    private final IEventService mEventService;
    private final IEventController mEventController;

    public EventScope(IEventService eventService, IEventController eventController) {
        this.mEventService = eventService;
        this.mEventController = eventController;
    }

    @Override
    public Observable<Boolean> save(final Event event) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mEventService.save(event);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final Event event) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mEventService.remove(event);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Event> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<Event>() {
            @Override
            public void call(Subscriber<? super Event> subscriber) {
                try {
                    Event event = mEventService.get(id);
                    subscriber.onNext(event);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Event> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<Event>() {
            @Override
            public void call(Subscriber<? super Event> subscriber) {
                try {
                    Event event = mEventService.get(uid);
                    subscriber.onNext(event);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Event>> list() {
        return Observable.create(new Observable.OnSubscribe<List<Event>>() {
            @Override
            public void call(Subscriber<? super List<Event>> subscriber) {
                try {
                    List<Event> events = mEventService.list();
                    subscriber.onNext(events);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public void send() {
        mEventController.sync();
    }

    @Override
    public void update(OrganisationUnit organisationUnit, Program program, int limit) {
        mEventController.sync(organisationUnit, program, limit);
    }

    @Override
    public void update(OrganisationUnit organisationUnit, Program program) {
        mEventController.sync(organisationUnit, program);
    }
}
