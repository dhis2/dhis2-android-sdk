package org.hisp.dhis.android.sdk.models.metadata;

import org.hisp.dhis.android.sdk.models.common.IIdentifiableObjectStore;

import java.util.List;

public interface IOptionStore extends IIdentifiableObjectStore<Option> {
    List<Option> query(OptionSet optionSet);
}
