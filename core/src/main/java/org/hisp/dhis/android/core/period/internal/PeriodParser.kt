package org.hisp.dhis.android.core.period.internal

import kotlinx.datetime.*
import org.hisp.dhis.android.core.period.PeriodType
import org.hisp.dhis.android.core.period.PeriodType.Companion.firstDayOfTheWeek
import org.hisp.dhis.android.core.period.PeriodType.Companion.periodTypeFromPeriodId
import java.util.regex.Matcher
import java.util.regex.Pattern

internal class PeriodParser {
    @Throws(IllegalArgumentException::class)
    fun parse(periodId: String): Instant {
        val periodType = periodTypeFromPeriodId(periodId)
        return parse(periodId, periodType)
    }

    @Throws(IllegalArgumentException::class)
    fun parse(periodId: String, periodType: PeriodType): Instant {
        val matcher = getMatcherFromPeriodId(periodId, periodType)
        val date = getDateFromPeriodId(matcher, periodType)
        return date.atStartOfDayIn(TimeZone.currentSystemDefault())
    }

    private fun getMatcherFromPeriodId(periodId: String, periodType: PeriodType): Matcher {
        val pattern = Pattern.compile(periodType.pattern)
        val matcher = pattern.matcher(periodId)
        val match = matcher.find()

        require(match) { "It has not been possible to generate a match for the period pattern." }

        return matcher
    }

    private fun getDateFromPeriodId(matcher: Matcher, periodType: PeriodType): LocalDate {
        val year = matcher.group(1)?.toIntOrNull() ?: throw IllegalArgumentException("Invalid year in periodId")
        val match2 = matcher.group(2)?.toIntOrNull()
        val month: Int
        val semester: Int
        val week: Int

        return when (periodType) {
            PeriodType.Daily -> {
                month = match2 ?: throw IllegalArgumentException("Invalid month in periodId")
                val day = matcher.group(3)?.toIntOrNull() ?: throw IllegalArgumentException("Invalid day in periodId")
                LocalDate(year, month, day)
            }

            PeriodType.Weekly, PeriodType.WeeklyWednesday, PeriodType.WeeklyThursday,
            PeriodType.WeeklySaturday, PeriodType.WeeklySunday -> {
                week = match2 ?: throw IllegalArgumentException("Invalid week in periodId")
                getDateFromWeek(year, week, firstDayOfTheWeek(periodType))
            }

            PeriodType.BiWeekly -> {
                week = match2?.let { it * 2 - 1 } ?: throw IllegalArgumentException("Invalid bi-week in periodId")
                getDateFromWeek(year, week, firstDayOfTheWeek(periodType))
            }

            PeriodType.Monthly -> {
                month = match2 ?: throw IllegalArgumentException("Invalid month in periodId")
                getDateFromMonth(year, month)
            }

            PeriodType.BiMonthly -> {
                val biMonth = match2 ?: throw IllegalArgumentException("Invalid bi-month in periodId")
                getDateFromMonth(year, biMonth * 2 - 1)
            }

            PeriodType.Quarterly -> {
                val quarter = match2 ?: throw IllegalArgumentException("Invalid quarter in periodId")
                getDateFromMonth(year, quarter * 3 - 2)
            }

            PeriodType.QuarterlyNov -> {
                val quarter = match2 ?: throw IllegalArgumentException("Invalid quarter in periodId")
                val quarterMap = mapOf(1 to 11, 2 to 2, 3 to 5, 4 to 8)
                getDateFromMonth(
                    if (quarter == 1) year - 1 else year,
                    quarterMap[quarter] ?: throw IllegalArgumentException("Invalid quarter in periodId")
                )
            }

            PeriodType.SixMonthly -> {
                semester = match2 ?: throw IllegalArgumentException("Invalid semester in periodId")
                getDateFromMonth(year, semester * 6 - 5)
            }

            PeriodType.SixMonthlyApril -> {
                semester = match2 ?: throw IllegalArgumentException("Invalid semester in periodId")
                getDateFromMonth(year, semester * 6 - 2)
            }

            PeriodType.SixMonthlyNov -> {
                semester = match2 ?: throw IllegalArgumentException("Invalid semester in periodId")
                getDateFromMonth(
                    if (semester == 1) year - 1 else year,
                    if (semester == 1) 11 else if (semester == 2) 5 else throw IllegalArgumentException("Invalid semester in periodId")
                )
            }

            PeriodType.Yearly -> getDateFromMonth(year, 0)
            PeriodType.FinancialApril -> getDateFromMonth(year, 3)
            PeriodType.FinancialJuly -> getDateFromMonth(year, 6)
            PeriodType.FinancialOct -> getDateFromMonth(year, 9)
            PeriodType.FinancialNov -> getDateFromMonth(year - 1, 10)
        }
    }

    /**
     * returns a date based on a week number
     *
     * @param year           The year of the date
     * @param week           The week of the date
     * @param firstDayOfWeek The first day of the week
     * @return The LocalDate of the week
     */
    @Throws(IllegalArgumentException::class)
    private fun getDateFromWeek(year: Int, week: Int, firstDayOfWeek: Int): LocalDate {
        require(!(week < 1 || week > 53)) { "The week number is outside the year week range." }

        val firstDayOfYear = LocalDate(year, 1, 1)
        val firstWeekStart = firstDayOfYear.plus(
            ((DayOfWeek(firstDayOfWeek).ordinal - firstDayOfYear.dayOfWeek.ordinal + 7) % 7).toLong(), DateTimeUnit.DAY
        )

        val firstWeekDays = firstDayOfYear.daysUntil(firstWeekStart) + 1
        val isFirstWeekValid = firstWeekDays >= 4

        val actualFirstWeekStart = if (isFirstWeekValid) firstWeekStart else firstWeekStart.plus(7, DateTimeUnit.DAY)

        val weekStart = actualFirstWeekStart.plus((week - 1).toLong() * 7, DateTimeUnit.DAY)
        val date = LocalDate(weekStart.year, weekStart.monthNumber, weekStart.dayOfMonth)

        return date
    }

    /**
     * returns a date based on a month number
     *
     * @param year              The year of the date
     * @param month             The month of the date
     * @return The first Date of the month
     */
    @Throws(IllegalArgumentException::class)
    private fun getDateFromMonth(year: Int, month: Int): LocalDate {
        require(!(month < 1 || month > 12)) { "The periodId does not match a real date." }

        return LocalDate(year, month, 1)
    }
}
