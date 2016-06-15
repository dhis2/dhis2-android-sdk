package org.hisp.dhis.client.sdk.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class ReportEntity implements Parcelable {
    private final String id;
    private final Status status;

    private final ArrayList<String> dataElementLabels;
    private final ArrayList<String> dataElementValues;

    public ReportEntity(String id, Status status, ArrayList<String> dataElementLabels,
                        ArrayList<String> dataElementValues) {
        this.id = isNull(id, "id must not be null");
        this.status = isNull(status, "status must not be null");
        this.dataElementLabels = dataElementLabels;
        this.dataElementValues = dataElementValues;
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
        dest.writeString(status.name());
        dest.writeList(dataElementLabels);
        dest.writeList(dataElementValues);
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
        status = Status.valueOf(in.readString());
        dataElementLabels = new ArrayList<>();
        in.readList(dataElementLabels, null);
        dataElementValues = new ArrayList<>();
        in.readList(dataElementValues, null);
    }

    public String getDisplayTextFromLabel(String label) {
        String value;
        if (dataElementLabels.contains(label)) {
            value = dataElementValues.get(dataElementLabels.indexOf(label));
        } else {
            value = "none";
        }

        return String.format("%s: %s", label, value);
    }

    public enum Status {
        SENT, TO_UPDATE, TO_POST, ERROR
    }

    public ArrayList<String> getDataElementLabels() {
        return dataElementLabels;
    }

    public ArrayList<String> getDataElementValues() {
        return dataElementValues;
    }

}
