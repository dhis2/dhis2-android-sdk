package org.hisp.dhis.client.sdk.ui.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class FormEntityText extends FormEntityCharSequence implements Parcelable {

    public FormEntityText(String id, String label) {
        this(id, label, null);
    }

    public FormEntityText(String id, String label, Object tag) {
        super(id, label, tag);
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.TEXT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getLabel());
        dest.writeString(getValue().toString());
    }

    public static final Parcelable.Creator<FormEntityText> CREATOR
            = new Parcelable.Creator<FormEntityText>() {
        public FormEntityText createFromParcel(Parcel in) {

            FormEntityText formEntityText = new FormEntityText(in.readString(), in.readString());
            formEntityText.setValue(in.readString(), false);
            return formEntityText;
        }

        public FormEntityText[] newArray(int size) {
            return new FormEntityText[size];
        }
    };
}
