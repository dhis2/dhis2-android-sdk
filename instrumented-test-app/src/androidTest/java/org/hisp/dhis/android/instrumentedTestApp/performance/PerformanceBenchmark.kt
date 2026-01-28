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

package org.hisp.dhis.android.instrumentedTestApp.performance

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2Configuration
import org.hisp.dhis.android.core.D2Manager
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalResponse
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.enrollment.EnrollmentCreateProjection
import org.hisp.dhis.android.core.event.EventCreateProjection
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceCreateProjection
import org.junit.After
import org.junit.Assume.assumeNotNull
import org.junit.Test
import java.io.FileNotFoundException
import java.time.Instant
import java.util.Date
import kotlin.random.Random
import kotlin.system.measureNanoTime

class PerformanceBenchmark {
    lateinit var d2: D2
    val elapsedTimes = mutableMapOf<String, Long>()
    val freeMemory = mutableMapOf<String, Long>()
    val context = InstrumentationRegistry.getInstrumentation().targetContext

    var config: BenchmarkConfiguration? = null

    init {
        try {
            val assets = InstrumentationRegistry.getInstrumentation().context.assets
            assets.open("benchmark.json").use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                config = KotlinxJsonParser.instance.decodeFromString<BenchmarkConfiguration>(json)
            }
        } catch (_: FileNotFoundException) {
            print("")
            // Ignore if missing file
        }
    }

    @After
    fun tearDown() {
        D2Manager.clear()
        elapsedTimes.clear()
        freeMemory.clear()
    }

    @Test
    fun benchmark_sdk() {
        assumeNotNull(config)

        val initialMemory = getMemoryusage(context)

        // Step 1 Instantiate d2
        instantiateD2()

        // Step 2 Login into the server
        login(config!!)

        // Step 3 Download metadata
        downloadMetadata()

        // Step 4 Download data
        downloadData()

        // Step 5 Perform R/W Random operation
        val createdTeis = emptyList<String>() // doRandomTeiOperations()
        val dataValues = doRandomAggregationOperations()

        // Step 6 Synchronize data
        uploadData()

        // Analytics
        performAnalytics(dataValues.map { it.second.uid() }.toSet())

//         Step 7 Wipe and download
        wipeDataAndDowload()

        // Step 8 Remove created tracked entity instances and sync
        deleteData(createdTeis, dataValues)

        // Step 7 Finalize test
//        finalizeTest()

        elapsedTimes.forEach { (step, dt) ->
            Log.d("SDKPerformanceAnalysisTime", "$step: ${dt / 1_000_000} ms")
        }
        freeMemory.forEach { (step, mem) ->
            Log.d("SDKPerformanceAnalysisMemory", "$step: ${initialMemory - mem} MB")
        }
    }

    private fun runWithTrace(name: String, block: () -> Unit) {
        NetworkTimeTracker.totalNetworkTime = 0L

        val totalElapsedTime = measureNanoTime {
            block()
        }
        elapsedTimes[name] = totalElapsedTime - NetworkTimeTracker.totalNetworkTime
        freeMemory[name] = getMemoryusage(context)
    }

    fun getMemoryusage(context: Context): Long {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.availMem / 1024 / 1024 // in MB
    }

    private fun instantiateD2() {
        runWithTrace("D2 Instantiation") {
            d2 = D2Manager.blockingInstantiateD2(d2Configuration(context))!!
        }
    }

    private fun login(config: BenchmarkConfiguration) {
        runWithTrace("D2 Login") {
            d2.userModule().blockingLogIn(config.username, config.password, config.serverUrl)
        }
    }

    private fun downloadMetadata() {
        runWithTrace("Download metadata") {
            d2.metadataModule().blockingDownload()
        }
    }

    private fun downloadData() {
        runWithTrace("Download data") {
            d2.trackedEntityModule().trackedEntityInstanceDownloader().blockingDownload()
            d2.eventModule().eventDownloader().blockingDownload()
            d2.aggregatedModule().data().blockingDownload()
            d2.fileResourceModule().fileResourceDownloader().blockingDownload()
            d2.dataStoreModule().dataStoreDownloader().byNamespace().eq("METADATASTORE").blockingDownload()
        }
    }

    private fun doRandomTeiOperations(): List<String> {
        var createdTeis = mutableListOf<String>()
        runWithTrace("Random operations") {
            val childProgram =
                d2.programModule().programs().byName().eq("Child Programme").blockingGet().first()
            val orgUnit =
                d2.organisationUnitModule().organisationUnits().byUid().eq("DiszpKrYNg8")
                    .blockingGet().first()

            for (i in 1..200) {
                // Create person with attributes
                val personUid = d2.trackedEntityModule().trackedEntityInstances().blockingAdd(
                    TrackedEntityInstanceCreateProjection.create(
                        orgUnit.uid(),
                        childProgram.trackedEntityType()!!.uid(),
                    ),
                )
                createdTeis.add(personUid)
                val randomChild = RandomChild.generateChild()

                d2.trackedEntityModule().trackedEntityAttributeValues()
                    .value(RandomChild.firstNameUid, personUid)
                    .blockingSet(randomChild.firstName)
                d2.trackedEntityModule().trackedEntityAttributeValues()
                    .value(RandomChild.lastNameUid, personUid)
                    .blockingSet(randomChild.lastName)

                // enroll person in child program with enrollment attributes
                val enrollment = d2.enrollmentModule().enrollments().blockingAdd(
                    EnrollmentCreateProjection.create(orgUnit.uid(), childProgram.uid(), personUid),
                )
                d2.enrollmentModule().enrollments().uid(enrollment).setEnrollmentDate(
                    Date.from(Instant.now()),
                )
                d2.enrollmentModule().enrollments().uid(enrollment)
                    .setIncidentDate(Date.from(Instant.now()))

                d2.trackedEntityModule().trackedEntityAttributeValues()
                    .value(RandomChild.genderUid, personUid)
                    .blockingSet(randomChild.gender)

                if (d2.programModule().programs().uid(childProgram.uid()).blockingGet()
                        ?.featureType() == FeatureType.POINT
                ) {
                    d2.enrollmentModule().enrollments().uid(enrollment).setGeometry(
                        Geometry.builder()
                            .type(FeatureType.POINT)
                            .coordinates(randomChild.coordinates.toString())
                            .build(),
                    )
                }

                // add program stage event and dataValues
                val eventUid = d2.eventModule().events().blockingAdd(
                    EventCreateProjection.create(
                        enrollment,
                        childProgram.uid(),
                        RandomChild.birthStageUid,
                        orgUnit.uid(),
                        null,
                    ),
                )

                d2.eventModule().events().uid(eventUid).setEventDate(
                    Date.from(Instant.now()),
                )

                d2.trackedEntityModule().trackedEntityDataValues()
                    .value(eventUid, RandomChild.weightDeUid)
                    .blockingSet(randomChild.weight.toString())
            }
        }
        return createdTeis
    }

    private fun doRandomAggregationOperations(): List<Triple<Period, ObjectWithUid, CategoryOptionCombo>> {
        var createdDV = mutableListOf<Triple<Period, ObjectWithUid, CategoryOptionCombo>>()

        runWithTrace("Random dataset operations") {
            val orgUnit = d2.organisationUnitModule().organisationUnits()
                .byUid().eq("DiszpKrYNg8")
                .blockingGet().first()

            val dataSet = d2.dataSetModule().dataSets()
                .byName().eq("Child Health").withDataSetElements()
                .one().blockingGet()

            val aoc = d2.categoryModule().categoryOptionCombos()
                .byCategoryComboUid().eq(dataSet?.categoryCombo()?.uid())
                .blockingGet().first()

            val periods = d2.periodModule().periodHelper()
                .blockingGetPeriodsForDataSet(dataSet?.uid()!!)

            periods.map { period ->
                dataSet.dataSetElements()?.forEach { dataSetElement ->
                    val cc = dataSetElement.categoryCombo()
                        ?: d2.dataElementModule().dataElements()
                            .uid(dataSetElement.dataElement().uid())
                            .blockingGet()?.categoryCombo()

                    d2.categoryModule().categoryOptionCombos().byCategoryComboUid().eq(cc?.uid()).blockingGet()
                        .map { coc ->
                            d2.dataValueModule().dataValues().value(
                                period.periodId()!!,
                                orgUnit.uid(),
                                dataSetElement.dataElement().uid(),
                                coc.uid(),
                                aoc.uid(),
                            ).blockingSet(Random.nextInt(1, 12).toString())
                            createdDV.add(Triple(period, dataSetElement.dataElement(), coc))
                        }
                }
            }
        }
        return createdDV
    }

    private fun uploadData() {
        runWithTrace("Upload data") {
            d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
            d2.dataSetModule().dataSetCompleteRegistrations().blockingUpload()
            d2.dataValueModule().dataValues().blockingUpload()
            d2.dataStoreModule().dataStore().blockingUpload()
        }
    }

    private fun performAnalytics(
        dataElementSet: Set<String>,
    ): DimensionalResponse? {
        var result: DimensionalResponse? = null
        runWithTrace("Analytics") {
            var analytics = d2.analyticsModule().analytics()
            dataElementSet.forEach { dataElement ->
                analytics = analytics.withDimension(
                    DimensionItem.DataItem.DataElementItem(dataElement),
                )
            }
            result = analytics
                .withDimension(DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_12_MONTHS))
                .blockingEvaluate()
                .getOrThrow()
        }
        return result
    }

    private fun wipeDataAndDowload() {
        runWithTrace("Wipe data and dowload again") {
            d2.wipeModule().wipeData()
            downloadData()
        }
    }

    private fun deleteData(
        createdTeis: List<String>,
        dataValues: List<Triple<Period, ObjectWithUid, CategoryOptionCombo>>,
    ) {
        runWithTrace("Delete data and push changes") {
            createdTeis.forEach {
                d2.trackedEntityModule().trackedEntityInstances().uid(it).blockingDelete()
            }

            val orgUnit = d2.organisationUnitModule().organisationUnits()
                .byUid().eq("DiszpKrYNg8")
                .blockingGet().first()

            val dataSet = d2.dataSetModule().dataSets()
                .byName().eq("Child Health")
                .one().blockingGet()

            val aoc = d2.categoryModule().categoryOptionCombos()
                .byCategoryComboUid().eq(dataSet?.categoryCombo()?.uid())
                .blockingGet().first()

            dataValues.forEach { (period, dataElement, categoryOptionCombo) ->
                d2.dataValueModule().dataValues().value(
                    period.periodId()!!,
                    orgUnit.uid(),
                    dataElement.uid(),
                    categoryOptionCombo.uid(),
                    aoc.uid(),
                ).blockingDelete()
            }

            uploadData()
        }
    }

    private fun d2Configuration(context: Context): D2Configuration {
        return D2Configuration.builder()
            .appVersion("1.0.0")
            .readTimeoutInSeconds(30)
            .connectTimeoutInSeconds(30)
            .writeTimeoutInSeconds(30)
            .interceptors(emptyList())
            .networkInterceptors(listOf(IgnoreIOTimeInterceptor()))
            .context(context)
            .build()
    }
}
