/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.core;

import java.io.IOException;
import java.util.concurrent.Executor;

abstract class AbsTask<T> implements Task<T> {
    private final Executor executor;
    private final Executor callbackExecutor;

    private boolean isExecuted;
    private boolean isCanceled;

    AbsTask(Executor executor, Executor callbackExecutor) {
        this.executor = executor;
        this.callbackExecutor = callbackExecutor;
    }

    @Override
    public T execute() throws IOException {
        if (isExecuted()) {
            throw new IllegalStateException("Task has been already executed");
        }

        /* mark task as executed */
        setExecuted(true);

        return executeTask();
    }

    @Override
    public void enqueue(final Callback<T> callback) {
        if (callback == null) {
            throw new NullPointerException("callback == null");
        }

        if (isExecuted()) {
            throw new IllegalStateException("Task has been already executed");
        }

        /* mark task as executed */
        setExecuted(true);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final T result = executeTask();
                    if (callbackExecutor != null) {
                        callbackExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(AbsTask.this, result);
                            }
                        });
                    } else {
                        callback.onSuccess(AbsTask.this, result);
                    }
                } catch (final Throwable throwable) {
                    if (callbackExecutor != null) {
                        callbackExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(AbsTask.this, throwable);
                            }
                        });
                    } else {
                        callback.onFailure(AbsTask.this, throwable);
                    }
                }
            }
        });
    }

    @Override
    public void cancel() {
        if (!isExecuted() && !isCanceled()) {
            /* mark as cancelled */
            setCanceled(true);

            cancelTask();
        }
    }

    @Override
    public synchronized boolean isExecuted() {
        return isExecuted;
    }

    @Override
    public synchronized boolean isCanceled() {
        return isCanceled;
    }

    private synchronized void setExecuted(boolean isExecuted) {
        this.isExecuted = isExecuted;
    }

    private synchronized void setCanceled(boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    protected abstract T executeTask() throws IOException;

    protected abstract void cancelTask();
}
