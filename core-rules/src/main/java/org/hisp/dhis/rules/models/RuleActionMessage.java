package org.hisp.dhis.rules.models;

import javax.annotation.Nonnull;

abstract class RuleActionMessage extends RuleAction {

    @Nonnull
    public abstract String content();

    @Nonnull
    public abstract String data();

    @Nonnull
    public abstract String field();
}
