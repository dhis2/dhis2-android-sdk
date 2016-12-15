package org.hisp.dhis.android.core.common;

import java.util.concurrent.Callable;

public interface Cancellable<T> extends Callable<T> {
}
