package org.hisp.dhis.android.persistence.systeminfo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SystemInfo")
internal data class SystemInfoDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val serverDate: String?,
    val dateFormat: String?,
    val version: String?,
    val contextPath: String?,
    val systemName: String?,
)
