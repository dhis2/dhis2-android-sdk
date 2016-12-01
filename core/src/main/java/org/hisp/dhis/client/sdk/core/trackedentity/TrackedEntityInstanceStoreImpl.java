package org.hisp.dhis.client.sdk.core.trackedentity;

import android.content.ContentResolver;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.core.commons.database.AbsDataStore;
import org.hisp.dhis.client.sdk.core.trackedentity.TrackedEntityInstanceTable.TrackedEntityInstanceColumns;
import org.hisp.dhis.client.sdk.models.trackedentity.TrackedEntityInstance;

import java.util.List;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class TrackedEntityInstanceStoreImpl extends AbsDataStore<TrackedEntityInstance> implements TrackedEntityInstanceStore {

    public TrackedEntityInstanceStoreImpl(ContentResolver contentResolver) {
        super(contentResolver, new TrackedEntityInstanceMapper());
    }

    @Override
    public List<TrackedEntityInstance> query(String organisationUnitUid) {
        isNull(organisationUnitUid, "organisationUnit uid must not be null");

        final String selection = TrackedEntityInstanceColumns.COLUMN_ORGANISATION_UNIT + " = ?";
        final String[] selectionArgs = new String[]{
                organisationUnitUid
        };

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModels(cursor);
    }

    @Override
    public TrackedEntityInstance queryByUid(String trackedEntityInstanceUid) {
        isNull(trackedEntityInstanceUid, "Tracked entity instance uid must not be null");

        final String selection = TrackedEntityInstanceColumns.COLUMN_UID + " = ?";
        final String[] selectionArgs = new String[]{
                trackedEntityInstanceUid
        };

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        if (noTrackedEntityIsFound(cursor)) {
            return null;
        }
        return toModel(cursor);
    }

    private boolean noTrackedEntityIsFound(Cursor cursor) {
        return cursor.getCount() == 0;
    }
}
