package org.hisp.dhis.android.persistence.configuration

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Configuration",
    indices = [
        Index(value = ["serverUrl"], unique = true),
    ],
)
internal data class ConfigurationDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val serverUrl: String,
)
