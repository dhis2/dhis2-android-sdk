package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.common.IStore;

import java.util.List;

public interface IInterpretationElementStore extends IStore<InterpretationElement> {
    List<InterpretationElement> query(Interpretation interpretation);
}
