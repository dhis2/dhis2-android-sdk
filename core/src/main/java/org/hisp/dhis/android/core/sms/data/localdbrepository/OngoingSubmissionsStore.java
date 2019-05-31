package org.hisp.dhis.android.core.sms.data.localdbrepository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

class OngoingSubmissionsStore {
    private static final String TAG = OngoingSubmissionsStore.class.getSimpleName();
    private final static String ONGOING_SUBMISSIONS_FILE = "submissions";
    private final Context context;
    private Map<Integer, LocalDbRepository.SubmissionType> ongoingSubmissions;

    OngoingSubmissionsStore(Context context) {
        this.context = context;
    }

    @SuppressLint("UseSparseArrays")
    Single<Map<Integer, LocalDbRepository.SubmissionType>> getOngoingSubmissions() {
        return Single.fromCallable(() -> {
            if (ongoingSubmissions != null) {
                return ongoingSubmissions;
            }
            Type mapType = new TypeToken<Map<Integer, LocalDbRepository.SubmissionType>>() {
            }.getType();
            return new GsonBuilder().create().fromJson(
                    new InputStreamReader(
                            context.openFileInput(ONGOING_SUBMISSIONS_FILE),
                            StandardCharsets.UTF_8),
                    mapType);
        }).onErrorReturn(throwable -> {
            Log.e(TAG, throwable.getMessage(), throwable);
            return new HashMap<>();
        }).doOnSuccess(submissions -> ongoingSubmissions = submissions);
    }

    Completable addOngoingSubmission(Integer id, LocalDbRepository.SubmissionType type) {
        if (id == null || id < 0 || id > 255) {
            return Completable.error(new IllegalArgumentException("Wrong submission id"));
        }
        if (type == null) {
            return Completable.error(new IllegalArgumentException("Wrong submission type"));
        }
        return getOngoingSubmissions().flatMapCompletable(submissions -> {
            if (submissions.containsKey(id)) {
                return Completable.error(new IllegalArgumentException("Submission id already exists"));
            }
            submissions.put(id, type);
            return saveOngoingSubmissions(submissions);
        });
    }

    Completable removeOngoingSubmission(Integer id) {
        if (id == null) {
            return Completable.error(new IllegalArgumentException("Wrong submission id"));
        }
        return getOngoingSubmissions().flatMapCompletable(submissions -> {
            submissions.remove(id);
            return saveOngoingSubmissions(ongoingSubmissions);
        });
    }

    private Completable saveOngoingSubmissions(Map<Integer, LocalDbRepository.SubmissionType> ongoingSubmissions) {
        this.ongoingSubmissions = ongoingSubmissions;
        return Completable.fromAction(() -> {
            Writer writer = new OutputStreamWriter(
                    context.openFileOutput(ONGOING_SUBMISSIONS_FILE, Context.MODE_PRIVATE),
                    StandardCharsets.UTF_8
            );
            new GsonBuilder().create().toJson(ongoingSubmissions, writer);
        });
    }
}
