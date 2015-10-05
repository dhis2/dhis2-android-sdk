/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.corejava.interpretation;

import org.hisp.dhis.android.sdk.models.common.state.Action;
import org.hisp.dhis.android.sdk.models.common.state.IStateStore;
import org.hisp.dhis.android.sdk.models.interpretation.InterpretationComment;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class InterpretationCommentService implements IInterpretationCommentService {
    private final IInterpretationCommentStore interpretationCommentStore;
    private final IStateStore stateStore;

    public InterpretationCommentService(IInterpretationCommentStore interpretationCommentStore, IStateStore stateStore) {
        this.interpretationCommentStore = interpretationCommentStore;
        this.stateStore = stateStore;
    }

    /**
     * Performs soft delete of model. If Action of object was SYNCED, it will be set to TO_DELETE.
     * If the model is persisted only in the local database, it will be removed immediately.
     *
     * @param interpretationComment comment to delete.
     */
    @Override
    public void remove(InterpretationComment interpretationComment) {
        isNull(interpretationComment, "interpretationComment should not be null");

        Action action = stateStore.queryActionForModel(interpretationComment);
        if (Action.TO_POST.equals(action)) {
            stateStore.deleteActionForModel(interpretationComment);
            interpretationCommentStore.delete(interpretationComment);
        } else {
            stateStore.saveActionForModel(interpretationComment, Action.TO_DELETE);
            interpretationCommentStore.save(interpretationComment);
        }
    }

    /**
     * Method modifies the original comment text and sets TO_UPDATE as state,
     * if the object was received from server. If the model was persisted only locally,
     * the Action will be the TO_POST.
     *
     * @param interpretationComment comment which should be updated.
     * @param text                  Edited text of comment.
     */
    @Override
    public void update(InterpretationComment interpretationComment, String text) {
        isNull(interpretationComment, "interpretationComment must not be null");

        Action action = stateStore.queryActionForModel(interpretationComment);
        if (Action.TO_DELETE.equals(action)) {
            throw new IllegalArgumentException("The text of interpretation comment with Action." +
                    "TO_DELETE cannot be updated");
        }

        if (!Action.TO_POST.equals(action)) {
            stateStore.saveActionForModel(interpretationComment, Action.TO_UPDATE);
        }

        interpretationComment.setText(text);
        interpretationCommentStore.save(interpretationComment);
    }
}
