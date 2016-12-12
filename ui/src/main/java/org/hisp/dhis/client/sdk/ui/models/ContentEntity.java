package org.hisp.dhis.client.sdk.ui.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ContentEntity implements Parcelable {
    private final String id;
    private final String title;
    private final String type;

    // types
    public static final String TYPE_TRACKED_ENTITY = "trackedEntity";
    public static final String TYPE_PROGRAM = "program";
    public static final String TYPE_DATA_SET = "dataSet";

    public ContentEntity(String id, String title, String type) {
        this.id = id;
        this.title = title;
        this.type = type;
    }

    protected ContentEntity(Parcel in) {
        id = in.readString();
        title = in.readString();
        type = in.readString();
    }

    public static final Creator<ContentEntity> CREATOR = new Creator<ContentEntity>() {
        @Override
        public ContentEntity createFromParcel(Parcel in) {
            return new ContentEntity(in);
        }

        @Override
        public ContentEntity[] newArray(int size) {
            return new ContentEntity[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(type);
    }
}
