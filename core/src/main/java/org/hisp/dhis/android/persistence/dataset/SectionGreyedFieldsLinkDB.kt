package org.hisp.dhis.android.persistence.dataset

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.dataset.SectionGreyedFieldsLink
import org.hisp.dhis.android.persistence.category.CategoryOptionComboDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataelement.DataElementOperandDB

@Entity(
    tableName = "SectionGreyedFieldsLink",
    foreignKeys = [
        ForeignKey(
            entity = SectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["section"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = DataElementOperandDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElementOperand"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryOptionComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOptionCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["section", "dataElementOperand", "categoryOptionCombo"],
)
internal data class SectionGreyedFieldsLinkDB(
    val section: String,
    val dataElementOperand: String,
    val categoryOptionCombo: String?,
) : EntityDB<SectionGreyedFieldsLink> {

    override fun toDomain(): SectionGreyedFieldsLink {
        return SectionGreyedFieldsLink.builder()
            .section(section)
            .dataElementOperand(dataElementOperand)
            .categoryOptionCombo(categoryOptionCombo)
            .build()
    }
}

internal fun SectionGreyedFieldsLink.toDB(): SectionGreyedFieldsLinkDB {
    return SectionGreyedFieldsLinkDB(
        section = section()!!,
        dataElementOperand = dataElementOperand()!!,
        categoryOptionCombo = categoryOptionCombo(),
    )
}
