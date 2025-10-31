package org.hisp.dhis.android.persistence.program

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.program.ProgramSectionAttributeLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "ProgramSectionAttributeLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramSectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["programSection"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["attribute"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["programSection", "attribute"],
)
internal data class ProgramSectionAttributeLinkDB(
    @ParentColumn val programSection: String,
    val attribute: String,
    val sortOrder: Int?,
) : EntityDB<ProgramSectionAttributeLink> {

    override fun toDomain(): ProgramSectionAttributeLink {
        return ProgramSectionAttributeLink.builder()
            .programSection(programSection)
            .attribute(attribute)
            .sortOrder(sortOrder ?: 0)
            .build()
    }
}

internal fun ProgramSectionAttributeLink.toDB(): ProgramSectionAttributeLinkDB {
    return ProgramSectionAttributeLinkDB(
        programSection = programSection(),
        attribute = attribute(),
        sortOrder = sortOrder(),
    )
}
