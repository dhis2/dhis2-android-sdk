package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.IStore;

import java.util.List;

public interface IDashboardItemContentStore extends IStore<DashboardItemContent> {
    List<DashboardItemContent> query(List<String> type);
}