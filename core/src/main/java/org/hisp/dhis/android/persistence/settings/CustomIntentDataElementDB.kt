package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CustomIntentDataElement",
    foreignKeys = [
        ForeignKey(
            entity = CustomIntentDB::class,
            parentColumns = ["uid"],
            childColumns = ["customIntentUid"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["customIntentUid"]),
    ],
)
internal data class CustomIntentDataElementDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val customIntentUid: String,
)
