package org.hisp.dhis.android.sdk.models.dashboard;


import org.hisp.dhis.android.sdk.models.common.IService;

public interface IDashboardElementService extends IService {
    DashboardElement createDashboardElement(DashboardItem item, DashboardItemContent content);

    void deleteDashboardElement(DashboardItem dashboardItem, DashboardElement dashboardElement);
}
