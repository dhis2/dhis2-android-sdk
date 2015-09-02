package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.common.meta.State;

import java.util.List;

public interface IInterpretationCommentStore extends IStore<InterpretationComment> {
    List<InterpretationComment> filter(State state);

    List<InterpretationComment> filter(Interpretation interpretation, State state);
}
