package org.hisp.dhis.android.core.sms.data.localdbrepository;

import android.content.Context;

import org.hisp.dhis.android.core.ObjectMapperFactory;
import org.hisp.dhis.smscompression.models.Metadata;

import io.reactivex.Completable;
import io.reactivex.Single;

class MetadataIdsStore {
    private final static String METADATA_FILE = "metadata_ids";
    private final Context context;

    MetadataIdsStore(Context context) {
        this.context = context;
    }

    Single<Metadata> getMetadataIds() {
        return Single.fromCallable(() ->
                ObjectMapperFactory.objectMapper().readValue(
                        context.openFileInput(METADATA_FILE), Metadata.class
                ));
    }

    Completable setMetadataIds(final Metadata metadata) {
        return Completable.fromAction(() ->
                ObjectMapperFactory.objectMapper().writeValue(
                        context.openFileOutput(METADATA_FILE, Context.MODE_PRIVATE), metadata
                ));
    }
}
