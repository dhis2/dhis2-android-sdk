package org.hisp.dhis.android.persistence.configuration

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.configuration.internal.Configuration
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(tableName = "Configuration")
internal data class ConfigurationDB(
    @PrimaryKey
    val serverUrl: String,
) : EntityDB<Configuration> {

    override fun toDomain(): Configuration {
        return Configuration.builder()
            .serverUrl(serverUrl)
            .build()
    }
}

internal fun Configuration.toDB(): ConfigurationDB {
    return ConfigurationDB(
        serverUrl = serverUrl(),
    )
}
