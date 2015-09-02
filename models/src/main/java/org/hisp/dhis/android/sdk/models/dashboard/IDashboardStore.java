package org.hisp.dhis.android.sdk.models.dashboard;

import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.common.meta.State;

import java.util.List;

public interface IDashboardStore extends IStore<Dashboard> {
    List<Dashboard> query(State... states);

    List<Dashboard> query(List<State> states);

    List<Dashboard> filter(State state);
}
