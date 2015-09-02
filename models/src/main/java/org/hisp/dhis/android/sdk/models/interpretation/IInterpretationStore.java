package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.common.IStore;
import org.hisp.dhis.android.sdk.models.common.meta.State;

import java.util.List;

public interface IInterpretationStore extends IStore<Interpretation> {
    List<Interpretation> filter(State state);
}
