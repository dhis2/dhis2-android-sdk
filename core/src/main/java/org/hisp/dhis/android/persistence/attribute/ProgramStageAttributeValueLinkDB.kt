package org.hisp.dhis.android.persistence.attribute

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.attribute.ProgramStageAttributeValueLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramStageDB

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
    indices = [
        Index(value = ["programStage", "attribute"], unique = true),
        Index(value = ["programStage"]),
        Index(value = ["attribute"]),
    ],
)
internal data class ProgramStageAttributeValueLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val programStage: String,
    val attribute: String,
    val value: String?,
) : EntityDB<ProgramStageAttributeValueLink> {

    override fun toDomain(): ProgramStageAttributeValueLink {
        return ProgramStageAttributeValueLink.builder()
            .id(id?.toLong())
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
