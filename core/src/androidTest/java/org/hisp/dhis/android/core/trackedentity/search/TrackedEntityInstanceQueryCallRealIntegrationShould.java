/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity.search;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.api.OuMode;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.utils.integration.real.BaseRealIntegrationTest;
import org.junit.Before;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.Assert.fail;

public class TrackedEntityInstanceQueryCallRealIntegrationShould extends BaseRealIntegrationTest {
    private D2 d2;
    private TrackedEntityInstanceQuery.Builder queryBuilder;

    @Before
    @Override
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(RealServerMother.url, databaseAdapter());


        List<String> orgUnits = new ArrayList<>();
        orgUnits.add("DiszpKrYNg8");

        queryBuilder = TrackedEntityInstanceQuery.builder()
                .paging(true).page(1).pageSize(50)
                .orgUnits(orgUnits).orgUnitMode(OuMode.ACCESSIBLE).program("IpHINAT79UW");
    }

    //@Test
    public void query_tracked_entity_instances_no_filter() throws Exception {
        login();
        List<TrackedEntityInstance> queryResponse =
                d2.trackedEntityModule().trackedEntityInstanceQuery.onlineOnly().get();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_filter_name() throws Exception {
        login();

        TrackedEntityInstanceQuery query = queryBuilder.query(QueryFilter.create("jorge")).build();
        List<TrackedEntityInstance> queryResponse =
                d2.trackedEntityModule().trackedEntityInstanceQuery.onlineOnly().query(query).get();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_filter_program_start_date() throws Exception {
        login();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1);
        TrackedEntityInstanceQuery query = queryBuilder.programStartDate(cal.getTime()).build();
        List<TrackedEntityInstance> queryResponse =
                d2.trackedEntityModule().trackedEntityInstanceQuery.onlineOnly().query(query).get();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_filter_program_end_date() throws Exception {
        login();

        Calendar cal = Calendar.getInstance();
        TrackedEntityInstanceQuery query = queryBuilder.programEndDate(cal.getTime()).build();
        List<TrackedEntityInstance> queryResponse =
                d2.trackedEntityModule().trackedEntityInstanceQuery.onlineOnly().query(query).get();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_one_attribute() throws Exception {
        login();

        List<QueryItem> attributeList = new ArrayList<>(1);
        attributeList.add(QueryItem.create("w75KJ2mc4zz", QueryFilter.create(QueryOperator.LIKE, "john")));

        TrackedEntityInstanceQuery query = queryBuilder.attribute(attributeList).build();
        List<TrackedEntityInstance> queryResponse =
                d2.trackedEntityModule().trackedEntityInstanceQuery.onlineOnly().query(query).get();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_two_attributes() throws Exception {
        login();

        List<QueryItem> attributeList = new ArrayList<>(2);

        attributeList.add(QueryItem.create("w75KJ2mc4zz", QueryFilter.create(QueryOperator.LIKE, "Filona")));
        attributeList.add(QueryItem.create("zDhUuAYrxNC", QueryFilter.create(QueryOperator.LIKE, "Ryder")));

        TrackedEntityInstanceQuery query = queryBuilder.attribute(attributeList).build();
        List<TrackedEntityInstance> queryResponse =
                d2.trackedEntityModule().trackedEntityInstanceQuery.onlineOnly().query(query).get();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void use_attribute_to_reduce_attributes_returned() throws Exception {
        login();

        List<QueryItem> attributeList = new ArrayList<>(1);
        attributeList.add(QueryItem.create("w75KJ2mc4zz"));

        TrackedEntityInstanceQuery query = queryBuilder.attribute(attributeList).build();
        List<TrackedEntityInstance> queryResponse =
                d2.trackedEntityModule().trackedEntityInstanceQuery.onlineOnly().query(query).get();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_one_filter() throws Exception {
        login();

        List<QueryItem> filterList = new ArrayList<>(1);
        filterList.add(QueryItem.create("w75KJ2mc4zz", QueryFilter.create(QueryOperator.LIKE, "jorge")));

        TrackedEntityInstanceQuery query = queryBuilder.filter(filterList).build();
        List<TrackedEntityInstance> queryResponse =
                d2.trackedEntityModule().trackedEntityInstanceQuery.onlineOnly().query(query).get();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void query_tracked_entity_instances_two_filters() throws Exception {
        login();

        List<QueryItem> filterList = new ArrayList<>(2);
        filterList.add(QueryItem.create("w75KJ2mc4zz", QueryFilter.create(QueryOperator.LIKE, "Filona")));
        filterList.add(QueryItem.create("zDhUuAYrxNC", QueryFilter.create(QueryOperator.LIKE, "Ryder")));

        TrackedEntityInstanceQuery query = queryBuilder.filter(filterList).build();
        List<TrackedEntityInstance> queryResponse =
                d2.trackedEntityModule().trackedEntityInstanceQuery.onlineOnly().query(query).get();
        assertThat(queryResponse).isNotEmpty();
    }

    //@Test
    public void throw_exception_for_too_long_list_of_org_units() throws Exception {
        login();

        List<String> orgUnits = Lists.newArrayList("Rp268JB6Ne4", "cDw53Ej8rju", "GvFqTavdpGE",
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
                "Q2USZSJmcNK", "EJoI3HArJ2W", "E497Rk80ivZ", "nOYt1LtFSyU", "wbtk73Zwhj9", "cMFi8lYbXHY");

        queryBuilder.orgUnits(orgUnits);

        try {
            d2.trackedEntityModule().trackedEntityInstanceQuery.onlineOnly().query(queryBuilder.build()).get();
            fail("D2Error was expected but was not thrown");
        } catch (Exception d2e) {
            // TODO
            //assertThat(d2e.errorCode() == D2ErrorCode.TOO_MANY_ORG_UNITS).isTrue();
        }
    }

    private void login() throws Exception {
        d2.userModule().logIn("android", "Android123").call();
    }
}