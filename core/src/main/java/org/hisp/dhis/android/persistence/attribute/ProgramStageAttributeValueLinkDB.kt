package org.hisp.dhis.android.persistence.attribute

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.attribute.ProgramStageAttributeValueLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB
import org.hisp.dhis.android.processor.ParentColumn

@Entity(
    tableName = "ProgramStageAttributeValueLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramStageDB::class,
            parentColumns = ["uid"],
            childColumns = ["programStage"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = AttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["attribute"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["programStage", "attribute"],
)
internal data class ProgramStageAttributeValueLinkDB(
    @ParentColumn val programStage: String,
    val attribute: String,
    val value: String?,
) : EntityDB<ProgramStageAttributeValueLink> {

    override fun toDomain(): ProgramStageAttributeValueLink {
        return ProgramStageAttributeValueLink.builder()
            .programStage(programStage)
            .attribute(attribute)
            .value(value)
            .build()
    }
}

internal fun ProgramStageAttributeValueLink.toDB(): ProgramStageAttributeValueLinkDB {
    return ProgramStageAttributeValueLinkDB(
        programStage = programStage()!!,
        attribute = attribute()!!,
        value = value(),
    )
}
