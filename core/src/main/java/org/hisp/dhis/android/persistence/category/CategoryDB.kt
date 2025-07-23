package org.hisp.dhis.android.persistence.category

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(tableName = "Category")
internal data class CategoryDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    val dataDimensionType: String?,
) : EntityDB<Category>, BaseIdentifiableObjectDB {

    override fun toDomain(): Category {
        return Category.builder()
            .applyBaseIdentifiableFields(this)
            .dataDimensionType(dataDimensionType)
            .build()
    }
}

internal fun Category.toDB(): CategoryDB {
    return CategoryDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        dataDimensionType = dataDimensionType(),
    )
}
