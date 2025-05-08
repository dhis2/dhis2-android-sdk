package org.hisp.dhis.android.persistence.legendset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.indicator.IndicatorDB

@Entity(
    tableName = "IndicatorLegendSetLink",
    foreignKeys = [
        ForeignKey(
            entity = IndicatorDB::class,
            parentColumns = ["uid"],
            childColumns = ["indicator"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = LegendSetDB::class,
            parentColumns = ["uid"],
            childColumns = ["legendSet"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["indicator", "legendSet"], unique = true),
        Index(value = ["indicator"]),
        Index(value = ["legendSet"]),
    ],
)
internal data class IndicatorLegendSetLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val indicator: String,
    val legendSet: String,
    val sortOrder: Int?,
)
