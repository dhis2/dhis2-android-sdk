package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
)
