package org.hisp.dhis.android.persistence.sms

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.datastore.KeyValuePair
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "SMSConfig",
    indices = [
        Index(value = ["key"], unique = true),
    ],
)
internal data class SMSConfigDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val key: String,
    val value: String?,
) : EntityDB<KeyValuePair> {
    override fun toDomain(): KeyValuePair {
        return KeyValuePair.builder()
            .id(id?.toLong())
            .key(key)
            .value(value)
            .build()
    }
}

internal fun KeyValuePair.toDB(): SMSConfigDB {
    return SMSConfigDB(
        key = key()!!,
        value = value(),
    )
}