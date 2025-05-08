package org.hisp.dhis.android.persistence.settings

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeDB

@Entity(
    tableName = "AnalyticsTeiWHONutritionData",
    foreignKeys = [
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["genderAttribute"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = AnalyticsTeiSettingDB::class,
            parentColumns = ["uid"],
            childColumns = ["teiSetting"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["genderAttribute"]),
        Index(value = ["teiSetting"]),
    ],
)
internal data class AnalyticsTeiWHONutritionDataDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val teiSetting: String,
    val chartType: String?,
    val genderAttribute: String,
    val genderFemale: String?,
    val genderMale: String?,
)
