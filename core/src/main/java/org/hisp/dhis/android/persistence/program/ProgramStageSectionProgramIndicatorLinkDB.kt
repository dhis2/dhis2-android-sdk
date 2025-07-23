package org.hisp.dhis.android.persistence.program

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.program.ProgramStageSectionProgramIndicatorLink
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "ProgramStageSectionProgramIndicatorLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageSectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStageSection"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = ProgramIndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["programIndicator"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["programStageSection", "programIndicator"],
)
internal data class ProgramStageSectionProgramIndicatorLinkDB(
    val programStageSection: String,
    val programIndicator: String,
) : EntityDB<ProgramStageSectionProgramIndicatorLink> {

    override fun toDomain(): ProgramStageSectionProgramIndicatorLink {
        return ProgramStageSectionProgramIndicatorLink.builder()
            .programStageSection(programStageSection)
            .programIndicator(programIndicator)
            .build()
    }
}

internal fun ProgramStageSectionProgramIndicatorLink.toDB(): ProgramStageSectionProgramIndicatorLinkDB {
    return ProgramStageSectionProgramIndicatorLinkDB(
        programStageSection = programStageSection()!!,
        programIndicator = programIndicator()!!,
    )
}
