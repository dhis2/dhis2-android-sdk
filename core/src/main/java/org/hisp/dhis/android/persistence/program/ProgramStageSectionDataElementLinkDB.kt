package org.hisp.dhis.android.persistence.program

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.program.ProgramStageSectionDataElementLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataelement.DataElementDB

@Entity(
    tableName = "ProgramStageSectionDataElementLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageSectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStageSection"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["programStageSection", "dataElement"],
)
internal data class ProgramStageSectionDataElementLinkDB(
    val programStageSection: String,
    val dataElement: String,
    val sortOrder: Int,
) : EntityDB<ProgramStageSectionDataElementLink> {

    override fun toDomain(): ProgramStageSectionDataElementLink {
        return ProgramStageSectionDataElementLink.builder()
            .programStageSection(programStageSection)
            .dataElement(dataElement)
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun ProgramStageSectionDataElementLink.toDB(): ProgramStageSectionDataElementLinkDB {
    return ProgramStageSectionDataElementLinkDB(
        programStageSection = programStageSection(),
        dataElement = dataElement(),
        sortOrder = sortOrder(),
    )
}
