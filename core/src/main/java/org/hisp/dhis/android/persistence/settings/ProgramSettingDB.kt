package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.DownloadPeriod
import org.hisp.dhis.android.core.settings.EnrollmentScope
import org.hisp.dhis.android.core.settings.LimitScope
import org.hisp.dhis.android.core.settings.ProgramSetting
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "ProgramSetting",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
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
internal data class ProgramSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String?,
    val name: String?,
    val lastUpdated: String?,
    val teiDownload: Int?,
    val teiDBTrimming: Int?,
    val eventsDownload: Int?,
    val eventsDBTrimming: Int?,
    val updateDownload: String?,
    val updateDBTrimming: String?,
    val settingDownload: String?,
    val settingDBTrimming: String?,
    val enrollmentDownload: String?,
    val enrollmentDBTrimming: String?,
    val eventDateDownload: String?,
    val eventDateDBTrimming: String?,
    val enrollmentDateDownload: String?,
    val enrollmentDateDBTrimming: String?,
) : EntityDB<ProgramSetting> {

    override fun toDomain(): ProgramSetting {
        return ProgramSetting.builder().apply {
            id(id?.toLong())
            uid(uid)
            name(name)
            lastUpdated(lastUpdated.toJavaDate())
            teiDownload(teiDownload)
            teiDBTrimming(teiDBTrimming)
            eventsDownload(eventsDownload)
            eventsDBTrimming(eventsDBTrimming)
            updateDownload?.let { updateDownload(DownloadPeriod.valueOf(it)) }
            updateDBTrimming?.let { updateDBTrimming(DownloadPeriod.valueOf(it)) }
            settingDownload?.let { settingDownload(LimitScope.valueOf(it)) }
            settingDBTrimming?.let { settingDBTrimming(LimitScope.valueOf(it)) }
            enrollmentDownload?.let { enrollmentDownload(EnrollmentScope.valueOf(it)) }
            enrollmentDBTrimming?.let { enrollmentDBTrimming(EnrollmentScope.valueOf(it)) }
            eventDateDownload?.let { eventDateDownload(DownloadPeriod.valueOf(it)) }
            eventDateDBTrimming?.let { eventDateDBTrimming(DownloadPeriod.valueOf(it)) }
            enrollmentDateDownload?.let { enrollmentDateDownload(DownloadPeriod.valueOf(it)) }
            enrollmentDateDBTrimming?.let { enrollmentDateDBTrimming(DownloadPeriod.valueOf(it)) }
        }.build()
    }
}

internal fun ProgramSetting.toDB(): ProgramSettingDB {
    return ProgramSettingDB(
        uid = uid(),
        name = name(),
        lastUpdated = lastUpdated()?.dateFormat(),
        teiDownload = teiDownload(),
        teiDBTrimming = teiDBTrimming(),
        eventsDownload = eventsDownload(),
        eventsDBTrimming = eventsDBTrimming(),
        updateDownload = updateDownload()?.name,
        updateDBTrimming = updateDBTrimming()?.name,
        settingDownload = settingDownload()?.name,
        settingDBTrimming = settingDBTrimming()?.name,
        enrollmentDownload = enrollmentDownload()?.name,
        enrollmentDBTrimming = enrollmentDBTrimming()?.name,
        eventDateDownload = eventDateDownload()?.name,
        eventDateDBTrimming = eventDateDBTrimming()?.name,
        enrollmentDateDownload = enrollmentDateDownload()?.name,
        enrollmentDateDBTrimming = enrollmentDateDBTrimming()?.name,
    )
}
