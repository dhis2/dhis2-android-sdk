package org.hisp.dhis.android.core.sms.data.localdbrepository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SubmissionType;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

class OngoingSubmissionsStore {
    private static final String TAG = OngoingSubmissionsStore.class.getSimpleName();
    private final static String ONGOING_SUBMISSIONS_FILE = "submissions";
    private final static String ONGOING_SUBMISSIONS_CONFIG_FILE = "submissions_config";
    private final static String KEY_LAST_SUBMISSION_ID = "last_submission_id";
    private final Context context;
    private Map<Integer, SubmissionType> ongoingSubmissions;
    private Integer lastGeneratedSubmissionId;

    OngoingSubmissionsStore(Context context) {
        this.context = context;
    }

    @SuppressLint("UseSparseArrays")
    Single<Map<Integer, SubmissionType>> getOngoingSubmissions() {
        return Single.fromCallable(() -> {
            if (ongoingSubmissions != null) {
                return ongoingSubmissions;
            }
            Type mapType = new TypeToken<Map<Integer, SubmissionType>>() {
            }.getType();
            InputStreamReader reader = new InputStreamReader(
                    context.openFileInput(ONGOING_SUBMISSIONS_FILE),
                    StandardCharsets.UTF_8);
            Map<Integer, SubmissionType> submissions =
                    new GsonBuilder().create().fromJson(reader, mapType);
            reader.close();
            return submissions;
        }).onErrorReturn(throwable -> {
            Log.e(TAG, throwable.getMessage(), throwable);
            return new HashMap<>();
        }).doOnSuccess(submissions -> ongoingSubmissions = submissions);
    }

    Completable addOngoingSubmission(Integer id, SubmissionType type) {
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

    private Completable saveOngoingSubmissions(Map<Integer, SubmissionType> ongoingSubmissions) {
        this.ongoingSubmissions = ongoingSubmissions;
        ArrayList<Completable> tasks = new ArrayList<>();
        tasks.add(Completable.fromAction(() -> {
            Writer writer = new OutputStreamWriter(
                    context.openFileOutput(ONGOING_SUBMISSIONS_FILE, Context.MODE_PRIVATE),
                    StandardCharsets.UTF_8
            );
            new GsonBuilder().create().toJson(ongoingSubmissions, writer);
            writer.close();
        }));
        if (lastGeneratedSubmissionId != null) {
            tasks.add(saveLastGeneratedSubmissionId());
        }
        return Completable.merge(tasks);
    }

    private Single<Integer> getLastGeneratedSubmissionId() {
        if (lastGeneratedSubmissionId != null) {
            return Single.just(lastGeneratedSubmissionId);
        }
        return Single.fromCallable(() ->
                context.getSharedPreferences(ONGOING_SUBMISSIONS_CONFIG_FILE, Context.MODE_PRIVATE)
                        .getInt(KEY_LAST_SUBMISSION_ID, 0)
        ).doOnSuccess(id -> lastGeneratedSubmissionId = id);
    }

    private Completable saveLastGeneratedSubmissionId() {
        return Completable.fromAction(() -> {
            boolean result = context.getSharedPreferences(
                    ONGOING_SUBMISSIONS_CONFIG_FILE, Context.MODE_PRIVATE)
                    .edit().putInt(KEY_LAST_SUBMISSION_ID, lastGeneratedSubmissionId).commit();
            if (!result) {
                throw new IOException("Failed writing last submission id to local storage");
            }
        });
    }

    Single<Integer> generateNextSubmissionId() {
        return Single.zip(
                getOngoingSubmissions(),
                getLastGeneratedSubmissionId(),
                Pair::create
        ).flatMap(ids -> {
            Collection<Integer> ongoingIds = ids.first.keySet();
            Integer lastId = ids.second;
            int i = lastId;
            do {
                i++;
                if (i >= 255) {
                    i = 0;
                }
                if (!ongoingIds.contains(i)) {
                    lastGeneratedSubmissionId = i;
                    return Single.just(i);
                }
            } while (i != lastId);
            return Single.error(new LocalDbRepository.TooManySubmissionsException());
        });
    }
}
