import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ValueTypeDeviceRendering",
    indices = [
        Index(value = ["uid", "deviceType"], unique = true)
    ]
)
internal data class ValueTypeDeviceRenderingDB(
    @PrimaryKey(autoGenerate = true)
    val _id: Int,
    val uid: String?,
    val objectTable: String?,
    val deviceType: String?,
    val type: String?,
    val min: Int?,
    val max: Int?,
    val step: Int?,
    val decimalPoints: Int?
)
