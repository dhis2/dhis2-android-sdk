package org.hisp.dhis.android.core.sms.domain.converter;

import android.annotation.SuppressLint;
import android.util.Base64;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.smscompression.SMSSubmissionWriter;
import org.hisp.dhis.smscompression.models.Metadata;

import io.reactivex.Single;

public abstract class Converter<P extends Converter.DataToConvert> {
    public Single<SMSSubmissionWriter> getSmsSubmissionWriter(Metadata metadata) {
        return Single.just(new SMSSubmissionWriter(metadata));
    }

    @SuppressLint("NewApi")
    String base64(byte[] bytes) {
        String encoded;
        try {
            encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Throwable t) {
            encoded = null;
            // not android, so will try with pure java
        }
        if (encoded == null) {
            encoded = java.util.Base64.getEncoder().encodeToString(bytes);
        }
        return encoded;
    }

    /**
     * @param dataItem object to convert
     * @return text ready to be sent by sms
     */
    public abstract Single<String> format(@NonNull P dataItem);

    public interface DataToConvert {
        BaseDataModel getDataModel();
    }
}
