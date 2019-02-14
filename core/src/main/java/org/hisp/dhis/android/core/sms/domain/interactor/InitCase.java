package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;

import io.reactivex.Completable;

/**
 * Used to set initial data that is common for all sms sending tasks
 */
public class InitCase {
    private final LocalDbRepository localDbRepository;

    public InitCase(LocalDbRepository localDbRepository) {
        this.localDbRepository = localDbRepository;
    }

    public Completable initSMSModule(String gatewayNumber, String confirmationSenderNumber) {
        if (gatewayNumber == null || gatewayNumber.isEmpty()) {
            return Completable.error(new IllegalArgumentException("Gateway number can't be empty"));
        }
        return localDbRepository.setGatewayNumber(gatewayNumber)
                .andThen(confirmationSenderNumber == null ?
                        Completable.complete() :
                        localDbRepository.setConfirmationSenderNumber(confirmationSenderNumber)
                );
    }
}
