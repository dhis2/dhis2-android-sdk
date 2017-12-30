package org.hisp.dhis.rules.functions;

import org.hisp.dhis.rules.RuleVariableValue;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings({
        "PMD.CyclomaticComplexity",
        "PMD.StdCyclomaticComplexity"
})
public abstract class RuleFunction {
    static final String DATE_PATTERN = "yyyy-MM-dd";

    @Nonnull
    public abstract String evaluate(@Nonnull List<String> arguments,
            Map<String, RuleVariableValue> valueMap);

    @Nullable
    public static RuleFunction create(@Nonnull String fun) {
        switch (fun) {
            case RuleFunctionDaysBetween.D2_DAYS_BETWEEN:
                return RuleFunctionDaysBetween.create();
            case RuleFunctionWeeksBetween.D2_WEEKS_BETWEEN:
                return RuleFunctionWeeksBetween.create();
            case RuleFunctionMonthsBetween.D2_MONTHS_BETWEEN:
                return RuleFunctionMonthsBetween.create();
            case RuleFunctionYearsBetween.D2_YEARS_BETWEEN:
                return RuleFunctionYearsBetween.create();
            case RuleFunctionHasValue.D2_HAS_VALUE:
                return RuleFunctionHasValue.create();
            case RuleFunctionFloor.D2_FLOOR:
                return RuleFunctionFloor.create();
            case RuleFunctionCeil.D2_CEIL:
                return RuleFunctionCeil.create();
            case RuleFunctionAddDays.D2_ADD_DAYS:
                return RuleFunctionAddDays.create();
            case RuleFunctionConcatenate.D2_CONCATENATE:
                return RuleFunctionConcatenate.create();
            case RuleFunctionCount.D2_COUNT:
                return RuleFunctionCount.create();
            case RuleFunctionCountIfZeroPos.D2_COUNT_IF_ZERO_POS:
                return RuleFunctionCountIfZeroPos.create();
            case RuleFunctionCountIfValue.D2_COUNT_IF_VALUE:
                return RuleFunctionCountIfValue.create();
            case RuleFunctionLeft.D2_LEFT:
                return RuleFunctionLeft.create();
            case RuleFunctionLength.D2_LENGTH:
                return RuleFunctionLength.create();
            case RuleFunctionModulus.D2_MODULUS:
                return RuleFunctionModulus.create();
            case RuleFunctionOizp.D2_OIZP:
                return RuleFunctionOizp.create();
            case RuleFunctionRound.D2_ROUND:
                return RuleFunctionRound.create();
            case RuleFunctionRight.D2_RIGHT:
                return RuleFunctionRight.create();
            case RuleFunctionSubString.D2_SUBSTRING:
                return RuleFunctionSubString.create();
            case RuleFunctionSplit.D2_SPLIT:
                return RuleFunctionSplit.create();
            case RuleFunctionValidatePattern.D2_VALIDATE_PATTERN:
                return RuleFunctionValidatePattern.create();
            case RuleFunctionZing.D2_ZING:
                return RuleFunctionZing.create();
            case RuleFunctionZpvc.D2_ZPVC:
                return RuleFunctionZpvc.create();
            default:
                return null;
        }
    }
}
