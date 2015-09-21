package org.hisp.dhis.android.sdk.models.common.meta;

public interface IDbOperation<T> {
    public T getModel();
    public DbAction getAction();
    public void execute();
}
