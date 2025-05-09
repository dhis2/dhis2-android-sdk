package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AnalyticsDhisVisualization")
internal data class AnalyticsDhisVisualizationDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val scopeUid: String?,
    val scope: String?,
    val groupUid: String?,
    val groupName: String?,
    val timestamp: String?,
    val name: String?,
    val type: String,
)
