package org.hisp.dhis.android.persistence.category

import androidx.room.Entity
import androidx.room.ForeignKey
import org.hisp.dhis.android.core.category.CategoryCategoryOptionLink
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "CategoryCategoryOptionLink",
    foreignKeys = [
        ForeignKey(
            entity = CategoryDB::class,
            parentColumns = ["uid"],
            childColumns = ["category"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
        ForeignKey(
            entity = CategoryOptionDB::class,
            parentColumns = ["uid"],
            childColumns = ["categoryOption"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    primaryKeys = ["category", "categoryOption"],
)
internal data class CategoryCategoryOptionLinkDB(
    val category: String,
    val categoryOption: String,
    val sortOrder: Int?,
) : EntityDB<CategoryCategoryOptionLink> {

    override fun toDomain(): CategoryCategoryOptionLink {
        return CategoryCategoryOptionLink.builder()
            .category(category)
            .categoryOption(categoryOption)
            .sortOrder(sortOrder)
            .build()
    }
}

internal fun CategoryCategoryOptionLink.toDB(): CategoryCategoryOptionLinkDB {
    return CategoryCategoryOptionLinkDB(
        category = category()!!,
        categoryOption = categoryOption()!!,
        sortOrder = sortOrder(),
    )
}
