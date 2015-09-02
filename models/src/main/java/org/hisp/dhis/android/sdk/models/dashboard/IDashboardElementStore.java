package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.common.meta.State;

import java.util.List;

public interface IDashboardElementStore extends IStore<DashboardElement> {
    List<DashboardElement> query(DashboardItem dashboardItem, State... states);

    List<DashboardElement> query(DashboardItem dashboardItem, List<State> states);

    List<DashboardElement> filter(DashboardItem dashboardItem, State state);

    List<DashboardElement> filter(State state);
}
