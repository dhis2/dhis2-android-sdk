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

public class InsertQueryResolverUnitTests extends ProviderTestCase2<FakeContentProvider> {
    // content resolver which is used to interact with database
    private ContentResolver contentResolver;

    // instance to test
    private WriteQueryResolver<Long> contentValuesWriteQueryResolver;
    private WriteQueryResolver<Long> modelValuesWriteQueryResolver;
    private Disposable subscription;

    public InsertQueryResolverUnitTests() {
        super(FakeContentProvider.class, FakeContentProvider.AUTHORITY.getAuthority());
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

        contentValuesWriteQueryResolver = new InsertQueryResolver<>(executor, contentResolver,
                FakeContentProvider.TABLE, TestModel.values(11L, "valueTwo"));
        modelValuesWriteQueryResolver = new InsertQueryResolver<>(executor, contentResolver,
                FakeContentProvider.TABLE, new TestMapper(), new TestModel(11L, "valueTwo"));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        subscription.dispose();
    }

    public void testAsTaskExecuteOnValues_shouldCallContentResolver() {
        Long modelId = contentValuesWriteQueryResolver.asTask().execute();

        // check that content provider returns specified id
        assertThat(modelId).isEqualTo(11L);

        // check that content provider contains inserted item
        // and values are matching original ones
        assertThatCursor(contentResolver.query(FakeContentProvider.TABLE,
                null, null, null, null)).hasRow(11L, "valueTwo").isExhausted();
    }

    public void testAsTaskExecuteOnModel_shouldCallContentResolver() {
        Long modelId = modelValuesWriteQueryResolver.asTask().execute();

        // check that content provider returns specified id
        assertThat(modelId).isEqualTo(11L);

        // check that content provider contains inserted item
        // and values are matching original ones
        assertThatCursor(contentResolver.query(FakeContentProvider.TABLE,
                null, null, null, null)).hasRow(11L, "valueTwo").isExhausted();
    }

    public void testAsTaskExecuteOnValuesAsynchronously_shouldCallContentResolver() {
        contentValuesWriteQueryResolver.asTask()
                .execute(new Callback<Long>() {
                    @Override
                    public void onSuccess(Task<Long> task, Long modelId) {
                        // check that content provider returns specified id
                        assertThat(modelId).isEqualTo(11L);

                        // check that content provider contains inserted item
                        // and values are matching original ones
                        assertThatCursor(contentResolver.query(FakeContentProvider.TABLE,
                                null, null, null, null)).hasRow(11L, "valueTwo").isExhausted();
                    }

                    @Override
                    public void onFailure(Task<Long> task, Throwable throwable) {
                        fail("onFailure() should not be called");
                    }
                });
    }

    public void testAsTaskExecuteOnModelAsynchronously_shouldCallContentResolver() {
        modelValuesWriteQueryResolver.asTask()
                .execute(new Callback<Long>() {
                    @Override
                    public void onSuccess(Task<Long> task, Long modelId) {
                        // check that content provider returns specified id
                        assertThat(modelId).isEqualTo(11L);

                        // check that content provider contains inserted item
                        // and values are matching original ones
                        assertThatCursor(contentResolver.query(FakeContentProvider.TABLE,
                                null, null, null, null)).hasRow(11L, "valueTwo").isExhausted();
                    }

                    @Override
                    public void onFailure(Task<Long> task, Throwable throwable) {
                        fail("onFailure() should not be called");
                    }
                });
    }

    public void testAsSingleOnValues_shouldCallContentResolverOnSubscribe() {
        // without any schedulers set, single should be
        // executed on current thread
        subscription = contentValuesWriteQueryResolver.asSingle()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long modelId) throws Exception {
                        // check that content provider returns specified id
                        assertThat(modelId).isEqualTo(11L);

                        // check that content provider contains inserted item
                        // and values are matching original ones
                        assertThatCursor(contentResolver.query(FakeContentProvider.TABLE,
                                null, null, null, null)).hasRow(11L, "valueTwo").isExhausted();
                    }
                });
    }

    public void testAsSingleOnModel_shouldCallContentResolverOnSubscribe() {
        // without any schedulers set, single should be
        // executed on current thread
        subscription = modelValuesWriteQueryResolver.asSingle()
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long modelId) throws Exception {
                        // check that content provider returns specified id
                        assertThat(modelId).isEqualTo(11L);

                        // check that content provider contains inserted item
                        // and values are matching original ones
                        assertThatCursor(contentResolver.query(FakeContentProvider.TABLE,
                                null, null, null, null)).hasRow(11L, "valueTwo").isExhausted();
                    }
                });
    }
}
