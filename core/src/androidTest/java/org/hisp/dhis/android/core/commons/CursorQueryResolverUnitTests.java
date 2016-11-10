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
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentProvider;

import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.annotation.Nonnull;

import io.reactivex.functions.Consumer;
import rx.schedulers.Schedulers;

import static org.hisp.dhis.android.core.commons.CursorAssert.assertThatCursor;

public class CursorQueryResolverUnitTests extends ProviderTestCase2<CursorQueryResolverUnitTests.TestContentProvider> {
    private static final Uri AUTHORITY = Uri.parse("content://test_authority");
    private static final Uri TABLE = AUTHORITY.buildUpon().appendPath("test_table").build();

    // projection
    private static final String KEY = "test_key";
    private static final String VALUE = "test_value";

    // content resolver which is used to interact with database
    private ContentResolver contentResolver;

    // instance to test
    private ReadQueryResolver<Cursor> cursorReadQueryResolver;
    private RecordingObserver recordingObserver;

    public CursorQueryResolverUnitTests() {
        super(TestContentProvider.class, AUTHORITY.getAuthority());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        /* for testing purposes */
        Executor currentThreadExecutor = new Executor() {
            @Override
            public void execute(@Nonnull Runnable runnable) {
                runnable.run();
            }
        };

        // ProviderTestCase2 supplies mocked content resolver
        contentResolver = getMockContentResolver();
        recordingObserver = new RecordingObserver();

        // concrete implementation of SqlBrite's content provider
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        BriteContentResolver briteContentResolver = sqlBrite.wrapContentProvider(
                contentResolver, Schedulers.immediate());

        // passing mock / concrete fields to mocked provider
        getProvider().init(contentResolver);

        cursorReadQueryResolver = new CursorQueryResolver(currentThreadExecutor,
                briteContentResolver, contentResolver, TABLE, Query.builder().build());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();

        recordingObserver.assertNoMoreEvents();
        recordingObserver.dispose();
    }

    public void testAsTaskExecute_shouldCallContentResolver() {
        // insert dummy data to database
        contentResolver.insert(TABLE, values("valueOne", "valueTwo"));

        Cursor cursor = cursorReadQueryResolver.asTask().execute();

        assertThatCursor(cursor).hasRow("valueOne", "valueTwo").isExhausted();
    }

    public void testAsTaskExecuteAsynchronously_shouldCallContentResolver() {
        // insert dummy data to database
        contentResolver.insert(TABLE, values("valueOne", "valueTwo"));

        cursorReadQueryResolver.asTask().execute(new Callback<Cursor>() {
            @Override
            public void onSuccess(Task<Cursor> task, Cursor cursor) {
                assertThatCursor(cursor).hasRow("valueOne", "valueTwo").isExhausted();
            }

            @Override
            public void onFailure(Task<Cursor> task, Throwable throwable) {
                fail("onFailure() should not be called");
            }
        });
    }

    public void testAsSingle_shouldCallContentResolverOnSubscribe() {
        // insert dummy data to database
        contentResolver.insert(TABLE, values("valueOne", "valueTwo"));

        // without any schedulers set, single should be
        // executed on current thread
        cursorReadQueryResolver.asSingle()
                .subscribe(new Consumer<Cursor>() {
                    @Override
                    public void accept(Cursor cursor) throws Exception {
                        assertThatCursor(cursor).hasRow("valueOne", "valueTwo").isExhausted();
                    }
                });
    }

    public void testAsObservable_shouldCallContentResolverOnSubscribe() {
        // insert dummy data to database
        contentResolver.insert(TABLE, values("valueOne", "valueTwo"));

        cursorReadQueryResolver.asObservable().subscribe(recordingObserver);
        recordingObserver.assertCursor().hasRow("valueOne", "valueTwo").isExhausted();
    }

    private static ContentValues values(String valueOne, String valueTwo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY, valueOne);
        contentValues.put(VALUE, valueTwo);
        return contentValues;
    }

    public static final class TestContentProvider extends MockContentProvider {
        // faking database behaviour
        private Map<String, String> storage;
        private ContentResolver contentResolver;

        void init(ContentResolver contentResolver) {
            this.storage = new HashMap<>();
            this.contentResolver = contentResolver;
        }

        @Override
        public Uri insert(Uri uri, ContentValues values) {
            storage.put(values.getAsString(KEY), values.getAsString(VALUE));
            contentResolver.notifyChange(uri, null);
            return Uri.parse(TABLE + "/" + values.getAsString(KEY));
        }

        @Override
        public Cursor query(Uri uri, String[] projection, String selection,
                String[] selectionArgs, String sortOrder) {

            MatrixCursor result = new MatrixCursor(new String[]{
                    KEY, VALUE
            });

            for (Map.Entry<String, String> entry : storage.entrySet()) {
                result.addRow(new String[]{
                        entry.getKey(), entry.getValue()
                });
            }

            return result;
        }
    }
}
