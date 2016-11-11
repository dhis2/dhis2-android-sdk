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

import android.content.ContentResolver;
import android.test.ProviderTestCase2;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.commons.CursorAssert.assertThatCursor;
import static org.hisp.dhis.android.core.commons.TestContentProvider.TABLE;


// TODO: add tests for use cases of resolver in conjunction with specific URIs
public class UpdateQueryResolverUnitTests extends ProviderTestCase2<TestContentProvider> {
    // content resolver which is used to interact with database
    private ContentResolver contentResolver;

    // instance to test
    private WriteQueryResolver<Integer> contentValuesWriteQueryResolver;
    private WriteQueryResolver<Integer> modelValuesWriteQueryResolver;
    private Disposable subscription;

    public UpdateQueryResolverUnitTests() {
        super(TestContentProvider.class, TestContentProvider.AUTHORITY.getAuthority());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        /* for testing purposes */
        Executor executor = new Executor() {
            @Override
            public void execute(@Nonnull Runnable runnable) {
                runnable.run();
            }
        };

        // ProviderTestCase2 supplies mocked content resolver
        contentResolver = getMockContentResolver();
        subscription = Disposables.empty();

        // passing mock / concrete fields to mocked provider
        getProvider().init(contentResolver);

        contentValuesWriteQueryResolver = new UpdateQueryResolver<>(executor, contentResolver,
                TABLE, TestModel.values(11L, "anotherValue"));
        modelValuesWriteQueryResolver = new UpdateQueryResolver<>(executor, contentResolver,
                TABLE, Where.builder().build(), new TestMapper(), new TestModel(11L, "anotherValue"));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        subscription.dispose();
    }

    public void testAsTaskExecuteOnValues_shouldCallContentResolver() {
        // insert values which have to be updated later
        contentResolver.insert(TABLE, TestModel.values(11L, "someValue"));

        // count of update rows returned from content provider
        Integer updatedCount = contentValuesWriteQueryResolver.asTask().execute();

        // check that content provider returns correct number of updated rows
        assertThat(updatedCount).isEqualTo(1);

        // check that content provider contains updated item
        assertThatCursor(contentResolver.query(TABLE,
                null, null, null, null)).hasRow(11L, "anotherValue").isExhausted();
    }

    public void testAsTaskExecuteOnModel_shouldCallContentResolver() {
        // count of update rows returned from content provider
        Integer updatedCount = modelValuesWriteQueryResolver.asTask().execute();

        // check that content provider returns correct number of updated rows
        assertThat(updatedCount).isEqualTo(1);

        // check that content provider contains updated item
        assertThatCursor(contentResolver.query(TABLE,
                null, null, null, null)).hasRow(11L, "anotherValue").isExhausted();
    }

    public void testAsTaskExecuteOnValuesAsynchronously_shouldCallContentResolver() {
        contentValuesWriteQueryResolver.asTask()
                .execute(new Callback<Integer>() {
                    @Override
                    public void onSuccess(Task<Integer> task, Integer updatedCount) {
                        // check that content provider returns correct number of updated rows
                        assertThat(updatedCount).isEqualTo(1);

                        // check that content provider contains updated item
                        assertThatCursor(contentResolver.query(TestContentProvider.TABLE,
                                null, null, null, null)).hasRow(11L, "anotherValue").isExhausted();
                    }

                    @Override
                    public void onFailure(Task<Integer> task, Throwable throwable) {
                        fail("onFailure() should not be called");
                    }
                });
    }

    public void testAsTaskExecuteOnModelAsynchronously_shouldCallContentResolver() {
        modelValuesWriteQueryResolver.asTask()
                .execute(new Callback<Integer>() {
                    @Override
                    public void onSuccess(Task<Integer> task, Integer updatedCount) {
                        // check that content provider returns correct number of updated rows
                        assertThat(updatedCount).isEqualTo(1);

                        // check that content provider contains updated item
                        assertThatCursor(contentResolver.query(TestContentProvider.TABLE,
                                null, null, null, null)).hasRow(11L, "anotherValue").isExhausted();
                    }

                    @Override
                    public void onFailure(Task<Integer> task, Throwable throwable) {
                        fail("onFailure() should not be called");
                    }
                });
    }

    public void testAsSingleOnValues_shouldCallContentResolverOnSubscribe() {
        // without any schedulers set, single should be executed on current thread
        subscription = contentValuesWriteQueryResolver.asSingle()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer updatedCount) throws Exception {
                        // check that content provider returns correct number of updated rows
                        assertThat(updatedCount).isEqualTo(1);

                        // check that content provider contains updated item
                        assertThatCursor(contentResolver.query(TestContentProvider.TABLE,
                                null, null, null, null)).hasRow(11L, "anotherValue").isExhausted();
                    }
                });
    }

    public void testAsSingleOnModel_shouldCallContentResolverOnSubscribe() {
        // without any schedulers set, single should be executed on current thread
        subscription = modelValuesWriteQueryResolver.asSingle()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer updatedCount) throws Exception {
                        // check that content provider returns correct number of updated rows
                        assertThat(updatedCount).isEqualTo(1);

                        // check that content provider contains updated item
                        assertThatCursor(contentResolver.query(TestContentProvider.TABLE,
                                null, null, null, null)).hasRow(11L, "anotherValue").isExhausted();
                    }
                });
    }
}
