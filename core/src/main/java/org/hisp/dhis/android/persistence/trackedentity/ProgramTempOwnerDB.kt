package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "ProgramTempOwner",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["program"]),
        Index(value = ["trackedEntityInstance"]),
    ],
)
internal data class ProgramTempOwnerDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val program: String,
    val trackedEntityInstance: String,
    val created: String,
    val validUntil: String,
    val reason: String,
)
