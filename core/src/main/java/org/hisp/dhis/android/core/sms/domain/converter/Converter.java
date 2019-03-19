package org.hisp.dhis.android.core.sms.domain.converter;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.smscompression.SMSSubmissionWriter;
import org.hisp.dhis.smscompression.models.Metadata;

import java.util.Collection;

import io.reactivex.Single;

public abstract class Converter<P extends Converter.DataToConvert, T extends BaseDataModel> {
    public Single<SMSSubmissionWriter> getSmsSubmissionWriter(Metadata metadata) {
        return Single.just(new SMSSubmissionWriter(metadata));
    }

    /**
     * @param dataItem object to convert
     * @return text ready to be sent by sms
     */
    public abstract Single<String> format(@NonNull P dataItem);

    /**
     * @return a texts list, that when they exists in a response confirmation sms, it means that
     * submission was successfully received
     */
    public abstract Single<? extends Collection<String>> getConfirmationRequiredTexts(T dataObject);

    public interface DataToConvert {
        BaseDataModel getDataModel();
    }
}
