import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FilterSetting")
internal data class FilterSettingDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int = 0,
    val scope: String?,
    val filterType: String?,
    val uid: String?,
    val sort: Int?,
    val filter: Int?,
)
