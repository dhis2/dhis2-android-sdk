package org.hisp.dhis.android.persistence.dataset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.category.CategoryComboDB
import org.hisp.dhis.android.persistence.dataelement.DataElementDB

@Entity(
    tableName = "DataSetDataElementLink",
    foreignKeys = [
        ForeignKey(
            entity = DataSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = DataElementDB::class,
            parentColumns = ["uid"],
            childColumns = ["dataElement"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true
        ),
    ],
    indices = [
        Index(value = ["dataSet", "dataElement"], unique = true),
        Index(value = ["dataSet"]),
        Index(value = ["dataElement"]),
        Index(value = ["categoryCombo"]),
    ],
)
internal data class DataSetDataElementLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val dataSet: String,
    val dataElement: String,
    val categoryCombo: String?,
)
