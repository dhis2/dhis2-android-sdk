package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.settings.CustomIntentAttribute
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "CustomIntentAttribute",
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
internal data class CustomIntentAttributeDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val customIntentUid: String,
) : EntityDB<CustomIntentAttribute> {

    override fun toDomain(): CustomIntentAttribute {
        return CustomIntentAttribute.builder()
            .uid(uid)
            .customIntentUid(customIntentUid)
            .build()
    }
}

internal fun CustomIntentAttribute.toDB(): CustomIntentAttributeDB {
    return CustomIntentAttributeDB(
        uid = uid(),
        customIntentUid = customIntentUid(),
    )
}
