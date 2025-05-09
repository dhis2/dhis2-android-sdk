package org.hisp.dhis.android.persistence.common

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ValueTypeDeviceRendering",
    indices = [
        Index(value = ["uid", "deviceType"], unique = true),
    ],
)
internal data class ValueTypeDeviceRenderingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String?,
    val objectTable: String?,
    val deviceType: String?,
    val type: String?,
    val min: Int?,
    val max: Int?,
    val step: Int?,
    val decimalPoints: Int?,
)
