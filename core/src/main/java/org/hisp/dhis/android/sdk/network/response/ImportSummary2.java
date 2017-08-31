package org.hisp.dhis.android.sdk.network.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportSummary2 {

    @JsonProperty("status")
    private Status status;

    @JsonProperty("description")
    private String description;

    @JsonProperty("importCount")
    private ImportCount2 importCount;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("href")
    private String href;

    @JsonProperty("conflicts")
    private List<Conflict2> conflicts;


    public ImportSummary2() {
        // explicit empty constructor
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public ImportCount2 getImportCount() {
        return importCount;
    }

    public String getReference() {
        return reference;
    }

    public String getHref() {
        return href;
    }

    public List<Conflict2> getConflicts() {
        return conflicts;
    }

    public boolean isSuccessOrOK() {
        return ImportSummary2.Status.SUCCESS.equals(status) ||
                ImportSummary2.Status.OK.equals(status);
    }

    public boolean isError() {
        return Status.ERROR.equals(status);
    }

    public enum Status {
        SUCCESS, OK, ERROR
    }
}
