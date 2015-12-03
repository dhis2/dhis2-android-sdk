package org.hisp.dhis.android.sdk.dashboard;

import org.hisp.dhis.java.sdk.dashboard.IDashboardController;
import org.hisp.dhis.java.sdk.dashboard.IDashboardService;
import org.hisp.dhis.java.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.java.sdk.models.dashboard.DashboardContent;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class DashboardScope implements IDashboardScope {
    private final IDashboardService mDashboardService;
    private final IDashboardController mDashboardController;

    public DashboardScope(IDashboardService dashboardService, IDashboardController dashboardController) {
        mDashboardService = dashboardService;
        mDashboardController = dashboardController;
    }

    @Override
    public Observable<Boolean> save(final Dashboard dashboard) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mDashboardService.save(dashboard);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final Dashboard dashboard) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mDashboardService.remove(dashboard);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Dashboard> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<Dashboard>() {
            @Override
            public void call(Subscriber<? super Dashboard> subscriber) {
                try {
                    Dashboard dashboard = mDashboardService.get(id);
                    subscriber.onNext(dashboard);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Dashboard> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<Dashboard>() {
            @Override
            public void call(Subscriber<? super Dashboard> subscriber) {
                try {
                    Dashboard dashboard = mDashboardService.get(uid);
                    subscriber.onNext(dashboard);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<Dashboard>> list() {
        return Observable.create(new Observable.OnSubscribe<List<Dashboard>>() {
            @Override
            public void call(Subscriber<? super List<Dashboard>> subscriber) {
                try {
                    List<Dashboard> dashboards = mDashboardService.list();
                    subscriber.onNext(dashboards);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Integer> countItems(final Dashboard dashboard) {
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    int count = mDashboardService.countItems(dashboard);
                    subscriber.onNext(count);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> addContent(final Dashboard dashboard, final DashboardContent content) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mDashboardService.addContent(dashboard, content);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
