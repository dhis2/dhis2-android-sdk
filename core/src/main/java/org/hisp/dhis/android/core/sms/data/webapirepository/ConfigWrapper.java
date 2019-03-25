package org.hisp.dhis.android.core.sms.data.webapirepository;

import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;

class ConfigWrapper {
    private final WebApiRepository.GetMetadataIdsConfig config;

    ConfigWrapper(WebApiRepository.GetMetadataIdsConfig config) {
        this.config = config;
    }

    String dataElements() {
        return value(config.dataElements);
    }

    String categoryOptionCombos() {
        return value(config.categoryOptionCombos);
    }

    String organisationUnits() {
        return value(config.organisationUnits);
    }

    String users() {
        return value(config.users);
    }

    String trackedEntityTypes() {
        return value(config.trackedEntityTypes);
    }

    String trackedEntityAttributes() {
        return value(config.trackedEntityAttributes);
    }

    String programs() {
        return value(config.programs);
    }

    private String value(boolean enable) {
        return enable ? ApiService.GET_IDS : null;
    }
}
