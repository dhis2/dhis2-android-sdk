package org.hisp.dhis.client.sdk.core;

public class ProgramInteractorImpl implements ProgramInteractor {
    private final ProgramStore programStore;
    private final ProgramsApi programsApi;
    private final MetadataApi metadataApi;

    public ProgramInteractorImpl(ProgramsApi programsApi,
                             ProgramStore programStore,
                             MetadataApi metadataApi) {
        this.programsApi = programsApi;
        this.programStore = programStore;
        this.metadataApi = metadataApi;
    }


    @Override
    public ProgramStore store() {
        return programStore;
    }

    @Override
    public ProgramsApi api() {
        return programsApi;
    }
}
