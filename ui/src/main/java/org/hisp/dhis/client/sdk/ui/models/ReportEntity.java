package org.hisp.dhis.client.sdk.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ReportEntity implements Parcelable {
    private final String id;
    private final Status status;

    private final ArrayList<String> displayInReports;

    public ReportEntity(String id, Status status, ArrayList<String> displayInReports) {
        this.id = isNull(id, "id must not be null");
        this.status = isNull(status, "status must not be null");
        this.displayInReports = displayInReports;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(status.toString());
        dest.writeList(displayInReports);
    }

    public static final Parcelable.Creator<ReportEntity> CREATOR
            = new Parcelable.Creator<ReportEntity>() {
        public ReportEntity createFromParcel(Parcel in) {
            return new ReportEntity(in);
        }

        public ReportEntity[] newArray(int size) {
            return new ReportEntity[size];
        }
    };

    private ReportEntity(Parcel in) {
        id = in.readString();
        status = Status.fromString(in.readString());
        displayInReports = new ArrayList<String>();
        in.readList(displayInReports, null);
    }

    public enum Status {
        SENT, TO_UPDATE, TO_POST, ERROR;

        public static Status fromString(String enumString) {
            switch (enumString) {
                case "SENT":
                    return SENT;
                case "TO_UPDATE":
                    return TO_UPDATE;
                case "TO_POST":
                    return TO_POST;
                default:
                    return ERROR;
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case SENT:
                    return "SENT";
                case TO_UPDATE:
                    return "TO_UPDATE";
                case TO_POST:
                    return "TO_POST";
                default:
                    return "ERROR";
            }
        }
    }

    public ArrayList<String> getDisplayInReports() {
        return displayInReports;
    }
}
