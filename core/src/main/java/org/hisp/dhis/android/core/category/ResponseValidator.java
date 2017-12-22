package org.hisp.dhis.android.core.category;


import org.hisp.dhis.android.core.common.Payload;

import retrofit2.Response;

public class ResponseValidator<E> {

    boolean isValid(Response<Payload<E>> response) {
        return response.isSuccessful() && response.body().items() != null;
    }
}
