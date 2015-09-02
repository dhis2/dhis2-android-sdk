package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.IService;

public interface IDashboardItemService extends IService {
    DashboardItem createDashboardItem(Dashboard dashboard, DashboardItemContent content);

    void deleteDashboardItem(DashboardItem dashboardItem);

    int getContentCount(DashboardItem dashboardItem);
}
