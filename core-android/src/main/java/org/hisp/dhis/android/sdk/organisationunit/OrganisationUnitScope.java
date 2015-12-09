package org.hisp.dhis.android.sdk.organisationunit;

import org.hisp.dhis.java.sdk.models.organisationunit.OrganisationUnit;
import org.hisp.dhis.java.sdk.organisationunit.IOrganisationUnitService;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class OrganisationUnitScope implements IOrganisationUnitScope {

    private final IOrganisationUnitService mOrganisationUnitService;

    public OrganisationUnitScope(IOrganisationUnitService mOrganisationUnitService) {
        this.mOrganisationUnitService = mOrganisationUnitService;
    }

    @Override
    public Observable<Boolean> save(final OrganisationUnit organisationUnit) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mOrganisationUnitService.save(organisationUnit);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final OrganisationUnit organisationUnit) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mOrganisationUnitService.remove(organisationUnit);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<OrganisationUnit> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<OrganisationUnit>() {
            @Override
            public void call(Subscriber<? super OrganisationUnit> subscriber) {
                try {
                    OrganisationUnit organisationUnit = mOrganisationUnitService.get(id);
                    subscriber.onNext(organisationUnit);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<OrganisationUnit> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<OrganisationUnit>() {
            @Override
            public void call(Subscriber<? super OrganisationUnit> subscriber) {
                try {
                    OrganisationUnit organisationUnit = mOrganisationUnitService.get(uid);
                    subscriber.onNext(organisationUnit);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<OrganisationUnit>> list() {
        return Observable.create(new Observable.OnSubscribe<List<OrganisationUnit>>() {
            @Override
            public void call(Subscriber<? super List<OrganisationUnit>> subscriber) {
                try {
                    List<OrganisationUnit> organisationUnits = mOrganisationUnitService.list();
                    subscriber.onNext(organisationUnits);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
