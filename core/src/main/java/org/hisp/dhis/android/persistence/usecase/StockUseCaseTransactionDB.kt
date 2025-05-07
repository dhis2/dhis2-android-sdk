import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "StockUseCaseTransaction",
    foreignKeys = [
        ForeignKey(
            entity = StockUseCaseDB::class,
            parentColumns = ["uid"],
            childColumns = ["programUid"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["programUid"]),
    ],
)
internal data class StockUseCaseTransactionDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val programUid: String,
    val sortOrder: Int?,
    val transactionType: String?,
    val distributedTo: String?,
    val stockDistributed: String?,
    val stockDiscarded: String?,
    val stockCount: String?,
)
