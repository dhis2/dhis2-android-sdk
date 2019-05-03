package org.hisp.dhis.android.core.sms.domain.interactor;

import android.util.Log;

import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;

import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;

/**
 * Used to set initial data that is common for all sms sending tasks
 */
public class ConfigCase {
    private final static String TAG = ConfigCase.class.getSimpleName();
    private final LocalDbRepository localDbRepository;
    private final WebApiRepository webApiRepository;

    public ConfigCase(WebApiRepository webApiRepository, LocalDbRepository localDbRepository) {
        this.localDbRepository = localDbRepository;
        this.webApiRepository = webApiRepository;
    }

    public Completable setGatewayNumber(String gatewayNumber) {
        if (gatewayNumber == null || gatewayNumber.isEmpty()) {
            return Completable.error(new IllegalArgumentException("Gateway number can't be empty"));
        }
        return localDbRepository.setGatewayNumber(gatewayNumber);
    }

    public Single<String> getGatewayNumber() {
        return localDbRepository.getGatewayNumber();
    }

    public Completable setResultListeningConfig(String confirmationSenderNumber, Integer timeoutSeconds) {
        return Completable.mergeArray(
                localDbRepository.setConfirmationSenderNumber(confirmationSenderNumber),
                localDbRepository.setWaitingResultTimeout(timeoutSeconds)
        );
    }

    public Completable setModuleEnabled(boolean enabled) {
        return localDbRepository.setModuleEnabled(enabled);
    }

    public Single<Boolean> isModuleEnabled() {
        return localDbRepository.isModuleEnabled();
    }

    public Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig) {
        if (metadataIdsConfig == null) {
            return Completable.error(new IllegalArgumentException("Received null config"));
        }
        return localDbRepository.setMetadataDownloadConfig(metadataIdsConfig);
    }

    public Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig() {
        return localDbRepository.getMetadataDownloadConfig();
    }

    public Completable refreshMetadataIds() {
        Completable refreshTask = getMetadataDownloadConfig().onErrorReturn(throwable -> {
            Log.d(TAG, "Can't read saved SMS metadata download config. Using default.");
            return getDefaultMetadataDownloadConfig();
        }).flatMap(webApiRepository::getMetadataIds
        ).flatMapCompletable(localDbRepository::setMetadataIds);

        return isModuleEnabled().flatMapCompletable(enabled -> {
            if (enabled) {
                return refreshTask;
            } else {
                Log.d(TAG, "Not refreshing SMS metadata, because sms module is disabled");
                return Completable.complete();
            }
        });
    }

    public Callable<Void> refreshMetadataIdsCallable() {
        return () -> {
            refreshMetadataIds().subscribe(new CompletableObserver() {
                @Override
                public void onSubscribe(Disposable d) {
                    Log.d(TAG, "Started SMS metadata sync.");
                }

                @Override
                public void onComplete() {
                    Log.d(TAG, "Completed SMS metadata sync.");
                }

                @Override
                public void onError(Throwable e) {
                    Log.d(TAG, e.getClass().getSimpleName() + " Error on SMS metadata sync.");
                }
            });
            return null;
        };
    }

    private WebApiRepository.GetMetadataIdsConfig getDefaultMetadataDownloadConfig() {
        return new WebApiRepository.GetMetadataIdsConfig();
    }
}
