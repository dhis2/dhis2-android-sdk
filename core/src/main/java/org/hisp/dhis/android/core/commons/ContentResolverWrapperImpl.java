package org.hisp.dhis.android.core.commons;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import java.util.concurrent.Callable;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

public class ContentResolverWrapperImpl implements ContentResolverWrapper {
    private final ContentResolver contentResolver;
    private final BriteContentResolver briteContentResolver;

    public ContentResolverWrapperImpl(ContentResolver contentResolver,
            BriteContentResolver briteContentResolver) {
        this.contentResolver = contentResolver;
        this.briteContentResolver = briteContentResolver;
    }

    @NonNull
    @Override
    public Single<Cursor> query(@NonNull Uri uri, @NonNull Query query) {
        Cursor queryCursor = contentResolver.query(uri, query.projection(),
                query.selection(), query.selectionArgs(), query.sortOrder());

        if (queryCursor != null) {
            return Single.just(queryCursor);
        }

        return Single.fromCallable(new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                return null;
            }
        });
    }

    @NonNull
    @Override
    public Observable<Cursor> query(@NonNull Uri uri, @NonNull Query query, boolean trackChanges) {
        return RxJavaInterop.toV2Observable(briteContentResolver.createQuery(uri, query.projection(),
                query.selection(), query.selectionArgs(), query.sortOrder(), trackChanges))
                .map(new Function<SqlBrite.Query, Cursor>() {
                    @Override
                    public Cursor apply(SqlBrite.Query query) throws Exception {
                        return query.run();
                    }
                });
    }
}
