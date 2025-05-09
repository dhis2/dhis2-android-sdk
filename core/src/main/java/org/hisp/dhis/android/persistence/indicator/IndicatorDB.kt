package org.hisp.dhis.android.persistence.indicator

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Indicator",
    foreignKeys = [
        ForeignKey(
            entity = IndicatorTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["indicatorType"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["indicatorType"]),
    ],
)
internal data class IndicatorDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
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
    val annualized: Int?,
    val indicatorType: String?,
    val numerator: String?,
    val numeratorDescription: String?,
    val denominator: String?,
    val denominatorDescription: String?,
    val url: String?,
    val decimals: Int?,
    val color: String?,
    val icon: String?,
)
