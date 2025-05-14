package org.hisp.dhis.android.persistence.relationship

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

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
    val id: Int? = 0,
    val uid: String,
    val name: String?,
    val created: String?,
    val lastUpdated: String?,
    val relationshipType: String,
    val syncState: String?,
    val deleted: Boolean?,
) : EntityDB<Relationship> {

    override fun toDomain(): Relationship {
        return Relationship.builder()
            .id(id?.toLong())
            .uid(uid)
            .name(name)
            .created(created.toJavaDate())
            .lastUpdated(lastUpdated.toJavaDate())
            .relationshipType(relationshipType)
            .syncState(State.valueOf(syncState!!))
            .deleted(deleted)
            .build()
    }
}

internal fun Relationship.toDB(): RelationshipDB {
    return RelationshipDB(
        uid = uid()!!,
        name = name(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        relationshipType = relationshipType()!!,
        syncState = syncState()?.name,
        deleted = deleted(),
    )
}
