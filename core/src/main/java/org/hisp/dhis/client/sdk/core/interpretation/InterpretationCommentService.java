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

package org.hisp.dhis.client.sdk.core.interpretation;

import org.hisp.dhis.client.sdk.core.common.IStateStore;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;
import org.hisp.dhis.client.sdk.models.common.Access;
import org.hisp.dhis.client.sdk.models.common.state.Action;
import org.hisp.dhis.client.sdk.models.interpretation.Interpretation;
import org.hisp.dhis.client.sdk.models.interpretation.InterpretationComment;
import org.hisp.dhis.client.sdk.models.user.User;
import org.hisp.dhis.client.sdk.models.utils.Preconditions;
import org.joda.time.DateTime;

public class InterpretationCommentService implements IInterpretationCommentService {
    private final IInterpretationCommentStore interpretationCommentStore;
    private final IStateStore stateStore;

    public InterpretationCommentService(IInterpretationCommentStore interpretationCommentStore,
                                        IStateStore stateStore) {
        this.interpretationCommentStore = interpretationCommentStore;
        this.stateStore = stateStore;
    }

    @Override
    public InterpretationComment create(Interpretation interpretation, User user, String text) {
        Preconditions.isNull(interpretation, "Interpretation object must not be null");
        Preconditions.isNull(user, "User object must not be null");
        Preconditions.isNull(text, "InterpretationComment text must not be null");

        DateTime created = DateTime.now();
        Access access = Access.createDefaultAccess();

        InterpretationComment interpretationComment = new InterpretationComment();
        interpretationComment.setUId(CodeGenerator.generateCode());
        interpretationComment.setName(text);
        interpretationComment.setDisplayName(text);
        interpretationComment.setCreated(created);
        interpretationComment.setLastUpdated(created);
        interpretationComment.setAccess(access);
        interpretationComment.setText(text);
        interpretationComment.setUser(user);
        interpretationComment.setInterpretation(interpretation);

        return interpretationComment;
    }

    /**
     * Performs soft delete of model. If Action of object was SYNCED, it will be set to TO_DELETE.
     * If the model is persisted only in the local database, it will be removed immediately.
     *
     * @param interpretationComment comment to delete.
     */
    @Override
    public boolean remove(InterpretationComment interpretationComment) {
        Preconditions.isNull(interpretationComment, "InterpretationComment object must not be " +
                "null");

        Action action = stateStore.queryActionForModel(interpretationComment);
        if (action == null) {
            return false;
        }

        boolean status = false;
        switch (action) {
            case SYNCED:
            case TO_UPDATE: {
                status = stateStore.saveActionForModel(interpretationComment, Action.TO_DELETE);
                break;
            }
            case TO_POST: {
                status = interpretationCommentStore.delete(interpretationComment);
                break;
            }
            case TO_DELETE: {
                status = false;
                break;
            }
        }

        return status;
    }

    @Override
    public boolean save(InterpretationComment object) {
        Preconditions.isNull(object, "InterpretationComment object must not be null");

        Action action = stateStore.queryActionForModel(object);
        if (action == null) {
            boolean status = interpretationCommentStore.save(object);

            if (status) {
                status = stateStore.saveActionForModel(object, Action.TO_POST);
            }

            return status;
        }

        boolean status = false;
        switch (action) {
            case TO_POST:
            case TO_UPDATE: {
                status = interpretationCommentStore.save(object);
                break;
            }
            case SYNCED: {
                status = interpretationCommentStore.save(object);

                if (status) {
                    status = stateStore.saveActionForModel(object, Action.TO_UPDATE);
                }
                break;
            }
            case TO_DELETE: {
                status = false;
                break;
            }

        }

        return status;
    }
}
