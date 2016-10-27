package org.hisp.dhis.client.sdk.models;

import org.hisp.dhis.client.sdk.models.common.BaseIdentifiableObject;

import java.util.Date;

public final class BaseIdentifiableObjectVerifier {
//    private BaseIdentifiableObjectVerifier() {
//        // no instances
//    }

    //******************************************************************************
    //
    // Generic methods to be used in tests
    //
    //******************************************************************************

    public void validate(BaseIdentifiableObject bio)                            { /* validate "new" immutable object */
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void validateWithUid(BaseIdentifiableObject bio, String uid)         { /* validate "new" immutable object */ }

    public void validateWithCreated(BaseIdentifiableObject bio, Date date)      { /* validate "new" immutable object */ }

    public void validateWithLastUpdated(BaseIdentifiableObject bio, Date date)  { /* validate "new" immutable object */ }

//    public static <T extends BaseIdentifiableObject> void validate(T bio) {
//        T.validate(bio);
//    }
}
