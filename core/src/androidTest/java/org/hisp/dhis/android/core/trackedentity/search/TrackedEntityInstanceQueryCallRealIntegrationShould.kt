/*
 *  Copyright (c) 2004-2023, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.trackedentity.search

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toJavaDate
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory
import org.junit.Assert
import org.junit.Before

class TrackedEntityInstanceQueryCallRealIntegrationShould : BaseRealIntegrationTest() {
    private lateinit var repository: TrackedEntityInstanceQueryCollectionRepository

    @Before
    override fun setUp() {
        super.setUp()
        val orgUnits = listOf("DiszpKrYNg8")

        repository = d2.trackedEntityModule().trackedEntityInstanceQuery()
            .byOrgUnits().`in`(orgUnits)
            .byOrgUnitMode().eq(OrganisationUnitMode.ACCESSIBLE)
            .byProgram().eq("IpHINAT79UW")
    }

    // @Test
    fun query_tracked_entity_instances_no_filter() {
        login()
        val queryResponse = repository.onlineOnly().blockingGet()
        assertThat(queryResponse).isNotEmpty()
    }

    // @Test
    fun query_tracked_entity_instances_filter_name() {
        login()
        val queryResponse = repository.onlineOnly()
            .byQuery().eq("jorge").blockingGet()

        assertThat(queryResponse).isNotEmpty()
    }

    // @Test
    fun query_tracked_entity_instances_filter_program_start_date() {
        login()
        val lastYear = ClockProviderFactory.clockProvider.clock.now()
            .minus(1, DateTimeUnit.YEAR, TimeZone.currentSystemDefault())
        val queryResponse = repository.onlineOnly()
            .byProgramStartDate().eq(lastYear.toJavaDate()).blockingGet()

        assertThat(queryResponse).isNotEmpty()
    }

    // @Test
    fun query_tracked_entity_instances_filter_program_end_date() {
        login()
        val now = ClockProviderFactory.clockProvider.clock.now()
        val queryResponse = repository.onlineOnly()
            .byProgramEndDate().eq(now.toJavaDate())
            .blockingGet()

        assertThat(queryResponse).isNotEmpty()
    }

    // @Test
    fun query_tracked_entity_instances_one_attribute() {
        login()
        val queryResponse = repository.onlineOnly()
            .byAttribute("w75KJ2mc4zz").like("john")
            .blockingGet()

        assertThat(queryResponse).isNotEmpty()
    }

    // @Test
    fun query_tracked_entity_instances_two_attributes() {
        login()
        val queryResponse = repository.onlineOnly()
            .byAttribute("w75KJ2mc4zz").like("Filona")
            .byAttribute("zDhUuAYrxNC").like("Ryeder")
            .blockingGet()

        assertThat(queryResponse).isNotEmpty()
    }

    // @Test
    fun query_tracked_entity_instances_one_filter() {
        login()
        val queryResponse = repository.onlineOnly()
            .byFilter("w75KJ2mc4zz").like("jorge").blockingGet()

        assertThat(queryResponse).isNotEmpty()
    }

    // @Test
    fun query_tracked_entity_instances_two_filters() {
        login()
        val queryResponse = repository.onlineOnly()
            .byFilter("w75KJ2mc4zz").like("Filona")
            .byFilter("zDhUuAYrxNC").like("Ryeder")
            .blockingGet()

        assertThat(queryResponse).isNotEmpty()
    }

    // @Test
    fun query_tracked_entity_instances_filtering_by_data_value() {
        login()
        val queryResponse = repository.onlineOnly()
            .byProgramStage().eq("A03MvHHogjR")
            .byFilter("w75KJ2mc4zz").like("al")
            .byDataValue("a3kGcGDCuk6").eq("2")
            .blockingGet()

        assertThat(queryResponse).isNotEmpty()
    }

    // @Test
    fun throw_exception_for_too_long_list_of_org_units() {
        login()
        val orgUnits = listOf(
            "Rp268JB6Ne4", "cDw53Ej8rju", "GvFqTavdpGE",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
            "plnHVbJR6p4", "BV4IomHvri4", "qjboFI0irVu", "dWOAzMcK2Wt", "kbGqmM6ZWWV", "eoYV2p74eVz",
            "nq7F0t1Pz6t", "r5WWF9WDzoa", "yMCshbaVExv", "tlMeFk8C4CG", "YuQRtpLP10I", "Jiymtq0A01x",
            "jPidqyo7cpF", "XtuhRhmbrJM", "BH7rDkWjUqc", "c41XRVOYNJm", "Rll4VmTDRiE", "Eyj2kiEJ7M3",
            "HFyjUvMjQ8H", "MHAWZr2Caxw", "LOpWauwwghf", "mUuCjQWMaOc", "TNbHYOuQi8s", "aSfF9kuNINJ",
            "wYLjA4vN6Y9", "jjtzkzrmG7s", "FNnj3jKGS7i", "ABM75Q1UfoP", "rx9ubw0UCqj", "OZ1olxsTyNa",
            "MpcMjLmbATv", "qO2JLjYrg91", "U7yKrx2QVet", "uPshwz3B3Uu", "aF6iPGbrcRk", "lpAPY3QOY2D",
            "t1aAdpBbDB3", "xQIU41mR69s", "pdF4XIHIGPx", "rxc497GUdDt", "vWbkYPRmKyS", "FLjwMPWLrL2",
            "Yj2ni275yPJ", "a1dP5m3Clw4", "TQ5DSmdliN7", "t52CJEyLhch", "Y8foq27WLti", "x8SUTSsJoeO",
            "jNb63DIHuwU", "QIp6DHlMGfb", "weLTzWrLXCO", "eLLMnNjuluX", "dGheVylzol6", "zFDYIgyGmXG",
            "y5hLlID8ihI", "XkA2vbJAWHG", "vyIl6s0lhKc", "vELaJEPLOPF", "RzKeCma9qb1", "tlvNeDXXrS7",
            "sDTodaygv5u", "UGVLYrO63mR", "agM0BKQlTh3", "iMZihUMzH92", "cUNdCErxl9g", "k92yudERPlv",
            "PwgoRuWEDvJ", "qusWt6sESRU", "VpYAl8dXs6m", "EB1zRKdYjdY", "uFp0ztDOFbI", "o0BgK1dLhF8",
            "PMsF64R6OJX", "er9S4CQ9QOn", "n7wN9gMFfZ5", "Wr8kmywwseZ", "amgb83zVxp5", "DQHGtTGOP6b",
            "yDFM5J6WeKU", "iPcreOldeV9", "ZKL5hlVG6F6", "wQ71REGAMet", "OcRCVRy2M7X", "GHHvGp7tgtZ",
            "fwH9ipvXde9", "kUzpbgPCwVA", "xXhKbgwL39t", "WAjjFMDJKcx", "kBP1UvZpsNj", "lPeZdUm9fD7",
            "waNtxFbPjrI", "ENHOJz3UH5L", "O6uvpzGd5pu", "L5gENbBNNup", "rZxk3S0qN63", "D6yiaX1K5sO",
            "fdc6uOvgoji", "KKkLOTpMXGV", "PB8FMGbn19r", "YQYgz8exK9S", "VXrJKs8hic4", "H97XE5Ea089",
            "aVlSMMvgVzf", "zAyK28LLaez", "IcVHzEm0b6Z", "lc3eMKXaEfw", "VfZnZ6UKyn8", "uYG1rUdsJJi",
            "szbAJSWOXjT", "cZZG5BMDLps", "GRc9WXp9gSy", "kbPmt60yi0L", "vRC0stJ5y9Q", "tO01bqIipeD",
            "iUauWFeH8Qp", "AXZq6q7Dr6E", "LZclRdyVk1t", "OI0BQUurVFS", "DwpbWkiqjMy", "MwfWgjMRgId",
            "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY",
        )
        try {
            repository.byOrgUnits().`in`(orgUnits).blockingGet()
            Assert.fail("D2Error was expected but was not thrown")
        } catch (d2e: Exception) {
            // TODO
            // assertThat(d2e.errorCode() == D2ErrorCode.TOO_MANY_ORG_UNITS).isTrue();
        }
    }

    private fun login() {
        d2.userModule().logIn(username, password, url).blockingGet()
    }
}
