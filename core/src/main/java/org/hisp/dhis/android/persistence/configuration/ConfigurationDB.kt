package org.hisp.dhis.android.persistence.configuration

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.hisp.dhis.android.core.configuration.internal.Configuration
import org.hisp.dhis.android.persistence.common.EntityDB

@Entity(
    tableName = "Configuration",
    indices = [
        Index(value = ["serverUrl"], unique = true),
    ],
)
internal data class ConfigurationDB(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    val id: Int? = 0,
    val serverUrl: String,
) : EntityDB<Configuration> {

    override fun toDomain(): Configuration {
        return Configuration.builder()
            .id(id?.toLong())
            .serverUrl(serverUrl)
            .build()
    }
}

internal fun Configuration.toDB(): ConfigurationDB {
    return ConfigurationDB(
        serverUrl = serverUrl(),
    )
}
