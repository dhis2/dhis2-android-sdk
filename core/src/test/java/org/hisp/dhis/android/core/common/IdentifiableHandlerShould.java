/*
 * Copyright (c) 2017, University of Oslo
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
package org.hisp.dhis.android.core.common;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class IdentifiableHandlerShould {

    private interface TestCall<A> {
        void call(A a);
    }
    
    abstract class NameableMockModelInterface extends BaseIdentifiableObjectModel implements StatementBinder {
    }

    @Mock
    private TestCall<BaseIdentifiableObject> testCall;

    @Mock
    private IdentifiableObjectStore<NameableMockModelInterface> store;

    @Mock
    private BaseIdentifiableObject pojo;

    @Mock
    private BaseIdentifiableObject pojo2;

    @Mock
    private NameableMockModelInterface model;

    @Mock
    private NameableMockModelInterface model2;

    @Mock
    private ModelBuilder<BaseIdentifiableObject, NameableMockModelInterface> modelBuilder;

    private GenericHandler<BaseIdentifiableObject, NameableMockModelInterface> genericHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(pojo.uid()).thenReturn("uid");
        when(pojo2.uid()).thenReturn("uid2");
        when(model.uid()).thenReturn("uid");
        when(model2.uid()).thenReturn("uid2");
        when(modelBuilder.buildModel(pojo)).thenReturn(model);
        when(modelBuilder.buildModel(pojo2)).thenReturn(model2);

        genericHandler = new IdentifiableHandlerImpl<BaseIdentifiableObject, NameableMockModelInterface>(store) {
            @Override
            protected void afterObjectPersisted(BaseIdentifiableObject BaseIdentifiableObject) {
                testCall.call(BaseIdentifiableObject);
            }
        };
    }

    @Test
    public void do_nothing_for_null() throws Exception {
        genericHandler.handle(null, null);
        verifyNoMoreInteractions(store);
    }

    @Test
    public void delete_when_deleted_is_true() throws Exception {
        when(pojo.deleted()).thenReturn(Boolean.TRUE);

        genericHandler.handle(pojo, modelBuilder);

        verify(store).delete(pojo.uid());
        verifyNoMoreInteractions(store);
    }

    @Test
    public void call_update_or_insert_when_deleted_is_false() throws Exception {
        when(pojo.deleted()).thenReturn(Boolean.FALSE);

        genericHandler.handle(pojo, modelBuilder);

        verify(store).updateOrInsert(any(NameableMockModelInterface.class));
        verifyNoMoreInteractions(store);
    }

    @Test
    public void call_after_object_persisted() throws Exception {
        genericHandler.handle(pojo, modelBuilder);
        verify(testCall).call(pojo);
    }

    @Test
    public void handle_multiple_pojos() throws Exception {
        genericHandler.handleMany(Arrays.asList(pojo, pojo2), modelBuilder);

        verify(store, times(2)).updateOrInsert(any(NameableMockModelInterface.class));
        verifyNoMoreInteractions(store);

        verify(testCall).call(pojo);
        verify(testCall).call(pojo2);
    }
}
