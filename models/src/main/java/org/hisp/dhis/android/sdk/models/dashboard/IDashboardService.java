package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.IService;

public interface IDashboardService extends IService {
    Dashboard createDashboard(String name);

    void updateDashboardName(Dashboard dashboard, String name);

    void deleteDashboard(Dashboard dashboard);

    boolean addDashboardContent(Dashboard dashboard, DashboardItemContent content);

    DashboardItem getAvailableItemByType(Dashboard dashboard, String type);
}
