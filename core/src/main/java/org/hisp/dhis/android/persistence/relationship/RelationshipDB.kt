package org.hisp.dhis.android.persistence.relationship

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Relationship",
    foreignKeys = [
        ForeignKey(
            entity = RelationshipTypeDB::class,
            parentColumns = ["uid"],
            childColumns = ["relationshipType"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["relationshipType"]),
    ],
)
internal data class RelationshipDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val uid: String,
    val name: String?,
    val created: String?,
    val lastUpdated: String?,
    val relationshipType: String,
    val syncState: String?,
    val deleted: Int?,
)
