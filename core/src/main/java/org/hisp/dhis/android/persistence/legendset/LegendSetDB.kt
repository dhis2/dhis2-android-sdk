package org.hisp.dhis.android.persistence.legendset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "LegendSet",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class LegendSetDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val symbolizer: String?,
)
