package org.hisp.dhis.android.persistence.resource

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Resource")
internal data class ResourceDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val resourceType: String,
    val lastSynced: String?,
)
