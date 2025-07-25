package org.hisp.dhis.android.persistence.sms

import androidx.room.Entity
import org.hisp.dhis.android.core.sms.data.localdbrepository.internal.SMSMetadataId
import org.hisp.dhis.android.persistence.common.EntityDB
import org.hisp.dhis.smscompression.SMSConsts

@Entity(
    tableName = "SmsMetadataId",
    primaryKeys = ["type", "uid"],
)
internal data class SMSMetadataIdDB(
    val type: String,
    val uid: String,
) : EntityDB<SMSMetadataId> {

    override fun toDomain(): SMSMetadataId {
        return SMSMetadataId.builder()
            .type(type.let { SMSConsts.MetadataType.valueOf(it) })
            .uid(uid)
            .build()
    }
}

internal fun SMSMetadataId.toDB(): SMSMetadataIdDB {
    return SMSMetadataIdDB(
        type = type().name,
        uid = uid(),
    )
}
