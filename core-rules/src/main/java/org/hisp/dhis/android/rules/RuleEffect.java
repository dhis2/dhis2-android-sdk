package org.hisp.dhis.android.rules;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class RuleEffect {

    public abstract String content();

    public abstract String location();

    public abstract String data();

    // package visible in order to avoid creation of RuleEffects outside engine
    static RuleEffect create(String content, String location, String data) {
        return new AutoValue_RuleEffect(content, location, data);
    }
}
