package org.hisp.dhis.android.persistence.note

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.note.Note
import org.hisp.dhis.android.persistence.common.DataObjectDB
import org.hisp.dhis.android.persistence.common.DeletableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.SyncStateDB
import org.hisp.dhis.android.persistence.common.toDB
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
)
internal data class NoteDB(
    val noteType: String?,
    val event: String?,
    val enrollment: String?,
    val value: String?,
    val storedBy: String?,
    val storedDate: String?,
    @PrimaryKey
    val uid: String,
    override val syncState: SyncStateDB?,
    override val deleted: Boolean?,
) : EntityDB<Note>, DeletableObjectDB, DataObjectDB {

    override fun toDomain(): Note {
        return Note.builder().apply {
            uid(uid)
            noteType?.let { noteType(Note.NoteType.valueOf(it)) }
            event(event)
            enrollment(enrollment)
            value(value)
            storedBy(storedBy)
            storedDate(storedDate)
            syncState?.let { syncState(it.toDomain()) }
            deleted(deleted)
        }.build()
    }
}

internal fun Note.toDB(): NoteDB {
    return NoteDB(
        uid = uid(),
        noteType = noteType()?.name,
        event = event(),
        enrollment = enrollment(),
        value = value(),
        storedBy = storedBy(),
        storedDate = storedDate(),
        syncState = syncState()?.toDB(),
        deleted = deleted(),
    )
}
