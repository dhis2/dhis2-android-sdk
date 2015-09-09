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

package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.common.meta.State;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public final class InterpretationCommentServiceTests {
    private IInterpretationCommentService service;

    @Before
    public void setUp() {
        service = new InterpretationCommentService(
                mock(IInterpretationCommentStore.class));
    }

    @Test
    public void deleteCommentShouldChangeState() {
        InterpretationComment interpretationCommentStateToPost = new InterpretationComment();
        InterpretationComment interpretationCommentStateSynced = new InterpretationComment();

        interpretationCommentStateToPost.setState(State.TO_POST);
        interpretationCommentStateSynced.setState(State.SYNCED);

        service.deleteComment(interpretationCommentStateToPost);
        service.deleteComment(interpretationCommentStateSynced);

        assertEquals(interpretationCommentStateToPost.getState(), State.TO_DELETE);
        assertEquals(interpretationCommentStateSynced.getState(), State.TO_DELETE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteCommentShouldFailOnNullComment() {
        service.deleteComment(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCommentTextShouldFailOnNullComment() {
        String comment = "Some very interesting comment is here";
        service.updateCommentText(null, comment);
    }

    @Test
    public void updateCommentTextShouldChangeState() {
        String newComment = "Another very useful comment";

        InterpretationComment commentToPost = new InterpretationComment();
        InterpretationComment commentSynced = new InterpretationComment();

        commentToPost.setText("Some fancy comment");
        commentToPost.setState(State.TO_POST);

        commentSynced.setText("Some fancy comment");
        commentSynced.setState(State.SYNCED);

        service.updateCommentText(commentToPost, newComment);
        service.updateCommentText(commentSynced, newComment);

        assertEquals(commentToPost.getState(), State.TO_POST);
        assertEquals(commentSynced.getState(), State.TO_UPDATE);

        assertEquals(commentToPost.getText(), newComment);
        assertEquals(commentSynced.getText(), newComment);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCommentTextShouldFailOnWrongState() {
        InterpretationComment commentToDelete = new InterpretationComment();
        commentToDelete.setState(State.TO_DELETE);

        String commentText = "comment";

        service.updateCommentText(commentToDelete, commentText);
    }
 }
