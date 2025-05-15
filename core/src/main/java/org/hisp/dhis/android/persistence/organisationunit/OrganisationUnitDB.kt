package org.hisp.dhis.android.persistence.organisationunit

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.GeometryDB
import org.hisp.dhis.android.persistence.common.ObjectWithUidDB
import org.hisp.dhis.android.persistence.common.StringListDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields
import org.hisp.dhis.android.persistence.common.toDB

@Entity(
    tableName = "OrganisationUnit",
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class OrganisationUnitDB(
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
    val path: String?,
    val openingDate: String?,
    val closedDate: String?,
    val level: Int?,
    val parent: String?,
    val displayNamePath: StringListDB?,
    val geometryType: String?,
    val geometryCoordinates: String?,
) : EntityDB<OrganisationUnit>, BaseNameableObjectDB {

    override fun toDomain(): OrganisationUnit {
        return OrganisationUnit.builder()
            .applyBaseNameableFields(this)
            .id(id?.toLong())
            .path(path)
            .openingDate(openingDate.toJavaDate())
            .closedDate(closedDate.toJavaDate())
            .level(level)
            .parent(parent?.let { ObjectWithUidDB(it).toDomain() })
            .displayNamePath(displayNamePath?.toDomain())
            .geometry(GeometryDB(geometryType, geometryCoordinates).toDomain())
            .build()
    }
}

internal fun OrganisationUnit.toDB(): OrganisationUnitDB {
    return OrganisationUnitDB(
        uid = uid()!!,
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        shortName = shortName(),
        displayShortName = displayShortName(),
        description = description(),
        displayDescription = displayDescription(),
        path = path(),
        openingDate = openingDate().dateFormat(),
        closedDate = closedDate().dateFormat(),
        level = level(),
        parent = parent()?.uid(),
        displayNamePath = displayNamePath()?.toDB(),
        geometryType = geometry().toDB().geometryType,
        geometryCoordinates = geometry().toDB().geometryCoordinates,
    )
}
