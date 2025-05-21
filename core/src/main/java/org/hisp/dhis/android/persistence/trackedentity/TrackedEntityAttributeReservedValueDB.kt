package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeReservedValue
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "TrackedEntityAttributeReservedValue")
internal data class TrackedEntityAttributeReservedValueDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val ownerObject: String?,
    val ownerUid: String?,
    val key: String?,
    val value: String?,
    val created: String?,
    val expiryDate: String?,
    val organisationUnit: String?,
    val temporalValidityDate: String?,
    val pattern: String?,
) : EntityDB<TrackedEntityAttributeReservedValue> {

    override fun toDomain(): TrackedEntityAttributeReservedValue {
        return TrackedEntityAttributeReservedValue.builder()
            .id(id?.toLong())
            .ownerObject(ownerObject)
            .ownerUid(ownerUid)
            .key(key)
            .value(value)
            .created(created.toJavaDate())
            .expiryDate(expiryDate.toJavaDate())
            .organisationUnit(organisationUnit)
            .temporalValidityDate(temporalValidityDate.toJavaDate())
            .pattern(pattern)
            .build()
    }
}

internal fun TrackedEntityAttributeReservedValue.toDB(): TrackedEntityAttributeReservedValueDB {
    return TrackedEntityAttributeReservedValueDB(
        ownerObject = ownerObject(),
        ownerUid = ownerUid(),
        key = key(),
        value = value(),
        created = created().dateFormat(),
        expiryDate = expiryDate().dateFormat(),
        organisationUnit = organisationUnit(),
        temporalValidityDate = temporalValidityDate().dateFormat(),
        pattern = pattern(),
    )
}
