package org.hisp.dhis.android.persistence.dataelement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.category.CategoryComboDB
import org.hisp.dhis.android.persistence.option.OptionSetDB

@Entity(
    tableName = "DataElement",
    foreignKeys = [
        ForeignKey(
            entity = OptionSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["optionSet"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["optionSet"]),
        Index(value = ["categoryCombo"]),
    ],
)
internal data class DataElementDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val code: String?,
    val name: String?,
    val displayName: String?,
    val created: String?,
    val lastUpdated: String?,
    val shortName: String?,
    val displayShortName: String?,
    val description: String?,
    val displayDescription: String?,
    val valueType: String?,
    val zeroIsSignificant: Int?,
    val aggregationType: String?,
    val formName: String?,
    val domainType: String?,
    val displayFormName: String?,
    val optionSet: String?,
    val categoryCombo: String,
    val fieldMask: String?,
    val color: String?,
    val icon: String?,
)
