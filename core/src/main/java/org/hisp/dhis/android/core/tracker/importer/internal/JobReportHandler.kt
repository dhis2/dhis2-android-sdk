/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.tracker.importer.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectTypes.ENROLLMENT
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectTypes.EVENT
import org.hisp.dhis.android.core.tracker.importer.internal.TrackerImporterObjectTypes.TRACKED_ENTITY

@Reusable
internal class JobReportHandler @Inject internal constructor(
    private val eventHandler: JobReportEventHandler,
    private val enrollmentHandler: JobReportEnrollmentHandler,
    private val trackedEntityHandler: JobReportTrackedEntityHandler
) {

    fun handle(o: JobReport, jobObjects: List<TrackerJobObject>) {
        val jobObjectsMap = jobObjects.groupBy { jo -> Pair(jo.trackerType(), jo.objectUid()) }
        handleErrors(o, jobObjectsMap)
        handleSuccesses(o, jobObjectsMap)
        handleNotPresentObjects(o, jobObjects)
    }

    private fun handleErrors(o: JobReport, jobObjectsMap: Map<Pair<String, String>, List<TrackerJobObject>>) {
        o.validationReport.errorReports.forEach { errorReport ->
            if (jobObjectsMap.containsKey(Pair(errorReport.trackerType, errorReport.uid))) {
                when (errorReport.trackerType) {
                    EVENT -> eventHandler.handleError(errorReport)
                    ENROLLMENT -> enrollmentHandler.handleError(errorReport)
                    TRACKED_ENTITY -> trackedEntityHandler.handleError(errorReport)
                    else -> println("Unsupported type") // TODO
                }
            }
        }
    }

    private fun handleSuccesses(o: JobReport, jobObjectsMap: Map<Pair<String, String>, List<TrackerJobObject>>) {
        if (o.bundleReport != null) {
            val typeMap = o.bundleReport.typeReportMap
            applySuccess(typeMap.event, jobObjectsMap, eventHandler)
            applySuccess(typeMap.enrollment, jobObjectsMap, enrollmentHandler)
            applySuccess(typeMap.trackedEntity, jobObjectsMap, trackedEntityHandler)
        }
    }

    private fun handleNotPresentObjects(
        o: JobReport,
        jobObjectsMap: List<TrackerJobObject>
    ) {
        val presentSuccesses = if (o.bundleReport == null) emptySet<Pair<String, String>>() else {
            val tm = o.bundleReport.typeReportMap
            setOf(tm.event, tm.trackedEntity, tm.enrollment, tm.relationship).flatMap {
                it.objectReports
            }.map { Pair(it.trackerType, it.uid) }
        }

        val presentErrors = o.validationReport.errorReports.map {
            Pair(it.trackerType, it.uid)
        }.toSet()

        val expectedObjects = jobObjectsMap.map { Pair(it.trackerType(), it.objectUid()) }

        val notPresentObjects = expectedObjects - presentSuccesses - presentErrors

        for (p in notPresentObjects) {
            when (p.first) {
                EVENT -> eventHandler.handleObject(p.second, State.TO_UPDATE)
                ENROLLMENT -> enrollmentHandler.handleObject(p.second, State.TO_UPDATE)
                TRACKED_ENTITY -> trackedEntityHandler.handleObject(p.second, State.TO_UPDATE)
                else -> println("Unsupported type") // TODO
            }
        }
    }

    private fun applySuccess(
        typeReport: JobTypeReport,
        jobObjects: Map<Pair<String, String>, List<TrackerJobObject>>,
        typeHandler: JobReportTypeHandler
    ) {
        typeReport.objectReports
            .filter { jobObjects.containsKey(Pair(it.trackerType, it.uid)) }
            .forEach { typeHandler.handleSuccess(it.uid) }
    }
}