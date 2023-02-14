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

package org.hisp.dhis.android.core.fileresource.internal

import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.util.Calendar
import java.util.Date
import org.hisp.dhis.android.core.arch.helpers.FileResourceDirectoryHelper
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.category.CategoryCombo
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.FormType
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.common.ValueType
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.fileresource.FileResource
import org.hisp.dhis.android.core.fileresource.FileResourceDomain
import org.hisp.dhis.android.core.option.OptionSet
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.program.ProgramStage
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType

object FileResourceRoutineSamples {
    private val generator = UidGeneratorImpl()

    private val twoHoursAgo = Calendar.getInstance().apply {
        add(Calendar.HOUR_OF_DAY, -2)
    }
    private val threeHoursAgo = Calendar.getInstance().apply {
        add(Calendar.HOUR_OF_DAY, -3)
    }

    val fileResource1: FileResource by lazy {
        val uid = generator.generate()
        FileResource.builder()
            .uid(uid)
            .lastUpdated(twoHoursAgo.time)
            .domain(FileResourceDomain.DATA_VALUE)
            .path(getFile("first-file").path)
            .build()
    }

    val fileResource2: FileResource by lazy {
        val uid = generator.generate()
        FileResource.builder()
            .uid(uid)
            .lastUpdated(threeHoursAgo.time)
            .domain(FileResourceDomain.DATA_VALUE)
            .path(getFile("second-file").path)
            .build()
    }

    val fileResource3: FileResource by lazy {
        val uid = generator.generate()
        FileResource.builder()
            .uid(uid)
            .lastUpdated(Date())
            .domain(FileResourceDomain.DATA_VALUE)
            .path(getFile("third-file").path)
            .build()
    }

    val orgUnit1: OrganisationUnit by lazy {
        val uid = generator.generate()
        OrganisationUnit.builder()
            .uid(uid)
            .displayName("Child 1")
            .path("/$uid")
            .level(1)
            .build()
    }

    val categoryCombo: CategoryCombo = CategoryCombo.builder()
        .uid(generator.generate())
        .build()

    val dataElement1: DataElement = DataElement.builder()
        .uid(generator.generate())
        .displayName("Data element 1")
        .valueType(ValueType.FILE_RESOURCE)
        .aggregationType(AggregationType.SUM.name)
        .categoryCombo(ObjectWithUid.fromIdentifiable(categoryCombo))
        .build()

    val trackedEntityType: TrackedEntityType = TrackedEntityType.builder()
        .uid(generator.generate())
        .build()

    val program: Program = Program.builder()
        .uid(generator.generate())
        .trackedEntityType(trackedEntityType)
        .categoryCombo(ObjectWithUid.create(categoryCombo.uid()))
        .build()

    val programStage1: ProgramStage = ProgramStage.builder()
        .uid(generator.generate())
        .name("Program stage 1")
        .program(ObjectWithUid.create(program.uid()))
        .formType(FormType.DEFAULT)
        .build()

    val event1: Event = Event.builder()
        .uid(generator.generate())
        .program(program.uid())
        .programStage(programStage1.uid())
        .organisationUnit(orgUnit1.uid())
        .build()

    val trackedEntityDataValue: TrackedEntityDataValue = TrackedEntityDataValue.builder()
        .value(fileResource1.uid())
        .event(event1.uid())
        .dataElement(dataElement1.uid())
        .build()

    val optionSet: OptionSet = OptionSet.builder()
        .uid(generator.generate())
        .build()

    val trackedEntityAttribute: TrackedEntityAttribute = TrackedEntityAttribute.builder()
        .uid(generator.generate())
        .valueType(ValueType.FILE_RESOURCE)
        .optionSet(ObjectWithUid.create(optionSet.uid()))
        .build()

    val trackedEntityInstance: TrackedEntityInstance = TrackedEntityInstance.builder()
        .uid(generator.generate())
        .trackedEntityType(trackedEntityType.uid())
        .build()

    val trackedEntityAttributeValue: TrackedEntityAttributeValue = TrackedEntityAttributeValue.builder()
        .trackedEntityAttribute(trackedEntityAttribute.uid())
        .trackedEntityInstance(trackedEntityInstance.uid())
        .value(fileResource2.uid())
        .build()

    private fun getFile(fileName: String): File {
        val context = InstrumentationRegistry.getInstrumentation().context
        val root = FileResourceDirectoryHelper.getRootFileResourceDirectory(context)
        return File(root, fileName).apply {
            createNewFile()
        }
    }
}
