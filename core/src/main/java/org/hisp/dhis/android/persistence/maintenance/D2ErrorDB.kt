package org.hisp.dhis.android.persistence.maintenance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.util.dateFormat
import org.hisp.dhis.android.core.util.toJavaDate
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "D2Error")
internal data class D2ErrorDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val resourceType: String?,
    val uid: String?,
    val url: String?,
    val errorComponent: String?,
    val errorCode: String?,
    val errorDescription: String?,
    val httpErrorCode: Int?,
    val created: String?,
) : EntityDB<D2Error> {
    override fun toDomain(): D2Error {
        return D2Error.builder().apply {
            url(url)
            errorComponent?.let { errorComponent(D2ErrorComponent.valueOf(it)) }
            errorCode?.let { errorCode(D2ErrorCode.valueOf(it)) }
            errorDescription(errorDescription)
            httpErrorCode(httpErrorCode)
            created(created.toJavaDate())
        }.build()
    }
}

internal fun D2Error.toDB(): D2ErrorDB {
    return D2ErrorDB(
        resourceType = null,
        uid = null,
        url = url(),
        errorComponent = errorComponent()?.name,
        errorCode = errorCode().name,
        errorDescription = errorDescription(),
        httpErrorCode = httpErrorCode(),
        created = created().dateFormat()
    )
}
