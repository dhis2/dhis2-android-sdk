package org.hisp.dhis.android.persistence.datastore

import androidx.room.Entity
import org.hisp.dhis.android.core.datastore.DataStoreEntry
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.DeletableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
import org.hisp.dhis.android.persistence.common.toDB

@Entity(
    tableName = "DataStore",
    primaryKeys = ["namespace", "key"],
)
internal data class DataStoreDB(
    val namespace: String,
    val key: String,
    val value: String?,
    override val syncState: SyncStateDB?,
    override val deleted: Boolean?,
) : EntityDB<DataStoreEntry>, DeletableObjectDB, DataObjectDB {

    override fun toDomain(): DataStoreEntry {
        return DataStoreEntry.builder()
            .namespace(namespace)
            .key(key)
            .value(value)
            .syncState(syncState?.toDomain())
            .deleted(deleted)
            .build()
    }
}

internal fun DataStoreEntry.toDB(): DataStoreDB {
    return DataStoreDB(
        namespace = namespace(),
        key = key(),
        value = value(),
        syncState = syncState()?.toDB(),
        deleted = deleted(),
    )
}
