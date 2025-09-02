package org.hisp.dhis.android.persistence.relationship

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.AccessDB
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields
import org.hisp.dhis.android.persistence.common.toDB

@Entity(tableName = "RelationshipType")
internal data class RelationshipTypeDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val fromToName: String?,
    val toFromName: String?,
    val bidirectional: Boolean?,
    val accessDataWrite: AccessDB?,
) : EntityDB<RelationshipType>, BaseIdentifiableObjectDB {

    override fun toDomain(): RelationshipType {
        return RelationshipType.builder()
            .apply {
                applyBaseIdentifiableFields(this@RelationshipTypeDB)
                fromToName(fromToName)
                toFromName(toFromName)
                bidirectional(bidirectional)
                accessDataWrite?.let { access(it.toDomain()) }
            }.build()
    }
}

internal fun RelationshipType.toDB(): RelationshipTypeDB {
    return RelationshipTypeDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        fromToName = fromToName(),
        toFromName = toFromName(),
        bidirectional = bidirectional(),
        accessDataWrite = access().toDB(),
    )
}
