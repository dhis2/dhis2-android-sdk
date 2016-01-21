package org.hisp.dhis.client.sdk.android.optionset;

import org.hisp.dhis.client.sdk.core.optionset.IOptionSetApiClient;
import org.hisp.dhis.client.sdk.models.optionset.OptionSet;
import org.joda.time.DateTime;

import java.util.List;

public class OptionSetApiClient implements IOptionSetApiClient {
    private final IOptionSetApiClientRetrofit optionSetApiClientRetrofit;

    public OptionSetApiClient(IOptionSetApiClientRetrofit optionSetApiClientRetrofit) {
        this.optionSetApiClientRetrofit = optionSetApiClientRetrofit;
    }

    @Override
    public List<OptionSet> getBasicOptionSets(DateTime dateTime) {
        return null;
    }

    @Override
    public List<OptionSet> getFullOptionSets(DateTime dateTime) {
        return null;
    }
}
