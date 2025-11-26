package org.hisp.dhis.android.persistence.datastore

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.datastore.KeyValuePair
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "LocalDataStore")
internal data class LocalDataStoreDB(
    @PrimaryKey
    val key: String,
    val value: String?,
) : EntityDB<KeyValuePair> {

    override fun toDomain(): KeyValuePair {
        return KeyValuePair.builder()
            .key(key)
            .value(value)
            .build()
    }
}

internal fun KeyValuePair.toDB(): LocalDataStoreDB {
    return LocalDataStoreDB(
        key = key()!!,
        value = value(),
    )
}
