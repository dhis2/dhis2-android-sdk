package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.relationship.Relationship;

import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.common.Utils.safeUnmodifiableList;

@AutoValue
public abstract class TrackedEntityInstanceModel extends BaseIdentifiableObjectModel {
    private static final String JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_UID = "trackedEntityInstance";
    private static final String JSON_PROPERTY_CREATED = "created";
    private static final String JSON_PROPERTY_LAST_UPDATED = "lastUpdated";
    private static final String JSON_PROPERTY_ORGANISATION_UNIT = "orgUnit";
    private static final String JSON_PROPERTY_ATTRIBUTES = "attributes";
    private static final String JSON_PROPERTY_RELATIONSHIPS = "relationships";

    public static TrackedEntityInstance create(Cursor cursor) {
        return null;//AutoValue_TrackedEntityInstance.from
    }

    public static Builder builder() {
        return new AutoValue_TrackedEntityInstanceModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentVaues();

    @ColumnName(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_UID)
    public abstract String uid();

    @Nullable
    @ColumnName(JSON_PROPERTY_CREATED)
    public abstract Date created();

    @Nullable
    @ColumnName(JSON_PROPERTY_LAST_UPDATED)
    public abstract Date lastUpdated();

    @ColumnName(JSON_PROPERTY_ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @ColumnName(JSON_PROPERTY_ATTRIBUTES)
    public abstract List<TrackedEntityAttributeValue> trackedEntityAttributeValues();

    @Nullable
    @ColumnName(JSON_PROPERTY_RELATIONSHIPS)
    public abstract List<Relationship> relationships();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {
        public abstract Builder uid(String uid);

        public abstract Builder created(@Nullable Date created);

        public abstract Builder lastUpdated(@Nullable Date lastUpdated);

        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder trackedEntityAttributeValues(
                List<TrackedEntityAttributeValue> trackedEntityAttributeValues);

        public abstract Builder relationships(@Nullable List<Relationship> relationships);

        abstract List<TrackedEntityAttributeValue> trackedEntityAttributeValues();

        abstract TrackedEntityInstance autoBuild();

        public TrackedEntityInstance build() {
            trackedEntityAttributeValues(safeUnmodifiableList(trackedEntityAttributeValues()));
            return autoBuild();
        }
    }
}
