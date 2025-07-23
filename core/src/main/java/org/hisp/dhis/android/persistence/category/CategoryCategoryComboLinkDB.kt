package org.hisp.dhis.android.persistence.category

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.category.CategoryCategoryComboLink
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "CategoryCategoryComboLink",
    foreignKeys = [
        ForeignKey(
            entity = CategoryDB::class,
            parentColumns = ["uid"],
            childColumns = ["category"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryComboDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryCombo"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["category", "categoryCombo"],
)
internal data class CategoryCategoryComboLinkDB(
    val category: String,
    val categoryCombo: String,
    val sortOrder: Int?,
) : EntityDB<CategoryCategoryComboLink> {

    override fun toDomain(): CategoryCategoryComboLink {
        return CategoryCategoryComboLink.builder()
            .category(category)
            .categoryCombo(categoryCombo)
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun CategoryCategoryComboLink.toDB(): CategoryCategoryComboLinkDB {
    return CategoryCategoryComboLinkDB(
        category = category()!!,
        categoryCombo = categoryCombo()!!,
        sortOrder = sortOrder(),
    )
}
