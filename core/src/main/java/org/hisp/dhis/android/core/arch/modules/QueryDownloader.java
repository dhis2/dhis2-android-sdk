package org.hisp.dhis.android.core.arch.modules;

import org.hisp.dhis.android.core.calls.Call;
import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.List;

public interface QueryDownloader<O, Q extends BaseQuery> {
    Call<List<O>> download(Q query);
}