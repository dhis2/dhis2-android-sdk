/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.client.sdk.core.common.controllers;

import org.hisp.dhis.client.sdk.core.common.network.ApiException;
import org.hisp.dhis.client.sdk.core.common.persistence.Store;
import org.hisp.dhis.client.sdk.models.common.base.Model;
import org.hisp.dhis.client.sdk.utils.Logger;

import java.net.HttpURLConnection;

public abstract class AbsDataController<T extends Model> {
    protected final Logger logger;
    protected final Store<T> store;

    public AbsDataController(Logger logger, Store<T> store) {
        this.logger = logger;
        this.store = store;
    }

    protected boolean handleApiException(ApiException exception, T model) throws ApiException {
        switch (exception.getKind()) {
            case HTTP: {
                switch (exception.getResponse().getStatus()) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED:
                        // user credentials are not valid
                    case HttpURLConnection.HTTP_FORBIDDEN: {
                        // client does not have access to server
                        // for example, oAuth2 token may expire
                        throw exception;
                    }

                    // given resource was removed, react accordingly
                    case HttpURLConnection.HTTP_NOT_FOUND: {
                        if (store != null && model != null) {
                            store.delete(model);
                            return true;
                        }

                        return false;
                    }

                    // return control to client code,
                    // conflict should be resolved
                    case HttpURLConnection.HTTP_CONFLICT: {
                        return false;
                    }

                    case HttpURLConnection.HTTP_BAD_REQUEST:
                    case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    case HttpURLConnection.HTTP_NOT_IMPLEMENTED: {
                        logger.e("ApiException", "HTTP error", exception);
                        // log error
                        throw exception;
                    }
                    default: {
                        throw exception;
                    }
                }
            }
            // if it is a network problem (like timeout or something else, do we really
            // want to continue execution? or should we retry request once?
            case NETWORK: {
                throw exception;
            }
            case CONVERSION:
            case UNEXPECTED: {
                // These types of errors are considered to be unrecoverable,
                throw exception;
            }
            default: {
                throw exception;
            }
        }
    }
}
