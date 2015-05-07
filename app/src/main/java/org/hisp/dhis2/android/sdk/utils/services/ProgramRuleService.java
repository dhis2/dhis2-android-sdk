package org.hisp.dhis2.android.sdk.utils.services;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis2.android.sdk.controllers.datavalues.DataValueController;
import org.hisp.dhis2.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis2.android.sdk.persistence.models.DataElement;
import org.hisp.dhis2.android.sdk.persistence.models.DataValue;
import org.hisp.dhis2.android.sdk.persistence.models.Event;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramRule;
import org.hisp.dhis2.android.sdk.persistence.models.ProgramRuleVariable;
import org.hisp.dhis2.android.sdk.utils.support.ExpressionUtils;
import org.hisp.dhis2.android.sdk.utils.support.MathUtils;
import org.hisp.dhis2.android.sdk.utils.support.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Simen Skogly Russnes on 29.04.15.
 */
public class ProgramRuleService {

    private static final String CLASS_TAG = ProgramRuleService.class.getSimpleName();

    private static final Pattern CONDITION_PATTERN = Pattern.compile( "#\\{(.+)\\}" );

    public static boolean evaluate( final String condition, Event event ) {

        StringBuffer buffer = new StringBuffer();

        Matcher matcher = CONDITION_PATTERN.matcher( condition );

        while ( matcher.find() ) {

            String variableName = matcher.group( 1 );
            ProgramRuleVariable programRuleVariable = MetaDataController.getProgramRuleVariableByName(variableName);
            if(programRuleVariable==null) {
                return false;
            }
            DataElement dataElement = MetaDataController.getDataElement(programRuleVariable.dataElement);
            DataValue dataValue = null;
            if(event.getDataValues()!=null) {
                for(DataValue dv: event.getDataValues()) {
                    if(dv.dataElement.equals(programRuleVariable.dataElement)) {
                        dataValue = dv;
                        break;
                    }
                }
            }

            String value;
            if(dataValue!=null && dataValue.value!=null && !dataValue.value.isEmpty()) {
                value = dataValue.value;
            } else {
                return false;
            }

            value = '\'' + value + '\'';
            matcher.appendReplacement( buffer, value );
        }

        String conditionReplaced = TextUtils.appendTail(matcher, buffer);
        Log.d(CLASS_TAG, condition);
        Log.d(CLASS_TAG, conditionReplaced);

        return ExpressionUtils.isTrue(conditionReplaced, null);
    }

    public static List<String> getDataElementsInRule(ProgramRule programRule) {
        String condition = programRule.condition;
        Matcher matcher = CONDITION_PATTERN.matcher( condition );
        List<String> dataElementsInRule = new ArrayList<>();

        while(matcher.find()) {
            String variableName = matcher.group( 1 );
            Log.d("programruleservice: ", variableName);
            ProgramRuleVariable programRuleVariable = MetaDataController.getProgramRuleVariableByName(variableName);
            if(programRuleVariable!=null) {
                Log.d("programruleservice", "the dataelement is: " + programRuleVariable.dataElement);
                dataElementsInRule.add(programRuleVariable.dataElement);
            }
        }

        return dataElementsInRule;
    }
}
