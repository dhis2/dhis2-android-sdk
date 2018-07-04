package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.common.GenericCallData;

import java.util.List;

public interface QueryCallFactory<P, Q extends BaseQuery> {
    Call<List<P>> create(GenericCallData data, Q query);
}