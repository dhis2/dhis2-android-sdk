package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.CustomIntentDataElement
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "CustomIntentDataElement",
    foreignKeys = [
        ForeignKey(
            entity = CustomIntentDB::class,
            parentColumns = ["uid"],
            childColumns = ["customIntentUid"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["customIntentUid"]),
    ],
)
internal data class CustomIntentDataElementDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val customIntentUid: String,
) : EntityDB<CustomIntentDataElement> {

    override fun toDomain(): CustomIntentDataElement {
        return CustomIntentDataElement.builder()
            .id(id?.toLong())
            .uid(uid)
            .customIntentUid(customIntentUid)
            .build()
    }
}

internal fun CustomIntentDataElement.toDB(): CustomIntentDataElementDB {
    return CustomIntentDataElementDB(
        uid = uid(),
        customIntentUid = customIntentUid(),
    )
}
