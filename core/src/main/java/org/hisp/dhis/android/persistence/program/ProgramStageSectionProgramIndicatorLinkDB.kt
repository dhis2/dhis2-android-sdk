package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["programStageSection", "programIndicator"], unique = true),
        Index(value = ["programStageSection"]),
        Index(value = ["programIndicator"]),
    ],
)
internal data class ProgramStageSectionProgramIndicatorLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
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
