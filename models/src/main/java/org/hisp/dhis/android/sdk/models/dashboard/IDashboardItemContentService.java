package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.IService;

import java.util.List;

public interface IDashboardItemContentService extends IService {

    DashboardItemContent get(long id);

    DashboardItemContent get(String uid);

    List<DashboardItemContent> list();

    List<DashboardItemContent> list(List<String> types);
}
