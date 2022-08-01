/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.arch.storage.internal;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory;

import java.io.IOException;

@SuppressWarnings({"PMD.PreserveStackTrace"})
public class JsonKeyValueStoreImpl<O> implements ObjectKeyValueStore<O> {

    private final KeyValueStore secureStore;
    private final String key;
    private final Class<O> clazz;

    private O object;

    public JsonKeyValueStoreImpl(KeyValueStore secureStore, String key, Class<O> clazz) {
        this.secureStore = secureStore;
        this.key = key;
        this.clazz = clazz;
    }

    public void set(O o) {
        try {
            String strObject = ObjectMapperFactory.objectMapper().writeValueAsString(o);
            this.secureStore.setData(key, strObject);
            this.object = o;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't persist object in key value store");
        }
    }

    public O get() {
        if (this.object == null) {
            String strObject = this.secureStore.getData(key);
            if (strObject == null) {
                return null;
            } else {
                try {
                    return ObjectMapperFactory.objectMapper().readValue(strObject, clazz);
                } catch (IOException e) {
                    throw new RuntimeException("Couldn't read object from key value store");
                }
            }

        } else {
            return this.object;
        }
    }

    public void remove() {
        this.object = null;
        this.secureStore.removeData(key);
    }
}