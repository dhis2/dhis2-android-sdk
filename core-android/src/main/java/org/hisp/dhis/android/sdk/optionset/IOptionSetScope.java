package org.hisp.dhis.android.sdk.optionset;

import org.hisp.dhis.java.sdk.models.optionset.Option;
import org.hisp.dhis.java.sdk.models.optionset.OptionSet;

import java.util.List;

import rx.Observable;

public interface IOptionSetScope {
    Observable<OptionSet> get(String uid);

    Observable<OptionSet> get(long id);

    Observable<List<OptionSet>> list();

    Observable<List<Option>> getOptions(OptionSet optionSet);

    Observable<Boolean> save(OptionSet object);

    Observable<Boolean> remove(OptionSet object);
}
