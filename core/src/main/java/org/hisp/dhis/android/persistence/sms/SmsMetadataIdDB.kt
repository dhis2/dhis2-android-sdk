package org.hisp.dhis.android.persistence.sms

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSMetadataId
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.smscompression.SMSConsts

@Entity(tableName = "SmsMetadataId")
internal data class SmsMetadataIdDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val type: String?,
    val uid: String?,
) : EntityDB<SMSMetadataId> {

    override fun toDomain(): SMSMetadataId {
        return SMSMetadataId.builder()
            .id(id?.toLong())
            .type(type?.let { SMSConsts.MetadataType.valueOf(it) })
            .uid(uid)
            .build()
    }

    companion object {
        fun SMSMetadataId.toDB(): SmsMetadataIdDB {
            return SmsMetadataIdDB(
                type = type().name,
                uid = uid()
            )
        }
    }
}
