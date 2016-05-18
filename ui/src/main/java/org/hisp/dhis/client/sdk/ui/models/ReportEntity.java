package org.hisp.dhis.client.sdk.ui.models;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ReportEntity {
    private final String id;
    private final Status status;

    private final String lineOne;
    private final String lineTwo;
    private final String lineThree;

    public ReportEntity(String id, Status status, String lineOne,
                        String lineTwo, String lineThree) {
        this.id = isNull(id, "id must not be null");
        this.status = isNull(status, "status must not be null");
        this.lineOne = lineOne;
        this.lineTwo = lineTwo;
        this.lineThree = lineThree;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public String getLineOne() {
        return lineOne;
    }

    public String getLineTwo() {
        return lineTwo;
    }

    public String getLineThree() {
        return lineThree;
    }

    public enum Status {
        SENT, OFFLINE, ERROR
    }
}
