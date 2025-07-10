package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramStageSection
import org.hisp.dhis.android.core.program.SectionDeviceRendering
import org.hisp.dhis.android.core.program.SectionRendering
import org.hisp.dhis.android.core.program.SectionRenderingType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(
    tableName = "ProgramStageSection",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["programStage"]),
    ],
)
internal data class ProgramStageSectionDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val sortOrder: Int?,
    val programStage: String,
    val desktopRenderType: String?,
    val mobileRenderType: String?,
    val description: String?,
    val displayDescription: String?,
) : EntityDB<ProgramStageSection>, BaseIdentifiableObjectDB {

    override fun toDomain(): ProgramStageSection {
        return ProgramStageSection.builder()
            .applyBaseIdentifiableFields(this)
            .sortOrder(sortOrder)
            .programStage(ObjectWithUid.create(programStage))
            .renderType(
                SectionRendering.create(
                    desktopRenderType?.let { SectionDeviceRendering.create(SectionRenderingType.valueOf(it)) },
                    mobileRenderType?.let { SectionDeviceRendering.create(SectionRenderingType.valueOf(it)) },
                ),
            )
            .description(description)
            .displayDescription(displayDescription)
            .build()
    }
}

internal fun ProgramStageSection.toDB(): ProgramStageSectionDB {
    return ProgramStageSectionDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        sortOrder = sortOrder(),
        programStage = programStage()!!.uid(),
        desktopRenderType = renderType()?.desktop()?.type()?.name,
        mobileRenderType = renderType()?.mobile()?.type()?.name,
        description = description(),
        displayDescription = displayDescription(),
    )
}
