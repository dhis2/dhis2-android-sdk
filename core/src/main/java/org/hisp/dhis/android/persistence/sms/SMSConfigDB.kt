package org.hisp.dhis.android.persistence.sms

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import org.hisp.dhis.android.core.datastore.KeyValuePair
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "SMSConfig")
internal data class SMSConfigDB(
    @PrimaryKey
    val key: String,
    val value: String?,
) : EntityDB<KeyValuePair> {
    override fun toDomain(): KeyValuePair {
        return KeyValuePair.builder()
            .key(key)
            .value(value)
            .build()
    }
}

internal fun KeyValuePair.toDB(): SMSConfigDB {
    return SMSConfigDB(
        key = key(),
        value = value(),
    )
}
