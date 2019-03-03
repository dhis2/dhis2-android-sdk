package org.hisp.dhis.android.core.sms.data;

import android.content.Context;

import org.hisp.dhis.android.core.ObjectMapperFactory;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.event.EventModel;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.user.UserModule;
import org.hisp.dhis.smscompression.models.Metadata;

import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Single;

public class LocalDbRepositoryImpl implements LocalDbRepository {

    private final Context context;
    private final UserModule userModule;
    private final EventStore eventStore;
    private final EnrollmentStore enrollmentStore;
    private final static String METADATA_FILE = "metadata_ids";
    private final static String CONFIG_FILE = "smsconfig";
    private final static String KEY_GATEWAY = "gateway";
    private final static String KEY_CONFIRMATION_SENDER = "confirmationsender";
    private final static String KEY_WAITING_RESULT_TIMEOUT = "reading_timeout";

    public LocalDbRepositoryImpl(Context ctx,
                                 UserModule userModule,
                                 EventStore eventStore,
                                 EnrollmentStore enrollmentStore) {
        this.context = ctx;
        this.userModule = userModule;
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
    public Completable updateSubmissionState(BaseDataModel item, State state) {
        if (item instanceof EventModel) {
            String uid = ((EventModel) item).uid();
            return Completable.fromAction(() -> eventStore.setState(uid, state));
        } else if (item instanceof EnrollmentModel) {
            String uid = ((EnrollmentModel) item).uid();
            return Completable.fromAction(() -> enrollmentStore.setState(uid, state));
        }
        return Completable.error(new IllegalArgumentException("Not supported data type"));
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
}
