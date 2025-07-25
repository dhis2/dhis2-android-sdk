package org.hisp.dhis.android.persistence.category

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.persistence.common.BaseIdentifiableObjectDB
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.android.persistence.common.ObjectWithUidDB
import org.hisp.dhis.android.persistence.common.applyBaseIdentifiableFields

@Entity(
    tableName = "CategoryOptionCombo",
    foreignKeys = [
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
)
internal data class CategoryOptionComboDB(
    @PrimaryKey
    override val uid: String,
    override val code: String?,
    override val name: String?,
    override val displayName: String?,
    override val created: String?,
    override val lastUpdated: String?,
    override val deleted: Boolean?,
    val categoryCombo: String?,
) : EntityDB<CategoryOptionCombo>, BaseIdentifiableObjectDB {

    override fun toDomain(): CategoryOptionCombo {
        return CategoryOptionCombo.builder().apply {
            applyBaseIdentifiableFields(this@CategoryOptionComboDB)
            categoryCombo?.let { ObjectWithUidDB(categoryCombo).toDomain() }
        }.build()
    }
}

internal fun CategoryOptionCombo.toDB(): CategoryOptionComboDB {
    return CategoryOptionComboDB(
        uid = uid(),
        code = code(),
        name = name(),
        displayName = displayName(),
        created = created().dateFormat(),
        lastUpdated = lastUpdated().dateFormat(),
        categoryCombo = categoryCombo()?.uid(),
        deleted = deleted(),
    )
}
