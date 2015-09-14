package org.hisp.dhis.android.sdk.models.option;

import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;
import org.hisp.dhis.android.sdk.models.optionset.OptionSet;

import java.util.List;

public interface IOptionStore extends IIdentifiableObjectStore<Option> {
    List<Option> query(OptionSet optionSet);
}
