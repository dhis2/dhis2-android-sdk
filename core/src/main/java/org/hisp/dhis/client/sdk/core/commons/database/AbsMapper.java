package org.hisp.dhis.client.sdk.core.commons.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.models.common.Model;

import java.util.List;

public abstract class AbsMapper<ModelType extends Model> implements Mapper<ModelType> {

    @Override
    public abstract  Uri getContentUri();

    @Override
    public abstract Uri getContentItemUri(long id);

    @Override
    public abstract String[] getProjection();

    @Override
    public abstract ContentValues toContentValues(ModelType model);

    @Override
    public ContentValues[] toContentValues(List<ModelType> models) {
        if(models.size() <= 0) {
            throw new IllegalArgumentException("Models to convert is 0");
        }

        ContentValues[] contentValues = new ContentValues[models.size()];

        for (int i = 0; i < models.size(); i++) {
            contentValues[i] = toContentValues(models.get(i));
        }

        return contentValues;
    }

    @Override
    public abstract ModelType toModel(Cursor cursor);
}
