package org.hisp.dhis.android.persistence.event

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "EventSync",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["program", "organisationUnitIdsHash"], unique = true),
        Index(value = ["program"]),
    ],
)
internal data class EventSyncDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val program: String?,
    val organisationUnitIdsHash: Int?,
    val downloadLimit: Int,
    val lastUpdated: String,
)
