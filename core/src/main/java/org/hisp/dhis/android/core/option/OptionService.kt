package org.hisp.dhis.android.core.option

import io.reactivex.Single

interface OptionService {
    fun blockingSearchForOptions(
        optionSetUid: String,
        searchText: String? = null,
        optionToHideUids: List<String>? = null,
        optionToShowUids: List<String>? = null,
    ): List<Option>

    fun searchForOptions(
        optionSetUid: String,
        searchText: String? = null,
        optionToHideUids: List<String>? = null,
        optionToShowUids: List<String>? = null,
    ): Single<List<Option>>
}
