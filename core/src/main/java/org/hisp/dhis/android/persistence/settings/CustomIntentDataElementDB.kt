package org.hisp.dhis.android.persistence.settings

import androidx.room.Entity
import androidx.room.ForeignKey
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
    primaryKeys = ["customIntentUid", "uid"],
)
internal data class CustomIntentDataElementDB(
    val uid: String,
    val customIntentUid: String,
) : EntityDB<CustomIntentDataElement> {

    override fun toDomain(): CustomIntentDataElement {
        return CustomIntentDataElement.builder()
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
