package org.hisp.dhis.client.sdk.core;

public class OptionSetInteractorImpl implements OptionSetInteractor {
    private final OptionSetStore optionSetStore;
    private final OptionSetApi optionSetApi;

    public OptionSetInteractorImpl(OptionSetStore optionSetStore, OptionSetApi optionSetApi) {
        this.optionSetStore = optionSetStore;
        this.optionSetApi = optionSetApi;
    }


    @Override
    public OptionSetStore store() {
        return optionSetStore;
    }

    @Override
    public OptionSetApi api() {
        return optionSetApi;
    }
}
