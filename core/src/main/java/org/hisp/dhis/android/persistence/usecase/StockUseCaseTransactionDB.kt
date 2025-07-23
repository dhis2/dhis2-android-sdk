package org.hisp.dhis.android.persistence.usecase

import androidx.room.Entity
import androidx.room.ForeignKey
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
    primaryKeys = ["programUid", "transactionType"],
)
internal data class StockUseCaseTransactionDB(
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
