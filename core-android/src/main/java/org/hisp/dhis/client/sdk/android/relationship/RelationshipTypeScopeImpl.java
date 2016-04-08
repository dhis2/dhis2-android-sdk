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

package org.hisp.dhis.client.sdk.android.relationship;


import org.hisp.dhis.client.sdk.core.relationship.RelationshipTypeService;
import org.hisp.dhis.client.sdk.models.relationship.RelationshipType;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class RelationshipTypeScopeImpl implements RelationshipTypeScope {

    private RelationshipTypeService mRelationshipTypeService;

    public RelationshipTypeScopeImpl(RelationshipTypeService relationshipTypeService) {
        this.mRelationshipTypeService = relationshipTypeService;
    }

    @Override
    public Observable<RelationshipType> get(final long id) {
        return Observable.create(new Observable.OnSubscribe<RelationshipType>() {
            @Override
            public void call(Subscriber<? super RelationshipType> subscriber) {
                try {
                    RelationshipType relationshipType = mRelationshipTypeService.get(id);
                    subscriber.onNext(relationshipType);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<RelationshipType> get(final String uid) {
        return Observable.create(new Observable.OnSubscribe<RelationshipType>() {
            @Override
            public void call(Subscriber<? super RelationshipType> subscriber) {
                try {
                    RelationshipType relationshipType = mRelationshipTypeService.get(uid);
                    subscriber.onNext(relationshipType);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<List<RelationshipType>> list() {
        return Observable.create(new Observable.OnSubscribe<List<RelationshipType>>() {
            @Override
            public void call(Subscriber<? super List<RelationshipType>> subscriber) {
                try {
                    List<RelationshipType> relationshipTypes = mRelationshipTypeService.list();
                    subscriber.onNext(relationshipTypes);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> save(final RelationshipType object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mRelationshipTypeService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }

    @Override
    public Observable<Boolean> remove(final RelationshipType object) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    boolean status = mRelationshipTypeService.save(object);
                    subscriber.onNext(status);
                } catch (Throwable throwable) {
                    subscriber.onError(throwable);
                }

                subscriber.onCompleted();
            }
        });
    }
}
