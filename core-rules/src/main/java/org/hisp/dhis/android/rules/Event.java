package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class Event {

    // @NonNull
    public abstract String event();

    // enum EventStatus, @NonNull
    public abstract String status();

    // @NonNull
    public abstract String programStage();

    // @NonNull
    public abstract Date eventDate();

    // @NonNull
    public abstract Date dueDate();

    // @NonNull
    public abstract List<DataValue> dataValues();
}
