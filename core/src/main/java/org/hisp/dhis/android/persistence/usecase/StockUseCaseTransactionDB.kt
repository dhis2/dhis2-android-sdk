package org.hisp.dhis.android.persistence.usecase

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.usecase.stock.InternalStockUseCaseTransaction
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "StockUseCaseTransaction",
    foreignKeys = [
        ForeignKey(
            entity = StockUseCaseDB::class,
            parentColumns = ["uid"],
            childColumns = ["programUid"],
            onDelete = ForeignKey.CASCADE,
            deferred = true,
        ),
    ],
    indices = [
        Index(value = ["programUid"]),
    ],
)
internal data class StockUseCaseTransactionDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val programUid: String,
    val sortOrder: Int?,
    val transactionType: String?,
    val distributedTo: String?,
    val stockDistributed: String?,
    val stockDiscarded: String?,
    val stockCount: String?,
) : EntityDB<InternalStockUseCaseTransaction> {
    override fun toDomain(): InternalStockUseCaseTransaction {
        return InternalStockUseCaseTransaction.builder()
            .programUid(programUid)
            .sortOrder(sortOrder)
            .transactionType(transactionType)
            .distributedTo(distributedTo)
            .stockDistributed(stockDistributed)
            .stockDiscarded(stockDiscarded)
            .stockCount(stockCount)
            .build()
    }
}

internal fun InternalStockUseCaseTransaction.toDB(): StockUseCaseTransactionDB {
    return StockUseCaseTransactionDB(
        programUid = programUid()!!,
        sortOrder = sortOrder(),
        transactionType = transactionType(),
        distributedTo = distributedTo(),
        stockDistributed = stockDistributed(),
        stockDiscarded = stockDiscarded(),
        stockCount = stockCount(),
    )
}
