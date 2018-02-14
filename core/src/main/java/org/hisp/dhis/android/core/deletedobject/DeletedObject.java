package org.hisp.dhis.android.core.deletedobject;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;

import java.util.Date;

@AutoValue
public abstract class DeletedObject {
    protected static final String UID = "uid";
    protected static final String KLASS = "klass";
    protected static final String DELETED_AT = "deletedAt";
    protected static final String DELETED_BY = "deletedBy";
    public static final Field<DeletedObject, String> uid = Field.create(UID);
    public static final Field<DeletedObject, String> klass = Field.create(KLASS);
    public static final Field<DeletedObject, String> deletedAt = Field.create(DELETED_AT);
    public static final Field<DeletedObject, String> deletedBy = Field.create(DELETED_BY);

    @JsonProperty(UID)
    public abstract String uid();

    @Nullable
    @JsonProperty(KLASS)
    public abstract String klass();

    @Nullable
    @JsonProperty(DELETED_AT)
    public abstract Date deletedAt();

    @Nullable
    @JsonProperty(DELETED_BY)
    public abstract String deletedBy();


    @JsonCreator
    public static DeletedObject create(
            @JsonProperty(UID) String uid,
            @JsonProperty(KLASS) String klass,
            @JsonProperty(DELETED_AT) Date deletedAt,
            @JsonProperty(DELETED_BY) String deletedBy) {

        return new AutoValue_DeletedObject(
                uid,
                klass,
                deletedAt,
                deletedBy);
    }
}
