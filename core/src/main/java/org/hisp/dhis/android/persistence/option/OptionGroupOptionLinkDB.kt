package org.hisp.dhis.android.persistence.option

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "OptionGroupOptionLink",
    foreignKeys = [
        ForeignKey(
            entity = OptionGroupDB::class,
            parentColumns = ["uid"],
            childColumns = ["optionGroup"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = OptionDB::class,
            parentColumns = ["uid"],
            childColumns = ["option"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["optionGroup", "option"], unique = true),
        Index(value = ["optionGroup"]),
        Index(value = ["option"]),
    ],
)
internal data class OptionGroupOptionLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val optionGroup: String,
    val option: String,
)
