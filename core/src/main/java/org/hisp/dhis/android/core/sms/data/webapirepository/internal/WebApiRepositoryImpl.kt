package org.hisp.dhis.android.core.sms.data.webapirepository.internal

import android.util.Log
import io.reactivex.Single
import org.hisp.dhis.android.core.sms.data.webapirepository.internal.MetadataResponse.MetadataId
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository.GetMetadataIdsConfig
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.smscompression.models.SMSMetadata
import java.util.*

internal class WebApiRepositoryImpl(
    private val apiService: ApiService,
    private val dhisVersionManager: DHISVersionManager
) : WebApiRepository {

    override fun getMetadataIds(config: GetMetadataIdsConfig): Single<SMSMetadata> {
        return metadataCall(config).map { response: MetadataResponse ->
            val metadata = SMSMetadata()
            // TODO Server date has not timezone. We cannot use server date because it will be consider as local and
            //  potentially could be move some hours back or forth.
            //metadata.lastSyncDate = response.system().date();
            metadata.lastSyncDate = Date()
            metadata.categoryOptionCombos = mapIds(response.categoryOptionCombos())
            metadata.dataElements = mapIds(response.dataElements())
            metadata.organisationUnits = mapIds(response.organisationUnits())
            metadata.users = mapIds(response.users())
            metadata.trackedEntityTypes = mapIds(response.trackedEntityTypes())
            metadata.trackedEntityAttributes = mapIds(response.trackedEntityAttributes())
            metadata.programs = mapIds(response.programs())
            metadata
        }
    }

    private fun mapIds(ids: List<MetadataId>?): List<SMSMetadata.ID>? {
        return ids?.let { idList ->
            idList.map { makeID(it.id()) }
        }
    }

    private fun makeID(id: String): SMSMetadata.ID {
        return SMSMetadata.ID(id)
    }

    fun metadataCall(c: GetMetadataIdsConfig): Single<MetadataResponse> {
        if (dhisVersionManager.isGreaterOrEqualThan(DHISVersion.V2_35) && c.users) {
            Log.i(TAG, "Version greater or equal to 2.35. Skipping users query to metadata endpoint")
            c.users = false
        }
        return apiService.getMetadataIds(
            `val`(c.dataElements),
            `val`(c.categoryOptionCombos),
            `val`(c.organisationUnits),
            `val`(c.users),
            `val`(c.trackedEntityTypes),
            `val`(c.trackedEntityAttributes),
            `val`(c.programs)
        )
    }

    private fun `val`(enable: Boolean): String? {
        return if (enable) ApiService.GET_IDS else null
    }

    companion object {
        private val TAG = WebApiRepositoryImpl::class.java.simpleName
    }
}