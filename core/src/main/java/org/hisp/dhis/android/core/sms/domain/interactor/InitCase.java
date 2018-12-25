package org.hisp.dhis.android.core.sms.domain.interactor;

import org.hisp.dhis.android.core.sms.domain.repository.ApiRepository;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;

import io.reactivex.Completable;

public class InitCase {
    private LocalDbRepository localDbRepository;
    private ApiRepository apiRepository;

    // TODO inject repos
    public InitCase() {
    }

    public Completable initSMSModule() {
        // TODO add sms commands if needed
        return apiRepository.getGatewayNumber().flatMapCompletable(
                result -> localDbRepository.setGatewayNumber(result)
        );
    }
}
