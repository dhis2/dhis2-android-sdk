package org.hisp.dhis.android.persistence.datastore

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "DataStore",
    indices = [
        Index(value = ["namespace", "key"], unique = true),
    ],
)
internal data class DataStoreDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val namespace: String,
    val key: String,
    val value: String?,
    val syncState: String?,
    val deleted: Int?,
)
