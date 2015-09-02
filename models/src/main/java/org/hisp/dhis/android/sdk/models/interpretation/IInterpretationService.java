package org.hisp.dhis.android.sdk.models.interpretation;

import org.hisp.dhis.android.sdk.models.common.IService;
import org.hisp.dhis.android.sdk.models.dashboard.DashboardItem;
import org.hisp.dhis.android.sdk.models.user.User;

import java.util.List;

public interface IInterpretationService extends IService {
    InterpretationComment addComment(Interpretation interpretation, User user, String text);

    Interpretation createInterpretation(DashboardItem item, User user, String text);

    void updateInterpretationText(Interpretation interpretation, String text);

    void deleteInterpretation(Interpretation interpretation);

    void setInterpretationElements(Interpretation interpretation, List<InterpretationElement> elements);

    List<InterpretationElement> getInterpretationElements(Interpretation interpretation);
}
