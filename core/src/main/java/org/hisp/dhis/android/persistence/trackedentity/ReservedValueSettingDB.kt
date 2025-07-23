package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.trackedentity.ReservedValueSetting
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "ReservedValueSetting",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["uid"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class ReservedValueSettingDB(
    @PrimaryKey
    val uid: String?,
    val numberOfValuesToReserve: Int?,
) : EntityDB<ReservedValueSetting> {

    override fun toDomain(): ReservedValueSetting {
        return ReservedValueSetting.builder()
            .uid(uid)
            .numberOfValuesToReserve(numberOfValuesToReserve)
            .build()
    }
}

internal fun ReservedValueSetting.toDB(): ReservedValueSettingDB {
    return ReservedValueSettingDB(
        uid = uid(),
        numberOfValuesToReserve = numberOfValuesToReserve(),
    )
}
