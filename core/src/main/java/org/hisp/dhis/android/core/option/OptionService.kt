package org.hisp.dhis.android.core.option

import io.reactivex.Single

interface OptionService {
    fun blockingSearchForOptions(
        optionSetUid:String,
        searchText: String,
        optionToHideUids: List<String>,
        optionToShowUids: List<String>,
    ):List<Option>

    fun searchForOptions(
        optionSetUid:String,
        searchText: String,
        optionToHideUids: List<String>,
        optionToShowUids: List<String>,
    ): Single<List<Option>>
}