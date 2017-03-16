package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DataValue {

    // @NonNull
    public abstract String field();

    // @NonNull
    public abstract String value();

    public static DataValue create(String field, String value) {
        return new AutoValue_DataValue(field, value);
    }
}
