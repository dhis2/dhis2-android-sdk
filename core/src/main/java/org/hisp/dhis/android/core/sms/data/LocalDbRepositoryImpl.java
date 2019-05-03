package org.hisp.dhis.android.core.sms.data;

import android.content.Context;

import org.hisp.dhis.android.core.ObjectMapperFactory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentModule;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventModule;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule;
import org.hisp.dhis.android.core.user.UserModule;
import org.hisp.dhis.smscompression.models.Metadata;

import java.io.IOException;
import java.util.Collections;

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
    private final static String METADATA_FILE = "metadata_ids";
    private final static String CONFIG_FILE = "smsconfig";
    private final static String KEY_GATEWAY = "gateway";
    private final static String KEY_CONFIRMATION_SENDER = "confirmationsender";
    private final static String KEY_WAITING_RESULT_TIMEOUT = "reading_timeout";

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
        return Single.fromCallable(() ->
                ObjectMapperFactory.objectMapper().readValue(
                        context.openFileInput(METADATA_FILE), Metadata.class
                ));
    }

    @Override
    public Completable setMetadataIds(final Metadata metadata) {
        return Completable.fromAction(() ->
                ObjectMapperFactory.objectMapper().writeValue(
                        context.openFileOutput(METADATA_FILE, Context.MODE_PRIVATE), metadata
                ));
    }

    @Override
    public Single<Event> getEventToSubmit(String eventUid, String teiUid) {
        return Single.fromCallable(() ->
                eventModule.events.withTrackedEntityDataValues()
                        .byUid().eq(eventUid).one().get().toBuilder()
                        .trackedEntityInstance(teiUid)
                        .build()
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
}
