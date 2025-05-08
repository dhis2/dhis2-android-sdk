package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "CustomIntent",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class CustomIntentDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val name: String?,
    val action: String?,
    val packageName: String?,
    val requestArguments: String?,
    val responseDataArgument: String?,
    val responseDataPath: String?,
)
