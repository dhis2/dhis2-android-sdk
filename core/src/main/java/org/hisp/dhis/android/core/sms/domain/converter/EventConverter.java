package org.hisp.dhis.android.core.sms.domain.converter;

import android.support.annotation.NonNull;
import android.util.Log;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;

import java.util.ArrayList;
import java.util.Collection;

import io.reactivex.Single;

public class EventConverter extends Converter<Event, EventConverter.EventSubmissionParams> {

    private final LocalDbRepository localDbRepository;

    public EventConverter(LocalDbRepository localDbRepository) {
        this.localDbRepository = localDbRepository;
    }

    @Override
    public String format(@NonNull Event dataObject, EventSubmissionParams params) {
        // TODO convert using compression library when received
        Log.d("", params.username + " " + params.categoryOptionCombo);
        return null;
    }

    @Override
    public Collection<String> getConfirmationRequiredTexts(Event event) {
        // TODO what is the confirmation sms text?
        return new ArrayList<>();
    }

    @Override
    public Single<EventSubmissionParams> getParamsTask() {
        return Single.zip(localDbRepository.getUserName(), localDbRepository.getDefaultCategoryOptionCombo(),
                EventConverter.EventSubmissionParams::new);
    }

    public static class EventSubmissionParams {
        final String username;
        final String categoryOptionCombo;

        public EventSubmissionParams(String username, String categoryOptionCombo) {
            this.username = username;
            this.categoryOptionCombo = categoryOptionCombo;
        }
    }
}
