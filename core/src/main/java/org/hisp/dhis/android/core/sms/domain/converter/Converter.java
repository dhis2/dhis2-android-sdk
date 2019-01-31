package org.hisp.dhis.android.core.sms.domain.converter;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseDataModel;

import java.util.Collection;

import io.reactivex.Single;

public abstract class Converter<T extends BaseDataModel, P> {

    /**
     * @param dataItem object to convert
     * @param params   additional params that will be needed to convert given object and are not
     *                 included in the object itself
     * @return text ready to be sent by sms
     */
    public abstract String format(@NonNull T dataItem, P params);

    /**
     * @return a texts list that when they exists in a response confirmation sms, it means that
     * submission was successfully received
     */
    public abstract Collection<String> getConfirmationRequiredTexts(T dataObject);

    public abstract Single<P> getParamsTask();

    public Single<String> format(@NonNull T dataItem) {
        Single<P> paramsTask = getParamsTask();
        if (paramsTask != null) return paramsTask.map(params -> format(dataItem, params));
        return Single.fromCallable(() -> format(dataItem, null));
    }
}
