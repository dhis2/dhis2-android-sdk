package org.hisp.dhis.android.persistence.tracker

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TrackerJobObject")
internal data class TrackerJobObjectDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val trackerType: String,
    val objectUid: String,
    val jobUid: String,
    val lastUpdated: String,
    val fileResources: String?,
)
