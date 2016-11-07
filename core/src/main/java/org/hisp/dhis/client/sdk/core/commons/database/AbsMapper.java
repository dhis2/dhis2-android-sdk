package org.hisp.dhis.client.sdk.core.commons.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.hisp.dhis.client.sdk.models.common.IdentifiableObject;
import org.hisp.dhis.client.sdk.models.common.Model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

public abstract class AbsMapper<ModelType extends Model> implements Mapper<ModelType> {
    protected static final int COLUMN_CHARACTER_LIMIT = 1000000;

    @Override
    public abstract Uri getContentUri();

    @Override
    public abstract Uri getContentItemUri(long id);

    @Override
    public abstract String[] getProjection();

    @Override
    public abstract ContentValues toContentValues(ModelType model) throws IOException;

    @Override
    public ContentValues[] toContentValues(List<ModelType> models) throws IOException {
        if (models.size() <= 0) {
            throw new IllegalArgumentException("Models to convert is 0");
        }

        ContentValues[] contentValues = new ContentValues[models.size()];

        for (int i = 0; i < models.size(); i++) {
            contentValues[i] = toContentValues(models.get(i));
        }

        return contentValues;
    }

    protected <T extends IdentifiableObject> String saveBlobToFile(Context context, Class<T> clazz, T object, String jsonBlob) throws IOException {
//        File fileBlobDir = context.getDir(context.getString(R.string.dir_json_blobs), Context.MODE_PRIVATE);
        File rootObjectDir = context.getDir(clazz.getSimpleName(), Context.MODE_PRIVATE);

        File jsonBlobFile = new File(rootObjectDir, object.uid());

        FileOutputStream fileOutputStream = new FileOutputStream(jsonBlobFile);

        Writer writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
        try {
            writer.write(jsonBlob);
        } catch (IOException e) {
            //something went wrong. Delete this file
            jsonBlobFile.delete();
            throw e;
        } finally {
            writer.close();
        }

        return jsonBlobFile.getPath();
    }

    protected <T extends IdentifiableObject> String readFromBlobFile(Context context, Class<T> clazz, String uid) throws IOException {
        File rootObjectDir = context.getDir(clazz.getSimpleName(), Context.MODE_PRIVATE);
        File jsonBlobFile = new File(rootObjectDir, uid);
        StringBuilder stringBuilder = new StringBuilder();
        if (jsonBlobFile.exists()) {
            FileInputStream fileInputStream = new FileInputStream(jsonBlobFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
        }
        return stringBuilder.toString();
    }

    @Override
    public abstract ModelType toModel(Cursor cursor);
}
