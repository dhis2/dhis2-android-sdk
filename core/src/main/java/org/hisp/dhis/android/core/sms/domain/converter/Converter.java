package org.hisp.dhis.android.core.sms.domain.converter;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseDataModel;

import java.util.Collection;

public interface Converter<P extends Converter.DataToConvert, T extends BaseDataModel> {
    /**
     * @param dataItem object to convert
     * @return text ready to be sent by sms
     */
    String format(@NonNull P dataItem);

    /**
     * @return a texts list, that when they exists in a response confirmation sms, it means that
     * submission was successfully received
     */
    Collection<String> getConfirmationRequiredTexts(T dataObject);

    interface DataToConvert {
        BaseDataModel getDataModel();
    }
}
