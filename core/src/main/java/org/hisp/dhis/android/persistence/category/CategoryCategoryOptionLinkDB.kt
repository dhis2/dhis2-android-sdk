package org.hisp.dhis.android.persistence.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
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
    indices = [
        Index(value = ["category", "categoryOption"], unique = true),
        Index(value = ["category"]),
        Index(value = ["categoryOption"]),
    ],
)
internal data class CategoryCategoryOptionLinkDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val category: String,
    val categoryOption: String,
    val sortOrder: Int?,
) : EntityDB<CategoryCategoryOptionLink> {

    override fun toDomain(): CategoryCategoryOptionLink {
        return CategoryCategoryOptionLink.builder()
            .id(id.toLong())
            .category(category)
            .categoryOption(categoryOption)
            .sortOrder(sortOrder)
            .build()
    }

    companion object {
        fun CategoryCategoryOptionLink.toDB(): CategoryCategoryOptionLinkDB {
            return CategoryCategoryOptionLinkDB(
                category = category()!!,
                categoryOption = categoryOption()!!,
                sortOrder = sortOrder(),
            )
        }
    }
}
