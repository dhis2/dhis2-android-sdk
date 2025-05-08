package org.hisp.dhis.android.persistence.option

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Option",
    foreignKeys = [
        ForeignKey(
            entity = OptionSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["optionSet"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["optionSet"]),
        Index(value = ["optionSet", "code"]),
    ],
)
internal data class OptionDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val optionSet: String,
    val sortOrder: Int?,
    val color: String?,
    val icon: String?,
)
