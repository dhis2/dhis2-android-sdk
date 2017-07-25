package org.hisp.dhis.rules.models;

import javax.annotation.Nonnull;

/*
 * ToDo - add support for next properties:
 *   1) Boolean useCode()
 *   2) List<Option> options()
 */
public abstract class RuleVariable {

    /**
     * @return Name of the variable. Something what users refer to
     * when building program rules.
     */
    @Nonnull
    public abstract String name();
}
