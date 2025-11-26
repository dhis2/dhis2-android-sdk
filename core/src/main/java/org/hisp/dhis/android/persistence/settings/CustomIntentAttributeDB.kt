package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.ForeignKey
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
    primaryKeys = ["customIntentUid", "uid"],
)
internal data class CustomIntentAttributeDB(
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
