package org.hisp.dhis.android.persistence.dataset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Section",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["dataSet"]),
    ],
)
internal data class SectionDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val description: String?,
    val sortOrder: Int?,
    val dataSet: String,
    val showRowTotals: Int?,
    val showColumnTotals: Int?,
    val disableDataElementAutoGroup: Int?,
    val pivotMode: String?,
    val pivotedCategory: String?,
    val afterSectionText: String?,
    val beforeSectionText: String?,
)
