package org.hisp.dhis.client.sdk.ui.views.chainablepickerview;

import android.os.Parcel;

public class Pickable implements IPickable {

    String id;
    String label;

    public Pickable(String label, String id) {
        this.label = label;
        this.id = id;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{id, label});
    }

    public Pickable(Parcel in) {
        String[] data = new String[2];
        in.readStringArray(data);
        this.id = data[0];
        this.label = data[1];
    }

    public static final Creator<Pickable> CREATOR = new Creator<Pickable>() {
        @Override
        public Pickable createFromParcel(Parcel in) {
            return new Pickable(in);
        }

        @Override
        public Pickable[] newArray(int size) {
            return new Pickable[size];
        }
    };
}
