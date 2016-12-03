package org.hisp.dhis.android.core.commons;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface ContentResolverWrapper {

    @NonNull
    Single<Cursor> query(@NonNull Uri uri, @NonNull Query query);

    @NonNull
    Observable<Cursor> query(@NonNull Uri uri, @NonNull Query query, boolean trackChanges);
}
