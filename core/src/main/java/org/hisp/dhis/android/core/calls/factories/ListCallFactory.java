package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.calls.Call;

import java.util.List;

public interface ListCallFactory<P> {
    Call<List<P>> create();
}