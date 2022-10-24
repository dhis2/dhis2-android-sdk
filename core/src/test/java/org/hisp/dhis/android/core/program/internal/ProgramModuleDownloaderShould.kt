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
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hisp.dhis.android.core.arch.call.factories.internal.ListCall
import org.hisp.dhis.android.core.arch.call.factories.internal.UidsCall
import org.hisp.dhis.android.core.arch.db.stores.internal.LinkStore
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.event.EventFilter
import org.hisp.dhis.android.core.option.Option
import org.hisp.dhis.android.core.option.OptionGroup
import org.hisp.dhis.android.core.option.OptionSet
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitProgramLink
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramRule
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType
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
    private val programCall: UidsCall<Program> = mock()
    private val programStageCall: UidsCall<ProgramStage> = mock()
    private val programRuleCall: UidsCall<ProgramRule> = mock()
    private val trackedEntityTypeCall: UidsCall<TrackedEntityType> = mock()
    private val trackedEntityAttributeCall: UidsCall<TrackedEntityAttribute> = mock()
    private val trackedEntityInstanceFilterCall: UidsCall<TrackedEntityInstanceFilter> = mock()
    private val eventFilterCall: UidsCall<EventFilter> = mock()
    private val relationshipTypeCall: ListCall<RelationshipType> = mock()
    private val optionSetCall: UidsCall<OptionSet> = mock()
    private val optionCall: UidsCall<Option> = mock()
    private val optionGroupCall: UidsCall<OptionGroup> = mock()
    private val organisationUnitProgramLinkLinkStore: LinkStore<OrganisationUnitProgramLink> = mock()

    // object to test
    private lateinit var programModuleDownloader: ProgramModuleDownloader

    @Before
    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        errorResponse = Response.error<Any>(
            HttpsURLConnection.HTTP_CLIENT_TIMEOUT,
            ResponseBody.create(MediaType.parse("application/json"), "{}")
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
