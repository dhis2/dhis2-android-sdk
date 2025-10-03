package org.hisp.dhis.android.persistence.program

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramSection
import org.hisp.dhis.android.core.program.SectionDeviceRendering
import org.hisp.dhis.android.core.program.SectionRendering
import org.hisp.dhis.android.core.program.SectionRenderingType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithStyleDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.persistence.common.applyStyleFields

@Entity(
    tableName = "ProgramSection",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class ProgramSectionDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val description: String?,
    val program: String?,
    val sortOrder: Int?,
    val formName: String?,
    override val color: String?,
    override val icon: String?,
    val desktopRenderType: String?,
    val mobileRenderType: String?,
) : EntityDB<ProgramSection>, BaseIdentifiableObjectDB, ObjectWithStyleDB {

    override fun toDomain(): ProgramSection {
        return ProgramSection.builder().apply {
            applyBaseIdentifiableFields(this@ProgramSectionDB)
            applyStyleFields(this@ProgramSectionDB)
            description(description)
            program?.let { program(ObjectWithUid.create(it)) }
            sortOrder(sortOrder)
            formName(formName)
            renderType(
                SectionRendering.create(
                    desktopRenderType?.let { SectionDeviceRendering.create(SectionRenderingType.valueOf(it)) },
                    mobileRenderType?.let { SectionDeviceRendering.create(SectionRenderingType.valueOf(it)) },
                ),
            )
        }.build()
    }
}

internal fun ProgramSection.toDB(): ProgramSectionDB {
    return ProgramSectionDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        description = description(),
        program = program()?.uid(),
        sortOrder = sortOrder(),
        formName = formName(),
        color = style()?.color(),
        icon = style()?.icon(),
        desktopRenderType = renderType()?.desktop()?.type()?.name,
        mobileRenderType = renderType()?.mobile()?.type()?.name,
    )
}
