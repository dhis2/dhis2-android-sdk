package org.hisp.dhis.android.persistence.note

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.enrollment.EnrollmentDB
import org.hisp.dhis.android.persistence.event.EventDB

@Entity(
    tableName = "Note",
    foreignKeys = [
        ForeignKey(
            entity = EventDB::class,
            parentColumns = ["uid"],
            childColumns = ["event"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = EnrollmentDB::class,
            parentColumns = ["uid"],
            childColumns = ["enrollment"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["noteType", "event", "enrollment", "value", "storedBy", "storedDate"], unique = true),
        Index(value = ["event"]),
        Index(value = ["enrollment"]),
    ],
)
internal data class NoteDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val noteType: String?,
    val event: String?,
    val enrollment: String?,
    val value: String?,
    val storedBy: String?,
    val storedDate: String?,
    val uid: String?,
    val syncState: String?,
    val deleted: Int?,
)
