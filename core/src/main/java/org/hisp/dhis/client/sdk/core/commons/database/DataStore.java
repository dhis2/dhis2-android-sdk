package org.hisp.dhis.client.sdk.core.commons.database;

import org.hisp.dhis.client.sdk.models.common.DataModel;
import org.hisp.dhis.client.sdk.models.common.Model;
import org.hisp.dhis.client.sdk.models.common.State;

import java.util.List;

public interface DataStore<T extends DataModel & Model> extends Store<T> {
    List<T> query(State state);

    List<T> query(List<State> states);
}
