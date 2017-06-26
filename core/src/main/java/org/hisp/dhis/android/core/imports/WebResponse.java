package org.hisp.dhis.android.core.imports;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class WebResponse {
    private static final String MESSAGE = "message";
    private static final String IMPORT_SUMMARIES = "response"; // is called response from api

    @NonNull
    @JsonProperty(MESSAGE)
    public abstract String message();

    @NonNull
    @JsonProperty(IMPORT_SUMMARIES)
    public abstract ImportSummaries importSummaries();

    @JsonCreator
    public static WebResponse create(
            @JsonProperty(MESSAGE) String message,
            @JsonProperty(IMPORT_SUMMARIES) ImportSummaries importSummaries) {
        return new AutoValue_WebResponse(message, importSummaries);
    }
}
