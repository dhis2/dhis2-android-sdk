package org.hisp.dhis.android.persistence.dataset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["section", "dataElement"], unique = true),
        Index(value = ["section"]),
        Index(value = ["dataElement"]),
    ],
)
internal data class SectionDataElementLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val section: String,
    val dataElement: String,
    val sortOrder: Int?,
) : EntityDB<SectionDataElementLink> {

    override fun toDomain(): SectionDataElementLink {
        return SectionDataElementLink.builder()
            .id(id?.toLong())
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
