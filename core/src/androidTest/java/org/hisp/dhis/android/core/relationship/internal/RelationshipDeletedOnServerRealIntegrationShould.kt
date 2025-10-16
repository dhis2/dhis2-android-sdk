/*
 *  Copyright (c) 2004-2025, University of Oslo
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
package org.hisp.dhis.android.core.relationship.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.d2.internal.DhisAndroidSdkKoinContext.koin
import org.hisp.dhis.android.core.arch.helpers.GeometryHelper
import org.hisp.dhis.android.core.arch.helpers.UidGenerator
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection
import org.junit.Before
import kotlin.time.Duration.Companion.seconds

/**
 * Real integration test for debugging relationship deletion handling.
 *
 * This test helps verify that the SDK correctly handles relationships that have been
 * deleted on the server but are still marked as TO_UPDATE locally.
 *
 * DEBUGGING STEPS:
 * 1. Run test until first breakpoint (after first sync)
 * 2. Copy the relationship UID from the log/debugger
 * 3. Manually delete the relationship in the DHIS2 server (via API or web UI)
 * 4. Resume test execution
 * 5. Verify that after second sync, the relationship is deleted locally
 */
class RelationshipDeletedOnServerRealIntegrationShould : BaseRealIntegrationTest() {

    private lateinit var relationshipStore: RelationshipStore
    private lateinit var uidGenerator: UidGenerator

    private lateinit var teiUid1: String
    private lateinit var teiUid2: String
    private lateinit var relationshipUid: String
    private lateinit var trackedEntityType: String

    private var programUid: String? = null
    private var orgUnitUid: String? = null
    private var relationshipTypeUid: String? = null

    @Before
    override fun setUp() {
        super.setUp()
        relationshipStore = koin.get()
        uidGenerator = UidGeneratorImpl()

        teiUid1 = uidGenerator.generate()
        teiUid2 = uidGenerator.generate()
        relationshipUid = uidGenerator.generate()
    }

    /**
     * Test scenario:
     * 1. Download metadata
     * 2. Create two TEIs locally
     * 3. Create a relationship between them locally
     * 4. Upload TEIs and relationships to server (should succeed)
     * 5. Mark relationship as TO_UPDATE to simulate a local modification
     * 6. PAUSE HERE - Manually delete the relationship on the server
     * 7. Upload again
     * 8. Verify the relationship is deleted locally (not in ERROR state)
     */
    // @Test
    @Throws(Exception::class)
    fun delete_local_relationship_when_deleted_on_server() = runTest(timeout = 300000.seconds) {
        println("========================================")
        println("STEP 1: Downloading metadata...")
        println("========================================")
        d2.userModule().logIn(username, password, url).blockingGet()
        d2.metadataModule().blockingDownload()

        programUid = "IpHINAT79UW"
        orgUnitUid = d2.organisationUnitModule().organisationUnits().blockingGet().firstOrNull()?.uid()
        relationshipTypeUid = "XdP5nraLPZ0"
        trackedEntityType = "nEenWmSyUEp"

        assertThat(orgUnitUid).isNotNull()

        println("Program UID: $programUid")
        println("OrgUnit UID: $orgUnitUid")
        println("RelationshipType UID: $relationshipTypeUid")

        println("\n========================================")
        println("STEP 2: Creating two TEIs...")
        println("========================================")

        val teiUid1Created = d2.trackedEntityModule().trackedEntityInstances().add(
            TrackedEntityInstanceCreateProjection.builder()
                .organisationUnit(orgUnitUid)
                .trackedEntityType(trackedEntityType)
                .build(),
        ).blockingGet()

        val teiUid2Created = d2.trackedEntityModule().trackedEntityInstances().add(
            TrackedEntityInstanceCreateProjection.builder()
                .organisationUnit(orgUnitUid)
                .trackedEntityType(trackedEntityType)
                .build(),
        ).blockingGet()

        println("TEI 1 UID: $teiUid1Created")
        println("TEI 2 UID: $teiUid2Created")

        println("\n========================================")
        println("STEP 3: Creating relationship...")
        println("========================================")

        val relationship = RelationshipHelper.teiToTeiRelationship(
            teiUid1Created,
            teiUid2Created,
            relationshipTypeUid,
        )

        val createdRelationshipUid = d2.relationshipModule().relationships()
            .add(relationship)
            .blockingGet()

        println("Relationship UID: $createdRelationshipUid")

        val relationshipBeforeSync = d2.relationshipModule().relationships()
            .uid(createdRelationshipUid)
            .blockingGet()

        assertThat(relationshipBeforeSync).isNotNull()
        assertThat(relationshipBeforeSync?.syncState()).isEqualTo(State.TO_POST)

        val tei1AfterRelationship = d2.trackedEntityModule().trackedEntityInstances()
            .uid(teiUid1Created)
            .blockingGet()

        println("TEI1 syncState: ${tei1AfterRelationship?.syncState()}")
        println("TEI1 aggregatedSyncState: ${tei1AfterRelationship?.aggregatedSyncState()}")

        assertThat(tei1AfterRelationship?.aggregatedSyncState()).isEqualTo(State.TO_UPDATE)

        println("\n========================================")
        println("STEP 4: Uploading TEIs and relationships to server...")
        println("========================================")

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        val tei1AfterUpload = d2.trackedEntityModule().trackedEntityInstances()
            .uid(teiUid1Created)
            .blockingGet()
        val tei2AfterUpload = d2.trackedEntityModule().trackedEntityInstances()
            .uid(teiUid2Created)
            .blockingGet()
        val relationshipAfterUpload = d2.relationshipModule().relationships()
            .uid(createdRelationshipUid)
            .blockingGet()

        println(
            "TEI1 after upload - syncState: ${tei1AfterUpload?.syncState()}, " +
                "aggregatedSyncState: ${tei1AfterUpload?.aggregatedSyncState()}",
        )
        println(
            "TEI2 after upload - syncState: ${tei2AfterUpload?.syncState()}, " +
                "aggregatedSyncState: ${tei2AfterUpload?.aggregatedSyncState()}",
        )
        println("Relationship after upload - syncState: ${relationshipAfterUpload?.syncState()}")

        assertThat(tei1AfterUpload?.syncState()).isEqualTo(State.SYNCED)
        assertThat(tei2AfterUpload?.syncState()).isEqualTo(State.SYNCED)
        assertThat(relationshipAfterUpload?.syncState()).isEqualTo(State.SYNCED)
        println("TEIs and relationship uploaded successfully")

        println("\n========================================")
        println("STEP 5: Simulating local modification...")
        println("========================================")

        d2.trackedEntityModule().trackedEntityInstances()
            .uid(teiUid1Created)
            .setGeometry(GeometryHelper.createPointGeometry(-180.0, 90.0))

        relationshipStore.setSyncState(createdRelationshipUid!!, State.TO_UPDATE)

        val tei1AfterUpdate = d2.trackedEntityModule().trackedEntityInstances()
            .uid(teiUid1Created)
            .blockingGet()

        val relationshipWithToUpdate = d2.relationshipModule().relationships()
            .uid(createdRelationshipUid)
            .blockingGet()

        println("TEI1 after update - syncState: ${tei1AfterUpdate?.syncState()}")
        println("Relationship after TEI update - syncState: ${relationshipWithToUpdate?.syncState()}")

        assertThat(tei1AfterUpdate?.syncState()).isEqualTo(State.TO_UPDATE)
        println("TEI marked as TO_UPDATE")

        println("\n========================================")
        println("STEP 6: MANUAL ACTION REQUIRED")
        println("========================================")
        println(">>> SET BREAKPOINT HERE <<<")
        println("Relationship UID to delete: $createdRelationshipUid")
        println("TEI with the relationship: $teiUid1Created")
        println("")
        println("INSTRUCTIONS:")
        println("1. Copy the TEI UID above")
        println("2. Delete it on the DHIS2 server:")
        println("   - Capture app: Enroll the TEI in the $programUid program and delete the relationship from UI")
        println("3. Resume the test execution")
        println("========================================")

        // This line is where you should set your breakpoint
        val pauseHere = "Delete relationship on server now"

        println("\n========================================")
        println("STEP 7: Second sync - expecting deletion...")
        println("========================================")

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()

        println("\n========================================")
        println("STEP 8: Verifying deletion...")
        println("========================================")

        val relationshipAfterSecondSync = d2.relationshipModule().relationships()
            .uid(createdRelationshipUid)
            .blockingGet()

        println("Relationship after second sync: $relationshipAfterSecondSync")

        assertThat(relationshipAfterSecondSync).isNull()

        println("\n========================================")
        println("SUCCESS! Relationship was deleted locally")
        println("========================================")
    }
}
