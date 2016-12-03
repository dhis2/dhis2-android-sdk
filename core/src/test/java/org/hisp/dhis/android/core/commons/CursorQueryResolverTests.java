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

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CursorQueryResolverTests {
    // private static final Uri AUTHORITY = Uri.parse("content://test_authority");
    // private static final Uri TABLE = AUTHORITY.buildUpon().appendPath("test_table").build();

    @Mock
    ContentResolverWrapper contentResolverWrapper;

    @Mock
    Cursor cursor;

    @Mock
    Uri uri;

    // instance to test
    private ReadQueryResolver<Cursor> cursorReadQueryResolver;

    // private RecordingObserver recordingObserver;
    private Disposable subscription;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        /* for testing purposes */
        Executor executor = new Executor() {
            @Override
            public void execute(@Nonnull Runnable runnable) {
                runnable.run();
            }
        };


        // recordingObserver = new RecordingObserver();
        subscription = Disposables.empty();

        // concrete instance
        cursorReadQueryResolver = new CursorQueryResolver2(executor, contentResolverWrapper,
                uri, Query.builder().build());
    }

    @After
    public void tearDown() throws Exception {
        // recordingObserver.assertNoMoreEvents();
        // recordingObserver.dispose();
        // subscription.dispose();
    }

    @Test
    public void testAsTaskExecute_shouldCallContentResolver() {
        // Query query = Query.builder().build();
        when(contentResolverWrapper.query(any(Uri.class), any(Query.class)))
                .thenReturn(Single.just(cursor));

        cursorReadQueryResolver.asTask().execute();

        verify(contentResolverWrapper, times(1)).query(any(Uri.class), any(Query.class));
    }

    @Test
    public void testContentValues() {
        // ContentValues contentValues = mock(ContentValues.class);
        ContentValues contentValues = mock(ContentValues.class);
        contentValues.put("one", "two");
    }
//
//    public void testAsTaskExecuteAsynchronously_shouldCallContentResolver() {
//        // insert dummy data to database
//        contentResolver.insert(FakeContentProvider.TABLE,
//                TestModel.values(11L, "valueTwo"));
//
//        cursorReadQueryResolver.asTask()
//                .execute(new Callback<Cursor>() {
//                    @Override
//                    public void onSuccess(Task<Cursor> task, Cursor cursor) {
//                        assertThatCursor(cursor).hasRow(11L, "valueTwo").isExhausted();
//                    }
//
//                    @Override
//                    public void onFailure(Task<Cursor> task, Throwable throwable) {
//                        fail("onFailure() should not be called");
//                    }
//                });
//    }
//
//    public void testAsSingle_shouldCallContentResolverOnSubscribe() {
//        // insert dummy data to database
//        contentResolver.insert(FakeContentProvider.TABLE,
//                TestModel.values(11L, "valueTwo"));
//
//        // without any schedulers set, single should be
//        // executed on current thread
//        subscription = cursorReadQueryResolver.asSingle()
//                .subscribe(new Consumer<Cursor>() {
//                    @Override
//                    public void accept(Cursor cursor) throws Exception {
//                        assertThatCursor(cursor).hasRow(11L, "valueTwo").isExhausted();
//                    }
//                });
//    }
//
//    public void testAsObservable_shouldCallContentResolverOnSubscribe() {
//        // insert dummy data to database
//        contentResolver.insert(FakeContentProvider.TABLE,
//                TestModel.values(11L, "valueTwo"));
//
//        cursorReadQueryResolver.asObservable().subscribe(recordingObserver);
//        recordingObserver.assertCursor().hasRow(11L, "valueTwo").isExhausted();
//    }
}
