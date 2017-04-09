package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.Date;
import java.util.List;

@AutoValue
public abstract class RuleEnrollment {

    // @NonNull
    public abstract Date dateOfEnrollment();

    // @Nullable
    public abstract Date dateOfIncident();

    // enum // @NonNull
    public abstract String status();

    // @NonNull
    public abstract List<RuleEvent> events();

    // @NonNull
    public abstract List<RuleDataValue> trackedEntityAttributeValues();
}
