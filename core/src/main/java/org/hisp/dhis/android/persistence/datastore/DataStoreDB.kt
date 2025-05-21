package org.hisp.dhis.android.persistence.datastore

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.datastore.DataStoreEntry
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.DeletableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
import org.hisp.dhis.android.persistence.common.toDB

@Entity(
    tableName = "DataStore",
    indices = [
        Index(value = ["namespace", "key"], unique = true),
    ],
)
internal data class DataStoreDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val namespace: String,
    val key: String,
    val value: String?,
    override val syncState: SyncStateDB?,
    override val deleted: Boolean?,
) : EntityDB<DataStoreEntry>, DeletableObjectDB, DataObjectDB {

    override fun toDomain(): DataStoreEntry {
        return DataStoreEntry.builder()
            .id(id?.toLong())
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
