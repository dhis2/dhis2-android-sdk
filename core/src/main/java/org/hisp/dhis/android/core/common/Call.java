package org.hisp.dhis.android.core.common;

import java.util.concurrent.Callable;

public interface Call<T> extends Callable<T> {
    boolean isExecuted();
}
