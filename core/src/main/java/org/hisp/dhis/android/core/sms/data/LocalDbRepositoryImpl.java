package org.hisp.dhis.android.core.sms.data;

import android.content.Context;

import org.hisp.dhis.android.core.category.CategoryOptionCombo;
import org.hisp.dhis.android.core.category.CategoryOptionComboCollectionRepository;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.user.UserModule;

import java.io.IOException;

import io.reactivex.Completable;
import io.reactivex.Single;

public class LocalDbRepositoryImpl implements LocalDbRepository {

    private final Context context;
    private final UserModule userModule;
    private final CategoryOptionComboCollectionRepository categoryOptionCombos;
    private final EventStore eventStore;
    private final static String DEFAULT_CATEGORY_OPTION_COMBO_CODE = "default";
    private final static String CONFIG_FILE = "smsconfig";
    private final static String KEY_GATEWAY = "gateway";
    private final static String KEY_CONFIRMATION_SENDER = "confirmationsender";

    public LocalDbRepositoryImpl(Context ctx,
                                 UserModule userModule,
                                 CategoryOptionComboCollectionRepository categoryOptionCombos,
                                 EventStore eventStore) {
        this.context = ctx;
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
        return Single.fromCallable(() ->
                context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                        .getString(KEY_GATEWAY, null)
        );
    }

    @Override
    public Completable setGatewayNumber(String number) {
        return Completable.fromAction(() -> {
            boolean result = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                    .edit().putString(KEY_GATEWAY, number).commit();
            if (!result) {
                throw new IOException("Failed writing gateway number to local storage");
            }
        });
    }

    @Override
    public Single<String> getConfirmationSenderNumber() {
        return Single.fromCallable(() ->
                context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                        .getString(KEY_CONFIRMATION_SENDER, null)
        );
    }

    @Override
    public Completable setConfirmationSenderNumber(String number) {
        return Completable.fromAction(() -> {
            boolean result = context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE)
                    .edit().putString(KEY_CONFIRMATION_SENDER, number).commit();
            if (!result) {
                throw new IOException("Failed writing confirmation sender number to local storage");
            }
        });
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
