package org.hisp.dhis.android.core.relationship;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

@AutoValue
public abstract class RelationshipTypeModel extends BaseIdentifiableObjectModel {

    private static final String JSON_PROPERTY_B_TO_A = "bIsToA";
    private static final String JSON_PROPERTY_A_TO_B = "aIsToB";

    public static RelationshipTypeModel create(Cursor cursor) {
        return AutoValue_RelationshipTypeModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_RelationshipTypeModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(RelationshipTypeContract.Columns.B_IS_TO_A)
    public abstract String bIsToA();

    @Nullable
    @ColumnName(RelationshipTypeContract.Columns.A_IS_TO_B)
    public abstract String aIsToB();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder bIsToA(@Nullable String bIsToA);

        public abstract Builder aIsToB(@Nullable String aIsToB);

        public abstract RelationshipTypeModel build();
    }
}
