package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;

import io.reactivex.Completable;

/**
 * Used to set initial data that is common for all sms sending tasks
 */
public class InitCase {
    private final LocalDbRepository localDbRepository;
    private final WebApiRepository webApiRepository;

    public InitCase(WebApiRepository webApiRepository, LocalDbRepository localDbRepository) {
        this.localDbRepository = localDbRepository;
        this.webApiRepository = webApiRepository;
    }

    public Completable initSMSModule(String gatewayNumber,
                                     WebApiRepository.GetMetadataIdsConfig metadataIdsConfig) {
        if (gatewayNumber == null || gatewayNumber.isEmpty()) {
            return Completable.error(new IllegalArgumentException("Gateway number can't be empty"));
        }
        return Completable.mergeArray(
                refreshMetadataIds(metadataIdsConfig),
                localDbRepository.setGatewayNumber(gatewayNumber)
        );
    }

    public Completable setResultListeningConfig(String confirmationSenderNumber, Integer timeoutSeconds) {
        return Completable.mergeArray(
                localDbRepository.setConfirmationSenderNumber(confirmationSenderNumber),
                localDbRepository.setWaitingResultTimeout(timeoutSeconds)
        );
    }

    public Completable refreshMetadataIds(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig) {
        if (metadataIdsConfig == null) {
            return Completable.error(new IllegalArgumentException("Metadata ids downloading config can't be null"));
        }
        return webApiRepository.getMetadataIds(metadataIdsConfig)
                .flatMapCompletable(localDbRepository::setMetadataIds);
    }
}
