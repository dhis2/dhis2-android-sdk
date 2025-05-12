package org.hisp.dhis.android.persistence.attribute

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.attribute.DataElementAttributeValueLink
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.dataelement.DataElementDB

@Entity(
    tableName = "DataElementAttributeValueLink",
    foreignKeys = [
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
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
        Index(value = ["dataElement", "attribute"], unique = true),
        Index(value = ["dataElement"]),
        Index(value = ["attribute"]),
    ],
)
internal data class DataElementAttributeValueLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val dataElement: String,
    val attribute: String,
    val value: String?,
) : EntityDB<DataElementAttributeValueLink> {

    override fun toDomain(): DataElementAttributeValueLink {
        return DataElementAttributeValueLink.builder()
            .id(id?.toLong())
            .dataElement(dataElement)
            .attribute(attribute)
            .value(value)
            .build()
    }
}

internal fun DataElementAttributeValueLink.toDB(): DataElementAttributeValueLinkDB {
    return DataElementAttributeValueLinkDB(
        dataElement = dataElement()!!,
        attribute = attribute()!!,
        value = value(),
    )
}
