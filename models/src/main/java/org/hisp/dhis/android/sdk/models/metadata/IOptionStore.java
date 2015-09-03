package org.hisp.dhis.android.sdk.models.metadata;

import org.hisp.dhis.android.sdk.models.common.IStore;

import java.util.List;

public interface IOptionStore extends IStore<Option> {
    List<Option> query(OptionSet optionSet);
}
