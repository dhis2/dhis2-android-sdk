package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.meta.State;

public final class DashboardElementService implements IDashboardElementService {
    private final IDashboardElementStore dashboardElementStore;
    private final IDashboardItemService dashboardItemService;

    public DashboardElementService(IDashboardElementStore dashboardElementStore,
                                   IDashboardItemService dashboardItemService) {
        this.dashboardElementStore = dashboardElementStore;
        this.dashboardItemService = dashboardItemService;
    }

    /**
     * Factory method for creating DashboardElement.
     *
     * @param item    DashboardItem to associate with element.
     * @param content Content from which element will be created.
     * @return new element.
     */
    @Override
    public DashboardElement createDashboardElement(DashboardItem item, DashboardItemContent content) {
        DashboardElement element = new DashboardElement();
        element.setUId(content.getUId());
        element.setName(content.getName());
        element.setCreated(content.getCreated());
        element.setLastUpdated(content.getLastUpdated());
        element.setDisplayName(content.getDisplayName());
        element.setState(State.TO_POST);
        element.setDashboardItem(item);

        return element;
    }

    @Override
    public void deleteDashboardElement(DashboardItem dashboardItem, DashboardElement dashboardElement) {
        if (State.TO_POST.equals(dashboardElement.getState())) {
            dashboardElementStore.delete(dashboardElement);
        } else {
            dashboardElement.setState(State.TO_DELETE);
            dashboardElementStore.update(dashboardElement);
        }

        /* if count of elements in item is zero, it means
        we don't need this item anymore */
        if (!(dashboardItemService.getContentCount(dashboardItem) > 0)) {
            dashboardItemService.deleteDashboardItem(dashboardItem);
        }
    }
}
