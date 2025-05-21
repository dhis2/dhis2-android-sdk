package org.hisp.dhis.android.persistence.common

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ValueTypeDeviceRendering
import org.hisp.dhis.android.core.common.ValueTypeRenderingType

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
) : EntityDB<ValueTypeDeviceRendering> {

    override fun toDomain(): ValueTypeDeviceRendering {
        return ValueTypeDeviceRendering.builder().apply {
            id(id?.toLong())
            uid(uid)
            objectTable(objectTable)
            deviceType(deviceType)
            type?.let { type(ValueTypeRenderingType.valueOf(type)) }
            min(min)
            max(max)
            step(step)
            decimalPoints(decimalPoints)
        }.build()
    }
}

internal fun ValueTypeDeviceRendering.toDB(): ValueTypeDeviceRenderingDB {
    return ValueTypeDeviceRenderingDB(
        uid = uid(),
        objectTable = objectTable(),
        deviceType = deviceType(),
        type = type()?.name,
        min = min(),
        max = max(),
        step = step(),
        decimalPoints = decimalPoints(),
    )
}
