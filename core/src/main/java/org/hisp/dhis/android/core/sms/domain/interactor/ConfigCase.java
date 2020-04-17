package org.hisp.dhis.android.core.sms.domain.interactor;

import android.util.Log;

import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;

import io.reactivex.Completable;
import io.reactivex.Single;

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

    public Single<SmsConfig> getSmsModuleConfig() {
        return Single.zip(
                localDbRepository.isModuleEnabled(),
                localDbRepository.getGatewayNumber(),
                localDbRepository.getWaitingForResultEnabled(),
                localDbRepository.getConfirmationSenderNumber(),
                localDbRepository.getWaitingResultTimeout(),
                SmsConfig::new
        );
    }

    /**
     * Set if the SDK has to wait for the result or not.
     * @param enabled
     * @return {@code Completable} that completes when the configuration is changed.
     */
    public Completable setWaitingForResultEnabled(boolean enabled) {
        return localDbRepository.setWaitingForResultEnabled(enabled);
    }

    /**
     * Set the number to receive messages from. Messages from other senders will not be read.
     * @param number The sender number.
     * @return {@code Completable} that completes when the configuration is changed.
     */
    public Completable setConfirmationSenderNumber(String number) {
        return localDbRepository.setConfirmationSenderNumber(number);
    }

    /**
     * Set the time in seconds to wait for the result.
     * @param timeoutSeconds Time in seconds.
     * @return {@code Completable} that completes when the configuration is changed.
     */
    public Completable setWaitingResultTimeout(int timeoutSeconds) {
        return localDbRepository.setWaitingResultTimeout(timeoutSeconds);
    }

    /**
     * Set the gateway number to send the SMS. This is a required parameter before sending SMS.
     * @param gatewayNumber The gateway numberf
     * @return {@code Completable} that completes when the configuration is changed.
     */
    public Completable setGatewayNumber(String gatewayNumber) {
        if (gatewayNumber == null || gatewayNumber.isEmpty()) {
            return Completable.error(new IllegalArgumentException("Gateway number can't be empty"));
        }
        return localDbRepository.setGatewayNumber(gatewayNumber);
    }

    /**
     * Set if SMS Module is enabled or not. It is required to enable it before using it.
     * @param enabled If the module is enabled or not
     * @return {@code Completable} that completes when the configuration is changed.
     */
    public Completable setModuleEnabled(boolean enabled) {
        return localDbRepository.setModuleEnabled(enabled);
    }

    /**
     * Set a new MetadataDownload configuration.
     * @param metadataIdsConfig Configuration with the metadata ids.
     * @return {@code Completable} that completes when the configuration is changed.
     */
    public Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig) {
        if (metadataIdsConfig == null) {
            return Completable.error(new IllegalArgumentException("Received null config"));
        }
        return localDbRepository.setMetadataDownloadConfig(metadataIdsConfig);
    }

    /**
     * Get the metadata download configuration.
     * @return {@code Single with the} metadata download configuration.
     */
    public Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig() {
        return localDbRepository.getMetadataDownloadConfig();
    }

    /**
     * Method to download metadata ids. This is required before using the SMS module.
     * @return {@code Completable} that completes when the metadata ids are downloaded.
     */
    public Completable refreshMetadataIds() {
        Completable refreshTask = getMetadataDownloadConfig().onErrorReturn(throwable -> {
            Log.d(TAG, "Can't read saved SMS metadata download config. Using default.");
            return getDefaultMetadataDownloadConfig();
        }).flatMap(webApiRepository::getMetadataIds
        ).flatMapCompletable(localDbRepository::setMetadataIds);

        return localDbRepository.isModuleEnabled().flatMapCompletable(enabled -> {
            if (enabled) {
                return refreshTask;
            } else {
                Log.d(TAG, "Not refreshing SMS metadata, because sms module is disabled");
                return Completable.complete();
            }
        });
    }

    /**
     * Callable that triggers the method {@link #refreshMetadataIds()}.
     * @return Callable object to refresh metadata ids.
     */
    public Completable refreshMetadataIdsCallable() {
        return refreshMetadataIds()
                .doOnSubscribe(d -> Log.d(TAG, "Started SMS metadata sync."))
                .doOnComplete(() -> Log.d(TAG, "Completed SMS metadata sync."))
                .doOnError(e -> Log.d(TAG, e.getClass().getSimpleName() + " Error on SMS metadata sync."));
    }

    private WebApiRepository.GetMetadataIdsConfig getDefaultMetadataDownloadConfig() {
        return new WebApiRepository.GetMetadataIdsConfig();
    }

    public static class SmsConfig {
        private final boolean moduleEnabled;
        private final String gateway;
        private final boolean waitingForResult;
        private final String resultSender;
        private final int resultWaitingTimeout;

        SmsConfig(boolean moduleEnabled,
                  String gateway,
                  boolean waitingForResult,
                  String resultSender,
                  int resultWaitingTimeout) {
            this.moduleEnabled = moduleEnabled;
            this.gateway = gateway;
            this.waitingForResult = waitingForResult;
            this.resultSender = resultSender;
            this.resultWaitingTimeout = resultWaitingTimeout;
        }

        public boolean isModuleEnabled() {
            return moduleEnabled;
        }

        public String getGateway() {
            return gateway;
        }

        public boolean isWaitingForResult() {
            return waitingForResult;
        }

        public String getResultSender() {
            return resultSender;
        }

        public int getResultWaitingTimeout() {
            return resultWaitingTimeout;
        }
    }
}
