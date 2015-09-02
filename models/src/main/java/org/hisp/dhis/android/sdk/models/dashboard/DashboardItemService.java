package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.Access;
import org.hisp.dhis.android.sdk.models.common.meta.State;
import org.joda.time.DateTime;

import java.util.List;

public final class DashboardItemService implements IDashboardItemService {
    private final IDashboardItemStore dashboardItemStore;
    private final IDashboardElementStore dashboardElementStore;

    public DashboardItemService(IDashboardItemStore dashboardItemStore,
                                IDashboardElementStore dashboardElementStore) {
        this.dashboardItemStore = dashboardItemStore;
        this.dashboardElementStore = dashboardElementStore;
    }

    /**
     * Factory method which creates and returns DashboardItem.
     *
     * @param dashboard Dashboard to associate with item.
     * @param content   Content for dashboard item.
     * @return new item.
     */
    @Override
    public DashboardItem createDashboardItem(Dashboard dashboard, DashboardItemContent content) {
        /* DateTime lastUpdatedDateTime = DateTimeManager.getInstance()
                .getLastUpdated(ResourceType.DASHBOARDS); */
        DateTime lastUpdated = new DateTime();

        DashboardItem item = new DashboardItem();
        item.setCreated(lastUpdated);
        item.setLastUpdated(lastUpdated);
        item.setState(State.TO_POST);
        item.setDashboard(dashboard);
        item.setAccess(Access.provideDefaultAccess());
        item.setType(content.getType());

        return item;
    }

    /**
     * This method will change the state of the model to TO_DELETE
     * if the model was already synced to the server.
     * <p/>
     * If model was created only locally, it will delete it
     * from embedded database.
     */
    @Override
    public void deleteDashboardItem(DashboardItem dashboardItem) {
        if (dashboardItem.getState() == State.TO_POST) {
            dashboardItemStore.delete(dashboardItem);
        } else {
            dashboardItem.setState(State.TO_DELETE);
            dashboardItemStore.update(dashboardItem);
        }
    }

    @Override
    public int getContentCount(DashboardItem dashboardItem) {
        List<DashboardElement> dashboardElements = dashboardElementStore
                .filter(dashboardItem, State.TO_DELETE);
        return dashboardElements == null ? 0 : dashboardElements.size();
    }
}
