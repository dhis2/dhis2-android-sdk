package org.hisp.dhis.android.core.data.database;

import org.hisp.dhis.android.core.common.State;

public class DbStateColumnAdapter extends EnumColumnAdapter<State> {
    @Override
    protected Class<State> getEnumClass() {
        return State.class;
    }
}
