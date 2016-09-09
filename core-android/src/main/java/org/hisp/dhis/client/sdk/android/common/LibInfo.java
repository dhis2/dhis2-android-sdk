package org.hisp.dhis.client.sdk.android.common;

import android.os.Parcel;
import android.os.Parcelable;

public class LibInfo implements Parcelable {

    private String name;
    private String licence;

    public LibInfo(String name, String licence) {
        this.name = name;
        this.licence = licence;
    }

    public LibInfo(Parcel source) {
        // String[] data = new String[2];
        // source.readStringArray(data);
        this.name = source.readString();
        this.licence = source.readString();
    }

    public String getName() {
        return name;
    }

    public String getLicence() {
        return licence;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLicence(String licence) {
        this.licence = licence;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.licence);
        /*dest.writeStringArray(new String[] {this.name, this.licence});*/
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        @Override
        public LibInfo createFromParcel(Parcel source) {
            return new LibInfo(source);
        }

        @Override
        public LibInfo[] newArray(int size) {
            return new LibInfo[size];
        }
    };
}
