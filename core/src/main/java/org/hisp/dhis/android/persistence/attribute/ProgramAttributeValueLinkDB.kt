package org.hisp.dhis.android.persistence.attribute

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.attribute.ProgramAttributeValueLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "ProgramAttributeValueLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
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
    primaryKeys = ["program", "attribute"],
)
internal data class ProgramAttributeValueLinkDB(
    val program: String,
    val attribute: String,
    val value: String?,
) : EntityDB<ProgramAttributeValueLink> {

    override fun toDomain(): ProgramAttributeValueLink {
        return ProgramAttributeValueLink.builder()
            .program(program)
            .attribute(attribute)
            .value(value)
            .build()
    }
}

internal fun ProgramAttributeValueLink.toDB(): ProgramAttributeValueLinkDB {
    return ProgramAttributeValueLinkDB(
        program = program()!!,
        attribute = attribute()!!,
        value = value(),
    )
}
