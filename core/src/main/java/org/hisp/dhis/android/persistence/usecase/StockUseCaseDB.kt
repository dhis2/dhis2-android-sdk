package org.hisp.dhis.android.persistence.usecase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.usecase.stock.InternalStockUseCase
import org.hisp.dhis.android.persistence.program.ProgramDB

@Entity(
    tableName = "StockUseCase",
    foreignKeys = [
        ForeignKey(
            entity = ProgramDB::class,
            parentColumns = ["uid"],
            childColumns = ["uid"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["uid"], unique = true),
    ],
)
internal data class StockUseCaseDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val uid: String,
    val itemCode: String?,
    val itemDescription: String?,
    val programType: String?,
    val description: String?,
    val stockOnHand: String?,
) {
    fun toDomain(): InternalStockUseCase {
        return InternalStockUseCase.builder()
            .uid(uid)
            .itemCode(itemCode)
            .itemDescription(itemDescription)
            .programType(programType)
            .description(description)
            .stockOnHand(stockOnHand)
            .build()
    }
}

internal fun InternalStockUseCase.toDB(): StockUseCaseDB {
    return StockUseCaseDB(
        uid = uid(),
        itemCode = itemCode(),
        itemDescription = itemDescription(),
        programType = programType(),
        description = description(),
        stockOnHand = stockOnHand(),
    )
}
