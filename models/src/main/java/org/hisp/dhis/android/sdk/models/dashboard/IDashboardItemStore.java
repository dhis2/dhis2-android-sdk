package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.common.meta.State;

import java.util.List;

public interface IDashboardItemStore extends IStore<DashboardItem> {
    List<DashboardItem> query(State... states);

    List<DashboardItem> query(List<State> states);

    List<DashboardItem> query(Dashboard dashboard, List<State> states);

    List<DashboardItem> filter(State state);

    List<DashboardItem> filter(Dashboard dashboard, State state);

    List<DashboardItem> filter(Dashboard dashboard, State state, String type);
}
