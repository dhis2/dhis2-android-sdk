package org.hisp.dhis.android.sdk.models.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SystemInfo {

    @JsonProperty("buildTime")
    DateTime buildTime;

    @JsonProperty("serverDate")
    DateTime serverDate;

    @JsonProperty("calendar")
    String calendar;

    @JsonProperty("dateFormat")
    String dateFormat;

    @JsonProperty("intervalSinceLastAnalyticsTableSuccess")
    String intervalSinceLastAnalyticsTableSuccess;

    @JsonProperty("lastAnalyticsTableSuccess")
    String lastAnalyticsTableSuccess;

    @JsonProperty("revision")
    int revision;

    @JsonProperty("version")
    String version;

    @JsonIgnore
    public DateTime getBuildTime() {
        return buildTime;
    }

    @JsonIgnore
    public void setBuildTime(DateTime buildTime) {
        this.buildTime = buildTime;
    }

    @JsonIgnore
    public DateTime getServerDate() {
        return serverDate;
    }

    @JsonIgnore
    public void setServerDate(DateTime serverDate) {
        this.serverDate = serverDate;
    }

    @JsonIgnore
    public String getCalendar() {
        return calendar;
    }

    @JsonIgnore
    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    @JsonIgnore
    public String getDateFormat() {
        return dateFormat;
    }

    @JsonIgnore
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @JsonIgnore
    public String getIntervalSinceLastAnalyticsTableSuccess() {
        return intervalSinceLastAnalyticsTableSuccess;
    }

    @JsonIgnore
    public void setIntervalSinceLastAnalyticsTableSuccess(String date) {
        this.intervalSinceLastAnalyticsTableSuccess = date;
    }

    @JsonIgnore
    public String getLastAnalyticsTableSuccess() {
        return lastAnalyticsTableSuccess;
    }

    @JsonIgnore
    public void setLastAnalyticsTableSuccess(String lastAnalyticsTableSuccess) {
        this.lastAnalyticsTableSuccess = lastAnalyticsTableSuccess;
    }

    @JsonIgnore
    public int getRevision() {
        return revision;
    }

    @JsonIgnore
    public void setRevision(int revision) {
        this.revision = revision;
    }

    @JsonIgnore
    public String getVersion() {
        return version;
    }

    @JsonIgnore
    public void setVersion(String version) {
        this.version = version;
    }
}
