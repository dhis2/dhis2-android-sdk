package org.hisp.dhis.android.persistence.program

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.persistence.trackedentity.TrackedEntityAttributeDB

@Entity(
    tableName = "ProgramSectionAttributeLink",
    foreignKeys = [
        ForeignKey(
            entity = ProgramSectionDB::class,
            parentColumns = ["uid"],
            childColumns = ["programSection"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = TrackedEntityAttributeDB::class,
            parentColumns = ["uid"],
            childColumns = ["attribute"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["programSection", "attribute"], unique = true),
        Index(value = ["programSection"]),
        Index(value = ["attribute"]),
    ],
)
internal data class ProgramSectionAttributeLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val programSection: String,
    val attribute: String,
    val sortOrder: Int?,
)
