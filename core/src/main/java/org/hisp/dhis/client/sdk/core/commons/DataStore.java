package org.hisp.dhis.client.sdk.core.commons;

import org.hisp.dhis.client.sdk.models.common.DataModel;
import org.hisp.dhis.client.sdk.models.common.State;

import java.util.List;

public interface DataStore<T extends DataModel> {
    List<T> query(State state);

    List<T> query(List<State> states);
}
