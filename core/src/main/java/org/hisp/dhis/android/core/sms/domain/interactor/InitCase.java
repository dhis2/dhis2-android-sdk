package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;

import java.util.ArrayList;

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
                                     String confirmationSenderNumber,
                                     WebApiRepository.GetMetadataIdsConfig metadataIdsConfig) {
        if (gatewayNumber == null || gatewayNumber.isEmpty()) {
            return Completable.error(new IllegalArgumentException("Gateway number can't be empty"));
        }
        ArrayList<Completable> tasks = new ArrayList<>();
        tasks.add(refreshMetadataIds(metadataIdsConfig));
        tasks.add(localDbRepository.setGatewayNumber(gatewayNumber));
        if (confirmationSenderNumber != null && !confirmationSenderNumber.isEmpty()) {
            tasks.add(localDbRepository.setConfirmationSenderNumber(confirmationSenderNumber));
        }
        return Completable.merge(tasks);
    }

    public Completable refreshMetadataIds(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig) {
        if (metadataIdsConfig == null) {
            return Completable.error(new IllegalArgumentException("Metadata ids downloading config can't be null"));
        }
        return webApiRepository.getMetadataIds(metadataIdsConfig)
                .flatMapCompletable(localDbRepository::setMetadataIds);
    }
}
