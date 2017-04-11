package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

import java.util.Date;
import java.util.List;

import javax.annotation.Nonnull;

@AutoValue
public abstract class RuleEnrollment {

    @Nonnull
    public abstract String enrollment();

    @Nonnull
    public abstract Date dateOfEnrollment();

    @Nonnull
    public abstract Date dateOfIncident();

    // enum // @NonNull
    public abstract String status();

    @Nonnull
    public abstract List<RuleAttributeValue> trackedEntityAttributeValues();
}
