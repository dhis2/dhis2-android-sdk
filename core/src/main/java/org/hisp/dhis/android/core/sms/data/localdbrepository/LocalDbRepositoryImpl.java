package org.hisp.dhis.android.core.sms.data.localdbrepository;

import android.content.Context;
import android.content.SharedPreferences;

import org.hisp.dhis.android.core.ObjectMapperFactory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentModule;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventModule;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule;
import org.hisp.dhis.android.core.user.UserModule;
import org.hisp.dhis.smscompression.models.Metadata;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class LocalDbRepositoryImpl implements LocalDbRepository {
    private final Context context;
    private final UserModule userModule;
    private final TrackedEntityModule trackedEntityModule;
    private final EventModule eventModule;
    private final EnrollmentModule enrollmentModule;
    private final EventStore eventStore;
    private final EnrollmentStore enrollmentStore;
    private final static String CONFIG_FILE = "smsconfig";
    private final static String KEY_GATEWAY = "gateway";
    private final static String KEY_CONFIRMATION_SENDER = "confirmationsender";
    private final static String KEY_WAITING_RESULT_TIMEOUT = "reading_timeout";
    private static final String KEY_METADATA_CONFIG = "metadata_conf";
    private static final String KEY_MODULE_ENABLED = "module_enabled";

    private final MetadataIdsStore metadataIdsStore;
    private final OngoingSubmissionsStore ongoingSubmissionsStore;

    @Inject
    public LocalDbRepositoryImpl(Context ctx,
                                 UserModule userModule,
                                 TrackedEntityModule trackedEntityModule,
                                 EventModule eventModule,
                                 EnrollmentModule enrollmentModule,
                                 EventStore eventStore,
                                 EnrollmentStore enrollmentStore) {
        this.context = ctx;
        this.userModule = userModule;
        this.trackedEntityModule = trackedEntityModule;
        this.eventModule = eventModule;
        this.enrollmentModule = enrollmentModule;
        this.eventStore = eventStore;
        this.enrollmentStore = enrollmentStore;
        metadataIdsStore = new MetadataIdsStore(context);
        ongoingSubmissionsStore = new OngoingSubmissionsStore(context);
    }

    @Override
    public Single<String> getUserName() {
        return Single.fromCallable(() -> userModule.authenticatedUser.get().user());
    }

    @Override
    public Single<String> getGatewayNumber() {
        return Single.fromCallable(() ->
                context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                        .getString(KEY_GATEWAY, null)
        );
    }

    @Override
    public Completable setGatewayNumber(String number) {
        return Completable.fromAction(() -> {
            boolean result = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                    .edit().putString(KEY_GATEWAY, number).commit();
            if (!result) {
                throw new IOException("Failed writing gateway number to local storage");
            }
        });
    }

    @Override
    public Single<Integer> getWaitingResultTimeout() {
        return Single.fromCallable(() ->
                context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                        .getInt(KEY_WAITING_RESULT_TIMEOUT, 120)
        );
    }

    @Override
    public Completable setWaitingResultTimeout(Integer timeoutSeconds) {
        return Completable.fromAction(() -> {
            boolean result = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                    .edit().putInt(KEY_WAITING_RESULT_TIMEOUT, timeoutSeconds).commit();
            if (!result) {
                throw new IOException("Failed writing timeout setting to local storage");
            }
        });
    }

    @Override
    public Single<String> getConfirmationSenderNumber() {
        return Single.fromCallable(() ->
                context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                        .getString(KEY_CONFIRMATION_SENDER, null)
        );
    }

    @Override
    public Completable setConfirmationSenderNumber(String number) {
        return Completable.fromAction(() -> {
            boolean result = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                    .edit().putString(KEY_CONFIRMATION_SENDER, number).commit();
            if (!result) {
                throw new IOException("Failed writing confirmation sender number to local storage");
            }
        });
    }

    @Override
    public Single<Metadata> getMetadataIds() {
        return metadataIdsStore.getMetadataIds();
    }

    @Override
    public Completable setMetadataIds(final Metadata metadata) {
        return metadataIdsStore.setMetadataIds(metadata);
    }

    @Override
    public Single<Event> getTrackerEventToSubmit(String eventUid) {
        return getSimpleEventToSubmit(eventUid); // no difference at the moment
    }

    @Override
    public Single<Event> getSimpleEventToSubmit(String eventUid) {
        return Single.fromCallable(() ->
                eventModule.events.withTrackedEntityDataValues()
                        .byUid().eq(eventUid).one().get()
        );
    }

    @Override
    public Single<TrackedEntityInstance> getTeiEnrollmentToSubmit(String enrollmentUid, String teiUid) {
        return Single.fromCallable(() -> {
            Enrollment enrollment = enrollmentModule.enrollments.byUid().eq(enrollmentUid).one().get();
            return trackedEntityModule.trackedEntityInstances.withTrackedEntityAttributeValues()
                    .byUid().eq(teiUid).one().get().toBuilder()
                    .enrollments(Collections.singletonList(enrollment))
                    .build();
        });
    }

    @Override
    public Completable updateEventSubmissionState(String eventUid, State state) {
        return Completable.fromAction(() -> eventStore.setState(eventUid, state));
    }

    @Override
    public Completable updateEnrollmentSubmissionState(String enrollmentUid, State state) {
        return Completable.fromAction(() -> enrollmentStore.setState(enrollmentUid, state));
    }

    @Override
    public Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig config) {
        return Completable.fromAction(() -> {
            String value = ObjectMapperFactory.objectMapper().writeValueAsString(config);
            SharedPreferences.Editor editor = context
                    .getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                    .edit().putString(KEY_METADATA_CONFIG, value);
            if (!editor.commit()) {
                throw new IOException("Failed writing SMS metadata config to local storage");
            }
        });
    }

    @Override
    public Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig() {
        return Single.fromCallable(() -> {
            String stringVal = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                    .getString(KEY_METADATA_CONFIG, null);
            return ObjectMapperFactory.objectMapper()
                    .readValue(stringVal, WebApiRepository.GetMetadataIdsConfig.class);
        });
    }

    @Override
    public Completable setModuleEnabled(boolean enabled) {
        return Completable.fromAction(() -> {
            boolean result = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                    .edit().putBoolean(KEY_MODULE_ENABLED, enabled).commit();
            if (!result) {
                throw new IOException("Failed writing module enabled value to local storage");
            }
        });
    }

    @Override
    public Single<Boolean> isModuleEnabled() {
        return Single.fromCallable(() ->
                context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                        .getBoolean(KEY_MODULE_ENABLED, false)
        );
    }

    @Override
    public Single<Map<Integer, SubmissionType>> getOngoingSubmissions() {
        return ongoingSubmissionsStore.getOngoingSubmissions();
    }

    @Override
    public Completable addOngoingSubmission(Integer id, SubmissionType type) {
        return ongoingSubmissionsStore.addOngoingSubmission(id, type);
    }

    @Override
    public Completable removeOngoingSubmission(Integer id) {
        return ongoingSubmissionsStore.removeOngoingSubmission(id);
    }
}
