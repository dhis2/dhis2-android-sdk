package org.hisp.dhis.android.persistence.dataset

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.category.CategoryComboDB

@Entity(
    tableName = "DataSet",
    foreignKeys = [
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["categoryCombo"]),
    ],
)
internal data class DataSetDB(
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
    val periodType: String?,
    val categoryCombo: String,
    val mobile: Int?,
    val version: Int?,
    val expiryDays: Int?,
    val timelyDays: Int?,
    val notifyCompletingUser: Int?,
    val openFuturePeriods: Int?,
    val fieldCombinationRequired: Int?,
    val validCompleteOnly: Int?,
    val noValueRequiresComment: Int?,
    val skipOffline: Int?,
    val dataElementDecoration: Int?,
    val renderAsTabs: Int?,
    val renderHorizontally: Int?,
    val accessDataWrite: Int?,
    val workflow: String?,
    val color: String?,
    val icon: String?,
    val header: String?,
    val subHeader: String?,
    val customTextAlign: String?,
    val tabsDirection: String?,
)
