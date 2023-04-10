package org.hisp.dhis.periodgenerator.helper

import kotlinx.datetime.DayOfWeek
import kotlin.test.*

class CalendarHelperTest {

    @Test
    fun shouldReturnWhenAYearIsLeap() {
        assertTrue(CalendarHelper.yearIsLeap(2020))
        assertFalse(CalendarHelper.yearIsLeap(2021))
        assertFalse(CalendarHelper.yearIsLeap(2022))
        assertFalse(CalendarHelper.yearIsLeap(2023))
        assertTrue(CalendarHelper.yearIsLeap(2024))
        assertFalse(CalendarHelper.yearIsLeap(2025))
        assertFalse(CalendarHelper.yearIsLeap(2026))
        assertFalse(CalendarHelper.yearIsLeap(2027))
        assertTrue(CalendarHelper.yearIsLeap(2028))
    }

    @Test
    fun shouldCalculateHowManyWeeksInAYear() {
        assertEquals(CalendarHelper.weeksInAYear(2004), 53)
        assertEquals(CalendarHelper.weeksInAYear(2009), 53)
        assertEquals(CalendarHelper.weeksInAYear(2015), 53)
        assertEquals(CalendarHelper.weeksInAYear(2016), 52)
        assertEquals(CalendarHelper.weeksInAYear(2017), 52)
        assertEquals(CalendarHelper.weeksInAYear(2018), 52)
        assertEquals(CalendarHelper.weeksInAYear(2019), 52)
        assertEquals(CalendarHelper.weeksInAYear(2020), 53)
        assertEquals(CalendarHelper.weeksInAYear(2021), 52)
        assertEquals(CalendarHelper.weeksInAYear(2022), 52)
        assertEquals(CalendarHelper.weeksInAYear(2023), 52)
        assertEquals(CalendarHelper.weeksInAYear(2024), 52)
        assertEquals(CalendarHelper.weeksInAYear(2025), 52)
        assertEquals(CalendarHelper.weeksInAYear(2026), 53)
        assertEquals(CalendarHelper.weeksInAYear(2027), 52)
        assertEquals(CalendarHelper.weeksInAYear(2028), 52)
        assertEquals(CalendarHelper.weeksInAYear(2029), 52)
        assertEquals(CalendarHelper.weeksInAYear(2030), 52)
        assertEquals(CalendarHelper.weeksInAYear(2031), 52)
        assertEquals(CalendarHelper.weeksInAYear(2032), 53)
        assertEquals(CalendarHelper.weeksInAYear(2033), 52)
    }

    @Test
    fun shouldCalculateHowManyWeeksInAYearWithDifferentStartWeeks() {
        // TODO Improve and validate test
        assertEquals(CalendarHelper.weeksInAYear(2020, DayOfWeek.SUNDAY), 53)
    }
}