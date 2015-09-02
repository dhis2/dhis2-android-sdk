package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.common.meta.State;

public final class InterpretationCommentService implements IInterpretationCommentService {
    private final IInterpretationCommentStore interpretationCommentStore;

    public InterpretationCommentService(IInterpretationCommentStore interpretationCommentStore) {
        this.interpretationCommentStore = interpretationCommentStore;
    }

    /**
     * Performs soft delete of model. If State of object was SYNCED, it will be set to TO_DELETE.
     * If the model is persisted only in the local database, it will be removed immediately.
     */
    @Override
    public void deleteComment(InterpretationComment comment) {
        if (State.TO_POST.equals(comment.getState())) {
            interpretationCommentStore.delete(comment);
        } else {
            comment.setState(State.TO_DELETE);
            interpretationCommentStore.save(comment);
        }
    }

    /**
     * Method modifies the original comment text and sets TO_UPDATE as state,
     * if the object was received from server. If the model was persisted only locally,
     * the State will be the TO_POST.
     *
     * @param text Edited text of comment.
     */
    @Override
    public void updateCommentText(InterpretationComment comment, String text) {
        comment.setText(text);

        if (comment.getState() != State.TO_DELETE &&
                comment.getState() != State.TO_POST) {
            comment.setState(State.TO_UPDATE);
        }

        interpretationCommentStore.save(comment);
    }
}
