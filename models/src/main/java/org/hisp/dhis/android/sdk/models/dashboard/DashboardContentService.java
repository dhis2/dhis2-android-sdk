package org.hisp.dhis.android.sdk.models.dashboard;

import java.util.List;

public class DashboardContentService implements IDashboardItemContentService {
    private final IDashboardItemContentStore mDashboardItemContentStore;

    public DashboardContentService(IDashboardItemContentStore mDashboardItemContentStore) {
        this.mDashboardItemContentStore = mDashboardItemContentStore;
    }

    @Override
    public DashboardItemContent get(long id) {
        return mDashboardItemContentStore.queryById(id);
    }

    @Override
    public DashboardItemContent get(String uid) {
        return mDashboardItemContentStore.queryByUid(uid);
    }

    @Override
    public List<DashboardItemContent> list() {
        return mDashboardItemContentStore.queryAll();
    }

    @Override
    public List<DashboardItemContent> list(List<String> types) {
        return mDashboardItemContentStore.queryByTypes(types);
    }
}