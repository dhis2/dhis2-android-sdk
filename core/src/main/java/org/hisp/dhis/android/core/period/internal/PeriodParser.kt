package org.hisp.dhis.android.core.period.internal

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import kotlinx.datetime.plus
import org.hisp.dhis.android.core.arch.helpers.DateUtils.atStartOfDayInSystem
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.PeriodType.Companion.firstDayOfTheWeek
import org.hisp.dhis.android.core.period.PeriodType.Companion.periodTypeFromPeriodId
import org.hisp.dhis.android.core.period.generator.internal.WeeklyPeriodGeneratorHelper
import org.koin.core.annotation.Singleton
import kotlin.time.Instant

@Singleton
internal class PeriodParser {
    @Throws(IllegalArgumentException::class)
    fun parse(periodId: String): Instant {
        val periodType = periodTypeFromPeriodId(periodId)
        return parse(periodId, periodType)
    }

    @Throws(IllegalArgumentException::class)
    fun parse(periodId: String, periodType: PeriodType): Instant {
        val regex = Regex(periodType.pattern)
        val matchResult = regex.matchEntire(periodId)
        requireNotNull(matchResult) { "It has not been possible to generate a match for the period pattern." }
        val date = getDateFromPeriodId(matchResult, periodType)
        return date.atStartOfDayInSystem()
    }

    private fun getDateFromPeriodId(matchResult: MatchResult, periodType: PeriodType): LocalDate {
        val year = matchResult.groupValues[1].toIntOrNull()
            ?: throw IllegalArgumentException("Invalid year in periodId")

        return when (periodType) {
            PeriodType.Daily -> getDateForDaily(matchResult, year)
            PeriodType.Weekly, PeriodType.WeeklyWednesday, PeriodType.WeeklyThursday,
            PeriodType.WeeklyFriday, PeriodType.WeeklySaturday, PeriodType.WeeklySunday,
            PeriodType.BiWeekly,
                -> getDateForWeeklyOrBiWeekly(matchResult, year, periodType)

            PeriodType.Monthly, PeriodType.BiMonthly,
            PeriodType.Quarterly, PeriodType.QuarterlyNov,
            PeriodType.SixMonthly, PeriodType.SixMonthlyApril, PeriodType.SixMonthlyNov,
                -> getDateFromMonthPattern(matchResult, year, periodType)

            PeriodType.Yearly, PeriodType.FinancialFeb, PeriodType.FinancialApril,
            PeriodType.FinancialJuly, PeriodType.FinancialAug, PeriodType.FinancialSep,
            PeriodType.FinancialOct, PeriodType.FinancialNov -> getDateFromYearPattern(year, periodType)
        }
    }

    private fun getDateForDaily(matchResult: MatchResult, year: Int): LocalDate {
        val month = matchResult.groupValues[2].toIntOrNull()
            ?: throw IllegalArgumentException("Invalid month in periodId")
        val day = matchResult.groupValues[DAY_MATCHER_INDEX].toIntOrNull()
            ?: throw IllegalArgumentException("Invalid day in periodId")
        return LocalDate(year, month, day)
    }

    private fun getDateForWeeklyOrBiWeekly(matchResult: MatchResult, year: Int, periodType: PeriodType): LocalDate {
        val week = matchResult.groupValues[2].toIntOrNull()
            ?: throw IllegalArgumentException("Invalid week in periodId")
        val adjustedWeek = if (periodType == PeriodType.BiWeekly) week * 2 - 1 else week
        return getDateFromWeek(year, adjustedWeek, firstDayOfTheWeek(periodType))
    }

    private fun getDateFromWeek(year: Int, week: Int, firstDayOfWeek: DayOfWeek): LocalDate {
        require(week in 1..MAX_WEEK_NUMBER) { "The week number is outside the year week range." }

        val weekHelper = WeeklyPeriodGeneratorHelper(firstDayOfWeek)
        val firstDayOfWeekOfYear = weekHelper.getFirstDayOfWeekOfYear(year)

        val weekStart = firstDayOfWeekOfYear.plus((week - 1).toLong(), DateTimeUnit.WEEK)
        return LocalDate(weekStart.year, weekStart.month.number, weekStart.day)
    }

    private fun getDateFromMonthPattern(matchResult: MatchResult, year: Int, periodType: PeriodType): LocalDate {
        val monthQuarterOrSemester = matchResult.groupValues[2].toIntOrNull()!!
        return when (periodType) {
            PeriodType.Monthly -> getDateFromMonth(year, monthQuarterOrSemester)
            PeriodType.BiMonthly -> getDateFromMonth(year, monthQuarterOrSemester * 2 - 1)
            PeriodType.Quarterly -> getDateFromMonth(year, monthQuarterOrSemester * QUARTER_MONTHS - 2)
            PeriodType.QuarterlyNov -> getDateForQuarterlyNov(year, monthQuarterOrSemester)
            PeriodType.SixMonthly -> getDateFromMonth(
                year,
                monthQuarterOrSemester * SEMESTER_MONTHS - (SEMESTER_MONTHS - Month.JANUARY.number),
            )

            PeriodType.SixMonthlyApril -> getDateFromMonth(
                year,
                monthQuarterOrSemester * SEMESTER_MONTHS - (SEMESTER_MONTHS - Month.APRIL.number),
            )

            PeriodType.SixMonthlyNov -> getDateFromMonth(
                if (monthQuarterOrSemester == 1) year - 1 else year,
                if (monthQuarterOrSemester == 1) Month.NOVEMBER.number else Month.MAY.number,
            )

            else -> throw IllegalArgumentException("Invalid period type")
        }
    }

    private fun getDateForQuarterlyNov(year: Int, quarter: Int): LocalDate {
        val quarterMap = mapOf(
            Q1 to Month.NOVEMBER.number,
            Q2 to Month.FEBRUARY.number,
            Q3 to Month.MAY.number,
            Q4 to Month.AUGUST.number,
        )
        return getDateFromMonth(
            if (quarter == 1) year - 1 else year,
            quarterMap[quarter] ?: throw IllegalArgumentException("Invalid quarter in periodId"),
        )
    }

    private fun getDateFromYearPattern(year: Int, periodType: PeriodType): LocalDate {
        return when (periodType) {
            PeriodType.Yearly -> getDateFromMonth(year, Month.JANUARY.number)
            PeriodType.FinancialFeb -> getDateFromMonth(year, Month.FEBRUARY.number)
            PeriodType.FinancialApril -> getDateFromMonth(year, Month.APRIL.number)
            PeriodType.FinancialJuly -> getDateFromMonth(year, Month.JULY.number)
            PeriodType.FinancialAug -> getDateFromMonth(year, Month.AUGUST.number)
            PeriodType.FinancialSep -> getDateFromMonth(year, Month.SEPTEMBER.number)
            PeriodType.FinancialOct -> getDateFromMonth(year, Month.OCTOBER.number)
            PeriodType.FinancialNov -> getDateFromMonth(year - 1, Month.NOVEMBER.number)
            else -> throw IllegalArgumentException("Invalid period type")
        }
    }

    private fun getDateFromMonth(year: Int, month: Int): LocalDate {
        require(month in Month.JANUARY.number..Month.DECEMBER.number) {
            "The periodId does not match a real date."
        }
        return LocalDate(year, month, 1)
    }

    companion object {
        private const val MAX_WEEK_NUMBER = 53
        private const val SEMESTER_MONTHS = 6
        private const val QUARTER_MONTHS = 3
        private const val DAY_MATCHER_INDEX = 3
        private const val Q1 = 1
        private const val Q2 = 2
        private const val Q3 = 3
        private const val Q4 = 4
    }
}
