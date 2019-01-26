package org.hisp.dhis.android.core.sms.data;

import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyIdentifiableCollectionRepository;
import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.user.UserModule;

import io.reactivex.Completable;
import io.reactivex.Single;

public class LocalDbRepositoryImpl implements LocalDbRepository {

    private final UserModule userModule;
    private final ReadOnlyIdentifiableCollectionRepository<CategoryOptionCombo> categoryOptionCombos;
    private final EventStore eventStore;
    private final static String DEFAULT_CATEGORY_OPTION_COMBO_CODE = "default";

    public LocalDbRepositoryImpl(UserModule userModule,
                                 ReadOnlyIdentifiableCollectionRepository<CategoryOptionCombo> categoryOptionCombos,
                                 EventStore eventStore) {
        this.userModule = userModule;
        this.categoryOptionCombos = categoryOptionCombos;
        this.eventStore = eventStore;
    }

    @Override
    public Single<String> getDefaultCategoryOptionCombo() {
        return Single.fromCallable(() -> {
            for (CategoryOptionCombo coc : categoryOptionCombos.get()) {
                if (DEFAULT_CATEGORY_OPTION_COMBO_CODE.equals(coc.code())) {
                    return coc.uid();
                }
            }
            return null;
        });
    }

    @Override
    public Single<String> getUserName() {
        return Single.fromCallable(() -> userModule.authenticatedUser.get().user());
    }

    @Override
    public Single<String> getGatewayNumber() {
        return null;
    }

    @Override
    public Completable setGatewayNumber(String number) {
        return null;
    }

    @Override
    public Single<String> getConfirmationSenderNumber() {
        return null;
    }

    @Override
    public Completable setConfirmationSenderNumber(String number) {
        return null;
    }

    @Override
    public Completable updateSubmissionState(BaseDataModel item, State state) {
        if (item instanceof Event) {
            String uid = ((Event) item).uid();
            if (uid != null) {
                return Completable.fromAction(() -> eventStore.setState(uid, state));
            }
            return Completable.error(new NullPointerException("Event uid param null"));
        }
        return Completable.error(new IllegalArgumentException("Not supported data type"));
    }
}
