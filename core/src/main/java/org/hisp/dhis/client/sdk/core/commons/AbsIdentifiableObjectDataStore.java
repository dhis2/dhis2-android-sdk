package org.hisp.dhis.client.sdk.core.commons;

import android.content.ContentResolver;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.models.common.DataModel;
import org.hisp.dhis.client.sdk.models.common.IdentifiableObject;

public class AbsIdentifiableObjectDataStore<T extends IdentifiableObject & DataModel> extends AbsDataStore<T> implements IdentifiableObjectDataStore<T> {

    public AbsIdentifiableObjectDataStore(ContentResolver contentResolver, Mapper<T> mapper) {
        super(contentResolver, mapper);
    }

    @Override
    public T queryByUid(String uid) {
        if (uid == null) {
            throw new IllegalArgumentException("uid must not be null");
        }

        final String[] selectionArgs = new String[]{uid};
        final String selection = DbContract.IdentifiableColumns.COLUMN_UID + " = ?";

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModel(cursor);
    }

    @Override
    public T queryByCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("code must not be null");
        }

        final String[] selectionArgs = new String[]{code};
        final String selection = DbContract.IdentifiableColumns.COLUMN_CODE + " = ?";

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModel(cursor);
    }
}
