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

import android.content.Context
import android.os.Debug
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2Configuration
import org.hisp.dhis.android.core.D2Manager
import org.hisp.dhis.android.core.analytics.aggregated.AnalyticsRepository
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalResponse
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.category.CategoryOptionCombo
import org.hisp.dhis.android.core.common.FeatureType
import org.hisp.dhis.android.core.common.Geometry
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.RelativeOrganisationUnit
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
import java.util.Locale
import kotlin.math.ceil
import kotlin.random.Random
import kotlin.system.measureNanoTime
import kotlin.time.Duration.Companion.seconds

/**
 * Every step logs, as soon as it finishes: wall-clock time, time spent in network requests, wall
 * minus network (the part the SDK and the database can affect), and the process PSS and native
 * heap after the step. The realistic scenarios at the end log per-operation latency (p50 / p95).
 */
class PerformanceBenchmark {
    lateinit var d2: D2
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val latencies = mutableMapOf<String, MutableList<Long>>()

    var config: BenchmarkConfiguration? = null

    init {
        try {
            val assets = InstrumentationRegistry.getInstrumentation().context.assets
            assets.open("benchmark.json").use { inputStream ->
                val json = inputStream.bufferedReader().use { it.readText() }
                config = KotlinxJsonParser.instance.decodeFromString<BenchmarkConfiguration>(json)
            }
        } catch (_: FileNotFoundException) {
            // Ignore if missing file
        }
    }

    @After
    fun tearDown() {
        D2Manager.clear()
        latencies.clear()
    }

    @Test
    fun benchmark_sdk() = runTest(timeout = 900.seconds) {
        assumeNotNull(config)

        // Step 1 Instantiate d2
        instantiateD2()

        // Step 2 Login into the server
        login(config!!)
        logDatabaseEncryption()

        // Step 3 Download metadata
        downloadMetadata()

        // Step 4 Download data
        downloadData()

        // Step 5 Perform R/W Random operation
        val dataValues = doRandomAggregationOperations()

        // Step 6 Synchronize data
        uploadData()

        // Analytics
        performAnalytics(dataValues.map { it.second.uid() }.toSet())

        // Step 7 Wipe and download
        wipeDataAndDowload()

        // Step 8 Remove created data values and sync
        deleteData(dataValues)

        // Realistic scenarios. They run last and upload nothing, so the steps above stay
        // comparable with earlier results.
        incrementalSync()
        openVisualizations()
        fillSingleForm()
        doRandomTeiOperations(TRACKER_ENTRY_ITERATIONS)
    }

    private suspend fun runWithTrace(name: String, block: suspend () -> Unit) {
        NetworkTimeTracker.reset()

        val wallTime = measureNanoTime {
            block()
        }
        val networkTime = NetworkTimeTracker.totalNetworkTime

        log(TIME_TAG, name, (wallTime - networkTime) / NANOS_PER_MS, "ms")
        log(WALL_TAG, name, wallTime / NANOS_PER_MS, "ms")
        log(NETWORK_TAG, name, networkTime / NANOS_PER_MS, "ms")
        logMemory(name)
    }

    private fun logMemory(name: String) {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        log(MEMORY_TAG, name, memoryInfo.totalPss / KB_PER_MB, "MB")
        log(NATIVE_HEAP_TAG, name, Debug.getNativeHeapAllocatedSize() / BYTES_PER_MB, "MB")
    }

    private fun log(tag: String, name: String, value: Any, unit: String) {
        Log.d(tag, "$name: $value $unit")
    }

    private fun logDatabaseEncryption() {
        val encrypted = d2.userModule().accountManager().getCurrentAccount()?.encrypted()
        Log.d(INFO_TAG, "Database encrypted: $encrypted")
    }

    private inline fun <T> measure(scenario: String, block: () -> T): T {
        val start = System.nanoTime()
        return block().also {
            latencies.getOrPut(scenario) { mutableListOf() }.add(System.nanoTime() - start)
        }
    }

    private fun logLatencies(scenario: String) {
        val samples = latencies.remove(scenario).orEmpty().sorted()
        log(LATENCY_TAG, "$scenario n", samples.size, "ops")
        if (samples.isNotEmpty()) {
            log(LATENCY_TAG, "$scenario p50", percentile(samples, P50), "ms")
            log(LATENCY_TAG, "$scenario p95", percentile(samples, P95), "ms")
        }
    }

    /** Nearest-rank percentile of [sorted] nanosecond samples, in milliseconds. */
    private fun percentile(sorted: List<Long>, percent: Int): String {
        val index = (ceil(percent / 100.0 * sorted.size).toInt() - 1).coerceIn(0, sorted.lastIndex)
        return String.format(Locale.ROOT, "%.2f", sorted[index].toDouble() / NANOS_PER_MS)
    }

    private suspend fun instantiateD2() {
        runWithTrace("D2 Instantiation") {
            d2 = D2Manager.blockingInstantiateD2(d2Configuration(context))!!
        }
    }

    private suspend fun login(config: BenchmarkConfiguration) {
        runWithTrace("D2 Login") {
            d2.userModule().blockingLogIn(config.username, config.password, config.serverUrl)
        }
    }

    private suspend fun downloadMetadata() {
        runWithTrace("Download metadata") {
            d2.metadataModule().blockingDownload()
        }
    }

    private suspend fun downloadData() {
        runWithTrace("Download data") {
            doDownloadData()
        }
    }

    private fun doDownloadData() {
        d2.trackedEntityModule().trackedEntityInstanceDownloader().blockingDownload()
        d2.eventModule().eventDownloader().blockingDownload()
        d2.aggregatedModule().data().blockingDownload()
        d2.fileResourceModule().fileResourceDownloader().blockingDownload()
        d2.dataStoreModule().dataStoreDownloader().byNamespace().eq("METADATASTORE").blockingDownload()
    }

    /**
     * Tracker entry scenario: creates [iterations] tracked entities with an enrollment and an event,
     * and reports the latency of each SDK call. Nothing is uploaded.
     */
    private fun doRandomTeiOperations(iterations: Int): List<String> {
        val scenario = "Tracker entry operation"
        val createdTeis = mutableListOf<String>()
        val childProgram =
            d2.programModule().programs().byName().eq("Child Programme").blockingGet().first()
        val orgUnit =
            d2.organisationUnitModule().organisationUnits().byUid().eq("DiszpKrYNg8")
                .blockingGet().first()

        for (i in 1..iterations) {
            // Create person with attributes
            val personUid = measure(scenario) {
                d2.trackedEntityModule().trackedEntityInstances().blockingAdd(
                    TrackedEntityInstanceCreateProjection.create(
                        orgUnit.uid(),
                        childProgram.trackedEntityType()!!.uid(),
                    ),
                )
            }
            createdTeis.add(personUid)
            val randomChild = RandomChild.generateChild()

            measure(scenario) {
                d2.trackedEntityModule().trackedEntityAttributeValues()
                    .value(RandomChild.firstNameUid, personUid)
                    .blockingSet(randomChild.firstName)
            }
            measure(scenario) {
                d2.trackedEntityModule().trackedEntityAttributeValues()
                    .value(RandomChild.lastNameUid, personUid)
                    .blockingSet(randomChild.lastName)
            }

            // enroll person in child program with enrollment attributes
            val enrollment = measure(scenario) {
                d2.enrollmentModule().enrollments().blockingAdd(
                    EnrollmentCreateProjection.create(orgUnit.uid(), childProgram.uid(), personUid),
                )
            }
            measure(scenario) {
                d2.enrollmentModule().enrollments().uid(enrollment).setEnrollmentDate(
                    Date.from(Instant.now()),
                )
            }
            measure(scenario) {
                d2.enrollmentModule().enrollments().uid(enrollment)
                    .setIncidentDate(Date.from(Instant.now()))
            }

            measure(scenario) {
                d2.trackedEntityModule().trackedEntityAttributeValues()
                    .value(RandomChild.genderUid, personUid)
                    .blockingSet(randomChild.gender)
            }

            val featureType = measure(scenario) {
                d2.programModule().programs().uid(childProgram.uid()).blockingGet()?.featureType()
            }
            if (featureType == FeatureType.POINT) {
                measure(scenario) {
                    d2.enrollmentModule().enrollments().uid(enrollment).setGeometry(
                        Geometry.builder()
                            .type(FeatureType.POINT)
                            .coordinates(randomChild.coordinates.toString())
                            .build(),
                    )
                }
            }

            // add program stage event and dataValues
            val eventUid = measure(scenario) {
                d2.eventModule().events().blockingAdd(
                    EventCreateProjection.create(
                        enrollment,
                        childProgram.uid(),
                        RandomChild.birthStageUid,
                        orgUnit.uid(),
                        null,
                    ),
                )
            }

            measure(scenario) {
                d2.eventModule().events().uid(eventUid).setEventDate(
                    Date.from(Instant.now()),
                )
            }

            measure(scenario) {
                d2.trackedEntityModule().trackedEntityDataValues()
                    .value(eventUid, RandomChild.weightDeUid)
                    .blockingSet(randomChild.weight.toString())
            }
        }
        logLatencies(scenario)
        return createdTeis
    }

    private suspend fun doRandomAggregationOperations(): List<Triple<Period, ObjectWithUid, CategoryOptionCombo>> {
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
                                dataSet.uid(),
                            ).blockingSet(Random.nextInt(1, 12).toString())
                            createdDV.add(Triple(period, dataSetElement.dataElement(), coc))
                        }
                }
            }
        }
        return createdDV
    }

    /**
     * Single form scenario: fills one period of Child Health value by value, as a user would, and
     * reports the latency of each value set. Nothing is uploaded.
     */
    private fun fillSingleForm() {
        val scenario = "Single form set value"
        val orgUnit = d2.organisationUnitModule().organisationUnits()
            .byUid().eq("DiszpKrYNg8")
            .blockingGet().first()

        val dataSet = d2.dataSetModule().dataSets()
            .byName().eq("Child Health").withDataSetElements()
            .one().blockingGet()!!

        val aoc = d2.categoryModule().categoryOptionCombos()
            .byCategoryComboUid().eq(dataSet.categoryCombo()?.uid())
            .blockingGet().first()

        val period = d2.periodModule().periodHelper()
            .blockingGetPeriodsForDataSet(dataSet.uid())
            .last()

        dataSet.dataSetElements()?.forEach { dataSetElement ->
            val cc = dataSetElement.categoryCombo()
                ?: d2.dataElementModule().dataElements()
                    .uid(dataSetElement.dataElement().uid())
                    .blockingGet()?.categoryCombo()

            d2.categoryModule().categoryOptionCombos().byCategoryComboUid().eq(cc?.uid()).blockingGet()
                .forEach { coc ->
                    measure(scenario) {
                        d2.dataValueModule().dataValues().value(
                            period.periodId()!!,
                            orgUnit.uid(),
                            dataSetElement.dataElement().uid(),
                            coc.uid(),
                            aoc.uid(),
                            dataSet.uid(),
                        ).blockingSet(Random.nextInt(1, 12).toString())
                    }
                }
        }
        logLatencies(scenario)
    }

    private suspend fun uploadData() {
        runWithTrace("Upload data") {
            doUploadData()
        }
    }

    private fun doUploadData() {
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload()
        d2.dataSetModule().dataSetCompleteRegistrations().blockingUpload()
        d2.dataValueModule().dataValues().blockingUpload()
        d2.dataStoreModule().dataStore().blockingUpload()
    }

    private suspend fun performAnalytics(
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

    /**
     * Open visualization scenario: evaluates visualization-shaped analytics queries right after a
     * sync (cold) and then again (warm), and reports the latency of each successful evaluation.
     * The queries are built from downloaded metadata instead of the server's visualizations, so the
     * scenario does not depend on the Android Settings app configuration. The selection is
     * deterministic, so every run evaluates the same queries.
     */
    private fun openVisualizations() {
        val queries = visualizationQueries()

        listOf("Open visualization cold", "Open visualization warm").forEach { scenario ->
            var failures = 0
            queries.forEach { query ->
                val start = System.nanoTime()
                val result = query.blockingEvaluate()
                if (result is Result.Success) {
                    latencies.getOrPut(scenario) { mutableListOf() }.add(System.nanoTime() - start)
                } else {
                    failures++
                }
            }
            logLatencies(scenario)
            log(LATENCY_TAG, "$scenario failed", failures, "ops")
        }
    }

    /**
     * Five typical visualization shapes: a pivot table of a form, a line chart, a column chart by
     * org unit, an indicator chart and a pivot table by category option combo.
     */
    private fun visualizationQueries(): List<AnalyticsRepository> {
        val dataSet = d2.dataSetModule().dataSets()
            .byName().eq("Child Health").withDataSetElements()
            .one().blockingGet()!!
        val dataElements = dataSet.dataSetElements().orEmpty().map { it.dataElement().uid() }.sorted()
        val indicators = d2.indicatorModule().indicators().blockingGetUids().sorted()
        val operands = dataElements.take(QUERY_SMALL).mapNotNull { dataElement ->
            val categoryCombo = d2.dataElementModule().dataElements().uid(dataElement).blockingGet()?.categoryCombo()
            d2.categoryModule().categoryOptionCombos().byCategoryComboUid().eq(categoryCombo?.uid())
                .blockingGetUids().minOrNull()
                ?.let { DimensionItem.DataItem.DataElementOperandItem(dataElement, it) }
        }

        fun query(
            data: List<DimensionItem.DataItem>,
            vararg others: DimensionItem,
            filters: List<DimensionItem> = emptyList(),
        ): AnalyticsRepository? {
            if (data.isEmpty()) return null
            var repository = d2.analyticsModule().analytics()
            (data + others).forEach { repository = repository.withDimension(it) }
            filters.forEach { repository = repository.withFilter(it) }
            return repository
        }

        val userOrgUnit = DimensionItem.OrganisationUnitItem.Relative(RelativeOrganisationUnit.USER_ORGUNIT)
        return listOfNotNull(
            query(
                dataElements.take(QUERY_LARGE).map { DimensionItem.DataItem.DataElementItem(it) },
                DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_12_MONTHS),
                userOrgUnit,
            ),
            query(
                dataElements.take(QUERY_SMALL).map { DimensionItem.DataItem.DataElementItem(it) },
                DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_12_MONTHS),
                filters = listOf(userOrgUnit),
            ),
            query(
                dataElements.take(2).map { DimensionItem.DataItem.DataElementItem(it) },
                DimensionItem.OrganisationUnitItem.Relative(RelativeOrganisationUnit.USER_ORGUNIT_CHILDREN),
                filters = listOf(DimensionItem.PeriodItem.Relative(RelativePeriod.THIS_YEAR)),
            ),
            query(
                indicators.take(QUERY_SMALL).map { DimensionItem.DataItem.IndicatorItem(it) },
                DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_4_QUARTERS),
                userOrgUnit,
            ),
            query(
                operands,
                DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_6_MONTHS),
                userOrgUnit,
            ),
        )
    }

    private suspend fun incrementalSync() {
        runWithTrace("Incremental sync") {
            d2.metadataModule().blockingDownload()
            doDownloadData()
        }
    }

    private suspend fun wipeDataAndDowload() {
        runWithTrace("Wipe data and dowload again") {
            d2.wipeModule().wipeData()
            doDownloadData()
        }
    }

    private suspend fun deleteData(
        dataValues: List<Triple<Period, ObjectWithUid, CategoryOptionCombo>>,
    ) {
        runWithTrace("Delete data and push changes") {
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
                    dataSet!!.uid(),
                ).blockingDelete()
            }

            doUploadData()
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

    private companion object {
        const val TIME_TAG = "SDKPerformanceAnalysisTime"
        const val WALL_TAG = "SDKPerformanceAnalysisWall"
        const val NETWORK_TAG = "SDKPerformanceAnalysisNetwork"
        const val MEMORY_TAG = "SDKPerformanceAnalysisMemory"
        const val NATIVE_HEAP_TAG = "SDKPerformanceAnalysisNativeHeap"
        const val LATENCY_TAG = "SDKPerformanceAnalysisLatency"
        const val INFO_TAG = "SDKPerformanceAnalysisInfo"

        const val NANOS_PER_MS = 1_000_000L
        const val KB_PER_MB = 1024
        const val BYTES_PER_MB = 1024L * 1024L
        const val P50 = 50
        const val P95 = 95

        const val TRACKER_ENTRY_ITERATIONS = 20
        const val QUERY_LARGE = 10
        const val QUERY_SMALL = 3
    }
}
