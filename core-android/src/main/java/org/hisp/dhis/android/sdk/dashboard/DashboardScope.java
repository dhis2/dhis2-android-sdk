package org.hisp.dhis.android.sdk.dashboard;

import org.hisp.dhis.java.sdk.dashboard.IDashboardController;
import org.hisp.dhis.java.sdk.dashboard.IDashboardService;
import org.hisp.dhis.java.sdk.models.dashboard.Dashboard;
import org.hisp.dhis.java.sdk.models.dashboard.DashboardContent;

import java.util.List;

public class DashboardScope implements IDashboardService {
    private final IDashboardService mDashboardService;
    private final IDashboardController mDashboardController;

    public DashboardScope(IDashboardService dashboardService, IDashboardController dashboardController) {
        mDashboardService = dashboardService;
        mDashboardController = dashboardController;
    }

    @Override
    public Dashboard create(String s) {
        return mDashboardService.create(s);
    }

    @Override
    public boolean addContent(Dashboard dashboard, DashboardContent dashboardContent) {
        return mDashboardService.addContent(dashboard, dashboardContent);
    }

    @Override
    public int countItems(Dashboard dashboard) {
        return mDashboardService.countItems(dashboard);
    }

    @Override
    public Dashboard get(long l) {
        return mDashboardService.get(l);
    }

    @Override
    public Dashboard get(String s) {
        return mDashboardService.get(s);
    }

    @Override
    public List<Dashboard> list() {
        return mDashboardService.list();
    }

    @Override
    public boolean remove(Dashboard dashboard) {
        return mDashboardService.remove(dashboard);
    }

    @Override
    public boolean save(Dashboard dashboard) {
        return mDashboardService.save(dashboard);
    }
}
