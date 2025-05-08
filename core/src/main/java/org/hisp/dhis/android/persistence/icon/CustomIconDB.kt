package org.hisp.dhis.android.persistence.icon

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CustomIcon")
internal data class CustomIconDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val key: String,
    val fileResource: String,
    val href: String,
)
