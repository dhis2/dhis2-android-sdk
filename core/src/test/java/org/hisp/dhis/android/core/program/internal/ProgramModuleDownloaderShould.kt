/*
 *  Copyright (c) 2004-2022, University of Oslo
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
package org.hisp.dhis.android.core.program.internal

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import javax.net.ssl.HttpsURLConnection
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.event.internal.EventFilterCall
import org.hisp.dhis.android.core.option.internal.OptionCall
import org.hisp.dhis.android.core.option.internal.OptionGroupCall
import org.hisp.dhis.android.core.option.internal.OptionSetCall
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitProgramLinkStore
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.programstageworkinglist.internal.ProgramStageWorkingListCall
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeCall
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityAttributeCall
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFilterCall
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityTypeCall
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import retrofit2.Response

@RunWith(JUnit4::class)
class ProgramModuleDownloaderShould : BaseCallShould() {
    private val program: Program = mock()
    private val trackedEntityType: TrackedEntityType = mock()
    private val trackedEntityAttribute: TrackedEntityAttribute = mock()
    private val programCall: ProgramCall = mock()
    private val programStageCall: ProgramStageCall = mock()
    private val programRuleCall: ProgramRuleCall = mock()
    private val trackedEntityTypeCall: TrackedEntityTypeCall = mock()
    private val trackedEntityAttributeCall: TrackedEntityAttributeCall = mock()
    private val trackedEntityInstanceFilterCall: TrackedEntityInstanceFilterCall = mock()
    private val eventFilterCall: EventFilterCall = mock()
    private val programStageWorkingListCall: ProgramStageWorkingListCall = mock()
    private val relationshipTypeCall: RelationshipTypeCall = mock()
    private val optionSetCall: OptionSetCall = mock()
    private val optionCall: OptionCall = mock()
    private val optionGroupCall: OptionGroupCall = mock()
    private val organisationUnitProgramLinkLinkStore: OrganisationUnitProgramLinkStore = mock()

    // object to test
    private lateinit var programModuleDownloader: ProgramModuleDownloader

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        errorResponse = Response.error<Any>(
            HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
            ResponseBody.create("application/json".toMediaTypeOrNull(), "{}")
        )

        // Calls
        returnSingletonList(programCall, program)
        returnSingletonList(trackedEntityTypeCall, trackedEntityType)
        returnSingletonList(trackedEntityAttributeCall, trackedEntityAttribute)
        returnSingletonList(programCall, program)
        whenever(relationshipTypeCall.download()).doReturn(Single.just(emptyList()))
        returnEmptyList(optionSetCall)
        returnEmptyList(optionCall)
        returnEmptyList(optionGroupCall)
        returnEmptyList(trackedEntityInstanceFilterCall)
        returnEmptyList(eventFilterCall)
        returnEmptyList(programStageWorkingListCall)
        returnEmptyList(programRuleCall)
        returnEmptyList(programStageCall)
        programModuleDownloader = ProgramModuleDownloader(
            programCall,
            programStageCall,
            programRuleCall,
            trackedEntityTypeCall,
            trackedEntityAttributeCall,
            trackedEntityInstanceFilterCall,
            eventFilterCall,
            programStageWorkingListCall,
            relationshipTypeCall,
            optionSetCall,
            optionCall,
            optionGroupCall,
            organisationUnitProgramLinkLinkStore
        )
    }

    private fun returnEmptyList(call: UidsCall<*>?) {
        whenever(call!!.download(ArgumentMatchers.anySet())).doReturn(Single.just(emptyList()))
    }

    private fun <O> returnSingletonList(call: UidsCall<O>?, o: O) {
        whenever(call!!.download(ArgumentMatchers.anySet())).doReturn(Single.just(listOf(o)))
    }

    private fun returnError(call: UidsCall<*>?) {
        whenever(call!!.download(ArgumentMatchers.anySet())).doReturn(Single.error(RuntimeException()))
    }

    @Test
    fun succeed_when_endpoint_calls_succeed() {
        programModuleDownloader.downloadMetadata().blockingAwait()
    }

    @Test(expected = Exception::class)
    fun fail_when_program_call_fails() {
        returnError(programCall)
        programModuleDownloader.downloadMetadata().blockingAwait()
    }

    @Test(expected = Exception::class)
    fun fail_when_program_stage_call_fails() {
        returnError(programStageCall)
        programModuleDownloader.downloadMetadata().blockingAwait()
    }

    @Test(expected = Exception::class)
    fun fail_when_program_rule_call_fails() {
        returnError(programRuleCall)
        programModuleDownloader.downloadMetadata().blockingAwait()
    }

    @Test(expected = Exception::class)
    fun fail_when_tracked_entity_types_call_fails() {
        returnError(trackedEntityTypeCall)
        programModuleDownloader.downloadMetadata().blockingAwait()
    }

    @Test(expected = Exception::class)
    fun fail_when_tracked_entity_attributes_call_fails() {
        returnError(trackedEntityAttributeCall)
        programModuleDownloader.downloadMetadata().blockingAwait()
    }

    @Test(expected = Exception::class)
    fun fail_when_tracked_entity_instance_filters_call_fails() {
        returnError(trackedEntityInstanceFilterCall)
        programModuleDownloader.downloadMetadata().blockingAwait()
    }

    @Test(expected = Exception::class)
    fun fail_when_event_filters_call_fails() {
        returnError(eventFilterCall)
        programModuleDownloader.downloadMetadata().blockingAwait()
    }

    @Test(expected = Exception::class)
    fun fail_when_program_stage_working_list_call_fails() {
        returnError(programStageWorkingListCall)
        programModuleDownloader.downloadMetadata().blockingAwait()
    }

    @Test(expected = Exception::class)
    fun fail_when_relationship_type_call_fails() {
        whenever(relationshipTypeCall.download()).thenReturn(Single.error(RuntimeException()))
        programModuleDownloader.downloadMetadata().blockingAwait()
    }

    @Test(expected = Exception::class)
    fun fail_when_option_call_fails() {
        returnError(optionCall)
        programModuleDownloader.downloadMetadata().blockingAwait()
    }
}
