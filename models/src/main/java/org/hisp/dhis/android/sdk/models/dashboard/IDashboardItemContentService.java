package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.IService;

import java.util.List;

public interface IDashboardItemContentService extends IService {

    DashboardItemContent query(long id);

    DashboardItemContent query(String uid);

    List<DashboardItemContent> query();

    List<DashboardItemContent> query(List<String> types);
}
