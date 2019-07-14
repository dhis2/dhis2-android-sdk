package org.hisp.dhis.android.core.sms.data.localdbrepository;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.hisp.dhis.smscompression.models.SMSMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.Single;

class MetadataIdsStore {
    private final static String METADATA_FILE = "metadata_ids";
    private final Context context;

    MetadataIdsStore(Context context) {
        this.context = context;
    }

    Single<SMSMetadata> getMetadataIds() {
        return Single.fromCallable(() -> {
                    InputStream is = null;
                    try {
                        is = context.openFileInput(METADATA_FILE);
                        JsonReader reader = new JsonReader(new InputStreamReader(is));
                        return getGson().fromJson(reader, SMSMetadata.class);
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
        );
    }

    Completable setMetadataIds(final SMSMetadata metadata) {
        return Completable.fromAction(() -> {
                    OutputStream fos = context.openFileOutput(METADATA_FILE, Context.MODE_PRIVATE);
                    JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos));
                    getGson().toJson(metadata, SMSMetadata.class, writer);
                    writer.flush();
                    writer.close();
                }
        );
    }

    private Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateLongFormatTypeAdapter())
                .create();
    }

    class DateLongFormatTypeAdapter extends TypeAdapter<Date> {

        @Override
        public void write(JsonWriter out, Date value) throws IOException {
            if (value != null) out.value(value.getTime());
            else out.nullValue();
        }

        @Override
        public Date read(JsonReader in) throws IOException {
            return new Date(in.nextLong());
        }
    }
}
