package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.trackedentity.ownership.ProgramTempOwner
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "ProgramTempOwner",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["program"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["program", "trackedEntityInstance", "created"],
)
internal data class ProgramTempOwnerDB(
    val program: String,
    val trackedEntityInstance: String,
    val created: String,
    val validUntil: String,
    val reason: String,
) : EntityDB<ProgramTempOwner> {

    override fun toDomain(): ProgramTempOwner {
        return ProgramTempOwner.builder()
            .program(program)
            .trackedEntityInstance(trackedEntityInstance)
            .created(created.toJavaDate())
            .validUntil(validUntil.toJavaDate())
            .reason(reason)
            .build()
    }
}

internal fun ProgramTempOwner.toDB(): ProgramTempOwnerDB {
    return ProgramTempOwnerDB(
        program = program(),
        trackedEntityInstance = trackedEntityInstance(),
        created = created().dateFormat()!!,
        validUntil = validUntil().dateFormat()!!,
        reason = reason(),
    )
}
