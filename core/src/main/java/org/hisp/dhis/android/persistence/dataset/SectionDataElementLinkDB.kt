package org.hisp.dhis.android.persistence.dataset

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.dataset.SectionDataElementLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataelement.DataElementDB

@Entity(
    tableName = "SectionDataElementLink",
    foreignKeys = [
        ForeignKey(
            entity = SectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["section"],
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
    primaryKeys = ["section", "dataElement"],
)
internal data class SectionDataElementLinkDB(
    val section: String,
    val dataElement: String,
    val sortOrder: Int?,
) : EntityDB<SectionDataElementLink> {

    override fun toDomain(): SectionDataElementLink {
        return SectionDataElementLink.builder()
            .section(section)
            .dataElement(dataElement)
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun SectionDataElementLink.toDB(): SectionDataElementLinkDB {
    return SectionDataElementLinkDB(
        section = section()!!,
        dataElement = dataElement()!!,
        sortOrder = sortOrder(),
    )
}
