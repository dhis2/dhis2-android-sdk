package org.hisp.dhis.android.persistence.trackedentity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.AccessDB
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithStyleDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields
import org.hisp.dhis.android.persistence.common.applyStyleFields
import org.hisp.dhis.android.persistence.common.toDB

@Entity(
    tableName = "TrackedEntityType",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class TrackedEntityTypeDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val shortName: String?,
    override val displayShortName: String?,
    override val description: String?,
    override val displayDescription: String?,
    val featureType: String?,
    override val color: String?,
    override val icon: String?,
    val accessDataWrite: AccessDB?,
) : EntityDB<TrackedEntityType>, BaseNameableObjectDB, ObjectWithStyleDB {

    override fun toDomain(): TrackedEntityType {
        return TrackedEntityType.builder().apply {
            applyBaseNameableFields(this@TrackedEntityTypeDB)
            applyStyleFields(this@TrackedEntityTypeDB)
            featureType(featureType?.let { FeatureType.valueOf(it) })
            accessDataWrite?.let { access(it.toDomain()) }
        }.build()
    }
}

internal fun TrackedEntityType.toDB(): TrackedEntityTypeDB {
    return TrackedEntityTypeDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        shortName = shortName(),
        displayShortName = displayShortName(),
        description = description(),
        displayDescription = displayDescription(),
        featureType = featureType()?.name,
        color = style()?.color(),
        icon = style()?.icon(),
        accessDataWrite = access().toDB(),
    )
}
