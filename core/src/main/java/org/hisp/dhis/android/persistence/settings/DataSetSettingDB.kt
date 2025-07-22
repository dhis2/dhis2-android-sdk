package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.DataSetSetting
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataset.DataSetDB

@Entity(
    tableName = "DataSetSetting",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["uid"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class DataSetSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String?,
    val name: String?,
    val lastUpdated: String?,
    val periodDSDownload: Int?,
    val periodDSDBTrimming: Int?,
) : EntityDB<DataSetSetting> {

    override fun toDomain(): DataSetSetting {
        return DataSetSetting.builder()
            .uid(uid)
            .name(name)
            .lastUpdated(lastUpdated.toJavaDate())
            .periodDSDownload(periodDSDownload)
            .periodDSDBTrimming(periodDSDBTrimming)
            .build()
    }
}

internal fun DataSetSetting.toDB(): DataSetSettingDB {
    return DataSetSettingDB(
        uid = uid(),
        name = name(),
        lastUpdated = lastUpdated().dateFormat(),
        periodDSDownload = periodDSDownload(),
        periodDSDBTrimming = periodDSDBTrimming(),
    )
}
