package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.ObjectStyle
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.program.ProgramSection
import org.hisp.dhis.android.core.program.SectionDeviceRendering
import org.hisp.dhis.android.core.program.SectionRendering
import org.hisp.dhis.android.core.program.SectionRenderingType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

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
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["program"]),
    ],
)
internal data class ProgramSectionDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
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
    val color: String?,
    val icon: String?,
    val desktopRenderType: String?,
    val mobileRenderType: String?,
) : EntityDB<ProgramSection>, BaseIdentifiableObjectDB {

    override fun toDomain(): ProgramSection {
        return ProgramSection.builder().apply {
            applyBaseIdentifiableFields(this@ProgramSectionDB)
            id(id?.toLong())
            description(description)
            program?.let { program(ObjectWithUid.create(it)) }
            sortOrder(sortOrder)
            formName(formName)
            renderType(
                SectionRendering.create(
                    SectionDeviceRendering.create(desktopRenderType?.let { SectionRenderingType.valueOf(it) }),
                    SectionDeviceRendering.create(mobileRenderType?.let { SectionRenderingType.valueOf(it) })
                )
            )
            style(ObjectStyle.builder().color(color).icon(icon).build())
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
