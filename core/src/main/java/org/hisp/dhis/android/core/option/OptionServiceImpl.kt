package org.hisp.dhis.android.core.option

import dagger.Reusable
import io.reactivex.Single
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope

@Reusable
class OptionServiceImpl @Inject constructor(
    private val optionRepository: OptionCollectionRepository
) : OptionService {

    override fun blockingSearchForOptions(
        optionSetUid: String,
        searchText: String,
        optionToHideUids: List<String>,
        optionToShowUids: List<String>,
    ): List<Option> {
        var repository = optionRepository
            .byOptionSetUid().eq(optionSetUid)

        if (optionToShowUids.isNotEmpty()) {
            repository = repository.byUid().`in`(optionToShowUids)
        }
        if (optionToHideUids.isNotEmpty()) {
            repository = repository.byUid().notIn(optionToHideUids)
        }
        if (searchText.isNotEmpty()) {
            repository = repository.byDisplayName().like("%$searchText%")
        }

        return repository.orderBySortOrder(RepositoryScope.OrderByDirection.DESC).blockingGet()
    }

    override fun searchForOptions(
        optionSetUid: String,
        searchText: String,
        optionToHideUids: List<String>,
        optionToShowUids: List<String>,
    ): Single<List<Option>> {
        return Single.just(blockingSearchForOptions(optionSetUid, searchText, optionToHideUids, optionToShowUids))
    }
}
