package org.hisp.dhis.android.rules.models;

import com.google.auto.value.AutoValue;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class Enrollment {

    // @NonNull
    public abstract Date dateOfEnrollment();

    // @Nullable
    public abstract Date dateOfIncident();

    // enum // @NonNull
    public abstract String status();

    // @NonNull
    public abstract List<Event> events();

    // @NonNull
    public abstract List<TrackedEntityDataValue> trackedEntityAttributeValues();
}
