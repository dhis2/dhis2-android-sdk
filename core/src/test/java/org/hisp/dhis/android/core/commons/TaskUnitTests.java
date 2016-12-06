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

package org.hisp.dhis.android.core.commons;

import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class TaskUnitTests {
    private Executor currentThreadExecutor;

    @Before
    public void setUp() {
        /* for testing purposes */
        currentThreadExecutor = new Executor() {
            @Override
            public void execute(@NonNull Runnable runnable) {
                runnable.run();
            }
        };
    }

    @Test(expected = NullPointerException.class)
    public void execute_shouldThrowExceptionOnNullCallback() {
        Task<String> task = new TaskImpl<>(currentThreadExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "call()";
            }
        });

        task.execute(null);
    }

    @Test
    public void execute_shouldTriggerCallbackWithCorrectValues() {
        Task<String> task = new TaskImpl<>(currentThreadExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "call()";
            }
        });

        @SuppressWarnings("unchecked")
        Callback<String> callback = (Callback<String>) mock(Callback.class);

        // in real scenario, task should be
        // executed on another thread
        task.execute(callback);

        // capture the value which is being passed to callback
        verify(callback).onSuccess(task, "call()");
    }

    @Test
    public void execute_shouldPassExceptionToCallbackOnFailure() {
        final IllegalStateException illegalStateException =
                new IllegalStateException("Something went wrong");
        Task<String> task = new TaskImpl<>(currentThreadExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw illegalStateException;
            }
        });

        @SuppressWarnings("unchecked")
        Callback<String> callback = (Callback<String>) mock(Callback.class);

        // in real scenario, task should be
        // executed on another thread
        task.execute(callback);

        // capture the value which is being passed to callback
        verify(callback).onFailure(task, illegalStateException);
    }

    @Test
    public void execute_shouldExecuteRunnableOnExecutor() {
        Executor executor = mock(Executor.class);

        // task to be called
        Task<String> task = new TaskImpl<>(executor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "call()";
            }
        });

        @SuppressWarnings("unchecked")
        Callback<String> callback = (Callback<String>) mock(Callback.class);

        task.execute(callback);

        // executor service should be called only once
        verify(executor, times(1)).execute(any(Runnable.class));
    }

    @Test
    public void execute_shouldReturnCorrectResult() {
        // task to be called
        Task<String> task = new TaskImpl<>(mock(Executor.class), new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "call()";
            }
        });

        assertThat(task.execute()).isEqualTo("call()");
    }

    @Test(expected = RuntimeException.class)
    public void execute_shouldThrowOnFailure() {
        Task<String> task = new TaskImpl<>(currentThreadExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new IllegalStateException("Something went wrong");
            }
        });

        task.execute();
    }

    @Test(expected = IllegalStateException.class)
    public void execute_shouldThrowOnConsequentCalls() {
        Task<String> task = new TaskImpl<>(currentThreadExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "call()";
            }
        });

        task.execute();

        assertThat(task.isExecuted()).isTrue();

        // if task has been already executed,
        // it should throw an exception on second call
        task.execute();
    }

    @Test(expected = IllegalStateException.class)
    public void executeWithCallback_shouldThrowOnConsequentCalls() {
        Task<String> task = new TaskImpl<>(currentThreadExecutor, new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "call()";
            }
        });

        task.execute(new Callback<String>() {
            @Override
            public void onSuccess(Task<String> task, String result) {
                // stub
            }

            @Override
            public void onFailure(Task<String> task, Throwable throwable) {
                // stub
            }
        });

        assertThat(task.isExecuted()).isTrue();

        // if task has been already executed,
        // it should throw an exception on second call
        task.execute(new Callback<String>() {
            @Override
            public void onSuccess(Task<String> task, String result) {
                // stub
            }

            @Override
            public void onFailure(Task<String> task, Throwable throwable) {
                // stub
            }
        });
    }
}