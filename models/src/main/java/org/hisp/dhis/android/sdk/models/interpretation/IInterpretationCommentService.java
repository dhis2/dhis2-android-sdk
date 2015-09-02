package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.common.IService;

public interface IInterpretationCommentService extends IService {
    void deleteComment(InterpretationComment comment);

    void updateCommentText(InterpretationComment comment, String text);
}
