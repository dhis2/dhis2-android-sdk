package org.hisp.dhis.client.sdk.android.optionset;


import org.hisp.dhis.client.sdk.models.optionset.Option;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;

import java.util.List;

import rx.Observable;

public interface IOptionSetScope {
    Observable<OptionSet> get(String uid);

    Observable<OptionSet> get(long id);

    Observable<List<OptionSet>> list();

    Observable<List<Option>> list(OptionSet optionSet);

    Observable<Boolean> save(OptionSet object);

    Observable<Boolean> remove(OptionSet object);
}
