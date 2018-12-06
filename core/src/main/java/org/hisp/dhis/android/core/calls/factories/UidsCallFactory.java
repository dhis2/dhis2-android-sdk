package org.hisp.dhis.android.core.calls.factories;

import org.hisp.dhis.android.core.calls.Call;

import java.util.List;
import java.util.Set;

public interface UidsCallFactory<P> {
    Call<List<P>> create(Set<String> uids);
}