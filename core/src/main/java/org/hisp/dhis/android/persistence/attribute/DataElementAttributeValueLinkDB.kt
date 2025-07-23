package org.hisp.dhis.android.persistence.attribute

import androidx.room.Entity
import androidx.room.ForeignKey
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
    primaryKeys = ["dataElement", "attribute"],
)
internal data class DataElementAttributeValueLinkDB(
    val dataElement: String,
    val attribute: String,
    val value: String?,
) : EntityDB<DataElementAttributeValueLink> {

    override fun toDomain(): DataElementAttributeValueLink {
        return DataElementAttributeValueLink.builder()
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
