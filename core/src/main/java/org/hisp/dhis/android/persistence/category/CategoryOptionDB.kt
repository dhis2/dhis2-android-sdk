package org.hisp.dhis.android.persistence.category

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.AccessDB
import org.hisp.dhis.android.persistence.common.BaseNameableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseNameableFields
import org.hisp.dhis.android.persistence.common.toDB

@Entity(tableName = "CategoryOption")
internal data class CategoryOptionDB(
    @PrimaryKey
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
    val startDate: String?,
    val endDate: String?,
    val accessDataWrite: AccessDB?,
) : EntityDB<CategoryOption>, BaseNameableObjectDB {

    override fun toDomain(): CategoryOption {
        return CategoryOption.builder().apply {
            applyBaseNameableFields(this@CategoryOptionDB)
            startDate(startDate.toJavaDate())
            endDate(endDate.toJavaDate())
            accessDataWrite?.let { access(it.toDomain()) }
        }.build()
    }
}

internal fun CategoryOption.toDB(): CategoryOptionDB {
    return CategoryOptionDB(
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
        startDate = startDate().dateFormat(),
        endDate = endDate().dateFormat(),
        accessDataWrite = access().toDB(),
    )
}
