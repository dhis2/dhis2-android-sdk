package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["program"]),
        Index(value = ["trackedEntityInstance"]),
    ],
)
internal data class ProgramTempOwnerDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val program: String,
    val trackedEntityInstance: String,
    val created: String,
    val validUntil: String,
    val reason: String,
) : EntityDB<ProgramTempOwner> {

    override fun toDomain(): ProgramTempOwner {
        return ProgramTempOwner.builder()
            .id(id?.toLong())
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
