package org.hisp.dhis.client.sdk.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class FormSection implements Parcelable {
    private final String id;
    private final String label;

    public FormSection(String id, String label) {
        this.id = isNull(id, "id must not be null");
        this.label = isNull(label, "label must not be null");
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "FormSection{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        FormSection that = (FormSection) other;
        if (!id.equals(that.id)) {
            return false;
        }

        return label.equals(that.label);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + label.hashCode();
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getLabel());
    }

    public static final Parcelable.Creator<FormSection> CREATOR
            = new Parcelable.Creator<FormSection>() {

        public FormSection createFromParcel(Parcel in) {
            return new FormSection(in.readString(), in.readString());
        }

        public FormSection[] newArray(int size) {
            return new FormSection[size];
        }
    };
}
