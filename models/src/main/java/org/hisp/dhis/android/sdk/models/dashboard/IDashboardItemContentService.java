package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.IService;

import java.util.List;

/**
 * Created by arazabishov on 9/30/15.
 */
public interface IDashboardItemContentService extends IService {

    DashboardItemContent query(long id);

    List<DashboardItemContent> query();

    List<DashboardItemContent> query(List<String> types);
}
