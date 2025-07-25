package org.hisp.dhis.android.persistence.category

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(tableName = "CategoryCombo")
internal data class CategoryComboDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    val isDefault: Boolean?,
) : EntityDB<CategoryCombo>, BaseIdentifiableObjectDB {

    override fun toDomain(): CategoryCombo {
        return CategoryCombo.builder()
            .applyBaseIdentifiableFields(this)
            .isDefault(isDefault)
            .build()
    }
}

internal fun CategoryCombo.toDB(): CategoryComboDB {
    return CategoryComboDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        isDefault = isDefault(),
        deleted = deleted(),
    )
}
