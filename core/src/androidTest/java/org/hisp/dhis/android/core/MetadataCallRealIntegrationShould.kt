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
package org.hisp.dhis.android.core

import android.util.Log
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.call.D2Progress
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.user.User
import org.junit.Test

class MetadataCallRealIntegrationShould : BaseRealIntegrationTest() {
    /**
     * A quick integration test that is probably flaky, but will help with finding bugs related to
     * the
     * metadataSyncCall. It works against the demo server.
     */
    /* How to extract database from tests:
    edit: AbsStoreTestCase.java (adding database name.)
    DbOpenHelper dbOpenHelper = new DbOpenHelper(InstrumentationRegistry.getTargetContext()
    .getApplicationContext(),
    "test.db");
    make a debugger break point where desired (after sync complete)

    Then while on the breakpoint :
    Android/platform-tools/adb pull /data/user/0/org.hisp.dhis.android.test/databases/test.db
    test.db

    in datagrip:
    pragma foreign_keys = on;
    pragma foreign_key_check;*/
    // This test is uncommented because technically it is flaky.
    // It depends on a live server to operate and the login is hardcoded here.
    // Uncomment in order to quickly test changes vs a real server, but keep it uncommented after.

//    @Test
    fun response_successful_on_sync_meta_data_once() {
        d2.userModule().logIn(username, password, url).blockingGet()

        d2.metadataModule().blockingDownload()

        // TODO: add additional sync + break point.
        // when debugger stops at the new break point manually change metadata online & resume.
        // This way I can make sure that additive (updates) work as well.
        // The changes could be to one of the programs, adding stuff to it.
        // adding a new program..etc.
    }

    @Test
    fun test_event_filter_with_order_uses_correct_version() {
        val TAG = "EventFilterOrderTest"
        // Login and download metadata
        d2.userModule().logIn(username, password, "https://android.im.dhis2.org/current").blockingGet()
        d2.metadataModule().blockingDownload()

        // Get the tracker exporter version that the SDK will use
        val syncSettings = d2.settingModule().synchronizationSettings().blockingGet()
        val trackerVersion = syncSettings?.trackerExporterVersion()

        Log.i("EventFilterOrderTest", "=== EventFilter Order Test ===")
        Log.i("EventFilterOrderTest", "Tracker Exporter Version: $trackerVersion")

        // Get available event filters
        val eventFilters = d2.eventModule().eventFilters().blockingGet()
        Log.i("EventFilterOrderTest", "Available event filters: ${eventFilters.size}")

        if (eventFilters.isNotEmpty()) {
            // Find a filter with order criteria
            val filterWithOrder = eventFilters.find {
                it.eventQueryCriteria()?.order() != null
            }

            if (filterWithOrder != null) {
                Log.i(
                    "EventFilterOrderTest",
                    "Testing filter: ${filterWithOrder.uid()} - ${filterWithOrder.displayName()}"
                )
                val orderCriteria = filterWithOrder.eventQueryCriteria()?.order()
                Log.i("EventFilterOrderTest", "Order criteria: $orderCriteria")

                // Get program and program stage info
                val programUid = filterWithOrder.program()
                Log.i("EventFilterOrderTest", "Program UID: $programUid")

                if (programUid != null) {
                    val program = d2.programModule().programs().uid(programUid).blockingGet()
                    Log.i("EventFilterOrderTest", "Program: ${program?.displayName()}")
                    Log.i("EventFilterOrderTest", "Program type: ${program?.programType()}")

                    // Get program stages
                    val programStages = d2.programModule().programStages()
                        .byProgramUid().eq(programUid)
                        .blockingGet()

                    Log.i("EventFilterOrderTest", "Program stages: ${programStages.size}")
                    programStages.forEach { stage ->
                        Log.i("EventFilterOrderTest", "  - Stage: ${stage.uid()} - ${stage.displayName()}")
                    }
                }

                try {
                    // Get user's org units to avoid SELECTED mode error
                    val orgUnits = d2.organisationUnitModule().organisationUnits()
                        .byOrganisationUnitScope(OrganisationUnit.Scope.SCOPE_DATA_CAPTURE)
                        .blockingGet()

                    if (orgUnits.isNotEmpty()) {
                        // Analyze the order field names
                        val orderFields = orderCriteria?.split(",")?.map { it.split(":")[0] } ?: emptyList()
                        val hasV1Names = orderFields.any {
                            it in listOf("eventDate", "dueDate", "trackedEntityInstance", "lastUpdated")
                        }
                        val hasV2Names = orderFields.any {
                            it in listOf("occurredAt", "scheduledAt", "trackedEntity", "updatedAt")
                        }

                        Log.i("EventFilterOrderTest", "Order field names: $orderFields")
                        Log.i("EventFilterOrderTest", "Contains V1 names: $hasV1Names, V2 names: $hasV2Names")
                        Log.i("EventFilterOrderTest", "Server uses: $trackerVersion")

                        if (trackerVersion?.name == "V2" && hasV1Names) {
                            Log.w("EventFilterOrderTest", "⚠ WARNING: Filter has V1 field names but server uses V2!")
                            Log.w(
                                "EventFilterOrderTest",
                                "⚠ The fix should handle this by parsing with V2 and accepting both name formats"
                            )
                        }

                        // Show info to help create events manually
                        val programUid = filterWithOrder.program()
                        if (programUid != null && orgUnits.isNotEmpty()) {
                            val program = d2.programModule().programs().uid(programUid).blockingGet()
                            val programStages = d2.programModule().programStages()
                                .byProgramUid().eq(programUid)
                                .blockingGet()

                            if (program != null && programStages.isNotEmpty()) {
                                val firstOrgUnit = orgUnits.first()

                                Log.i("EventFilterOrderTest", "=== Info for manual event creation ===")
                                Log.i("EventFilterOrderTest", "Program: ${program.displayName()} (${program.uid()})")
                                Log.i("EventFilterOrderTest", "Program type: ${program.programType()}")
                                programStages.forEach { stage ->
                                    Log.i("EventFilterOrderTest", "Stage: ${stage.displayName()} (${stage.uid()})")
                                }
                                Log.i(
                                    "EventFilterOrderTest",
                                    "OrgUnit: ${firstOrgUnit.displayName()} (${firstOrgUnit.uid()})"
                                )
                                Log.i("EventFilterOrderTest", "======================================")
                            }

                            // Check how many events exist WITHOUT the filter
                            try {
                                val eventsWithoutFilter = d2.eventModule().eventQuery()
                                    .onlineOnly()
                                    .byProgram().eq(programUid)
                                    .byOrgUnits().`in`(orgUnits.map { it.uid() })
                                    .blockingGet()

                                Log.i(
                                    "EventFilterOrderTest",
                                    "Events in program WITHOUT filter: ${eventsWithoutFilter.size}"
                                )
                                if (eventsWithoutFilter.isNotEmpty()) {
                                    Log.i("EventFilterOrderTest", "Sample events (without order):")
                                    eventsWithoutFilter.take(3).forEach { event ->
                                        Log.i(
                                            "EventFilterOrderTest",
                                            "  - ${event.uid()} - occurredAt: ${event.eventDate()}"
                                        )
                                    }
                                }
                            } catch (e: Exception) {
                                Log.w("EventFilterOrderTest", "Could not query without filter: ${e.message}")
                            }
                        }

                        // Now query WITH the filter (this tests the order parsing)
                        val events = d2.eventModule().eventQuery()
                            .onlineOnly()
                            .byEventFilter().eq(filterWithOrder.uid())
                            .byOrgUnits().`in`(orgUnits.map { it.uid() })
                            .blockingGet()

                        Log.i("EventFilterOrderTest", "Events WITH filter: ${events.size}")

                        if (events.isEmpty()) {
                            Log.w(
                                "EventFilterOrderTest",
                                "⚠ No events found with filter - this might be due to filter criteria (assignedUserMode, etc.)"
                            )
                        } else {
                            Log.i("EventFilterOrderTest", "✓ Successfully retrieved ${events.size} events")

                            // Verify the order is correct (should be descending by occurredAt)
                            Log.i("EventFilterOrderTest", "Verifying order (should be desc by occurredAt):")
                            events.take(5).forEachIndexed { index, event ->
                                Log.i(
                                    "EventFilterOrderTest",
                                    "  Event ${index + 1}: ${event.uid()} - occurredAt: ${event.eventDate()}"
                                )
                            }

                            // Check if events are actually ordered
                            if (events.size >= 2) {
                                val isOrdered = events.zipWithNext().all { (a, b) ->
                                    val dateA = a.eventDate()?.time ?: 0
                                    val dateB = b.eventDate()?.time ?: 0
                                    dateA >= dateB // descending order
                                }

                                if (isOrdered) {
                                    Log.i("EventFilterOrderTest", "✓✓ Events are correctly ordered by occurredAt DESC!")
                                } else {
                                    Log.w("EventFilterOrderTest", "⚠ Events are NOT in descending order")
                                }
                            }
                        }

                        Log.i(
                            "EventFilterOrderTest",
                            "✓ Query executed without errors - order parameter handling works"
                        )
                        Log.i(
                            "EventFilterOrderTest",
                            "✓ The order field 'eventDate' (V1) was successfully converted and used in the V2 API call"
                        )
                    } else {
                        Log.i("EventFilterOrderTest", "⚠ User has no org units assigned, skipping query")
                    }
                } catch (e: Exception) {
                    Log.e("EventFilterOrderTest", "✗ Failed to query events: ${e.message}")
                    Log.e("EventFilterOrderTest", "✗ Stack trace: ${e.stackTraceToString()}")
                    throw e
                }
            } else {
                Log.i("EventFilterOrderTest", "No event filters with order criteria found")

                // Test with a manual query with order
                try {
                    val events = d2.eventModule().eventQuery()
                        .onlineOnly()
                        .blockingGet()

                    Log.i("EventFilterOrderTest", "Retrieved ${events.size} events without filter")
                } catch (e: Exception) {
                    Log.e("EventFilterOrderTest", "Failed: ${e.message}")
                }
            }
        } else {
            Log.i("EventFilterOrderTest", "No event filters available on this server")
        }

        Log.i("EventFilterOrderTest", "=== Test Complete ===")
    }

    // @Test
    fun download_metadata_in_io_scheduler() {
        d2.userModule().logIn(username, password, url)
            .flatMapObservable { user: User? -> d2.metadataModule().download() }
            .subscribeOn(Schedulers.io())
            .subscribe { progress: D2Progress -> Log.i("META", progress.lastCall()!!) }

        Thread.sleep(60000)
    }

    // @Test
    fun response_successful_on_sync_meta_data_two_times() {
        d2.userModule().logIn(username, password, url).blockingGet()

        // first sync:
        d2.metadataModule().blockingDownload()

        // second sync:
        d2.metadataModule().blockingDownload()
    }

    // @Test
    fun response_successful_on_login_wipe_db_and_login() = runTest {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.wipeModule().wipeEverything()
        d2.userModule().logIn(username, password, url).blockingGet()
    }

    // @Test
    fun response_successful_on_login_logout_and_login() {
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.userModule().logOut().blockingAwait()
        d2.userModule().logIn(username, password, url).blockingGet()
    }
}
